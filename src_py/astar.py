import numpy as np

from datastructure import (
    Node,
    Pv_Uv,
    AStar_Solution,
    CLCS,
    MaxPriorityQueue,
)
from upperbound import ub_both

INT_MAX = np.iinfo(np.int32).max


def check_is_outdated(v: Node, N_v_relative: list[tuple[int, int]]) -> bool:
    outdated = False
    for v_rel in N_v_relative:
        outdated = _is_outdated(v, v_rel)
        if outdated:
            break
    return outdated


def _is_outdated(v: Node, v_rel: tuple[int, int]) -> bool:
    l_rel = v_rel[0]
    u_rel = v_rel[1]
    cond1 = l_rel > v.l_v and u_rel >= v.u_v
    cond2 = l_rel == v.l_v and u_rel > v.u_v

    if cond1 or cond2:
        return True
    return False


def insert_new_to_nv(
    N_v: dict[tuple[int, ...], list[tuple[int, int]]], v: Node
) -> None:
    N_v[v.get_pv()] = [v.get_l_u()]

# @njit(parallel=True)
def feasible_is_overshoot(
    num_inputs: int, pos_vec: list[int], input_lens: list[int]
) -> bool:
    for i in range(num_inputs):
        if pos_vec[i] > input_lens[i]:
            return True
    return False


def is_label_absent(
    label: int, num_inputs: int, pos_vec: list[int], successor_tables: np.ndarray
) -> bool:
    for i in range(num_inputs):
        label_succ_idx = get_successor_label(i, label, pos_vec, successor_tables)
        if label_succ_idx == INT_MAX:
            return True
    return False


def is_dominated_by_other(
    label: int,
    label_other: int,
    num_inputs: int,
    pos_vec: list[int],
    successor_tables: np.ndarray,
) -> bool:
    for i in range(num_inputs):
        label_succ_idx = get_successor_label(i, label, pos_vec, successor_tables)
        other_succ_idx = get_successor_label(i, label_other, pos_vec, successor_tables)
        if label_succ_idx < other_succ_idx:
            return False
    return True


def is_label_dominated(
    label: int,
    num_inputs: int,
    sigma_len: int,
    pos_vec: list[int],
    successor_tables: np.ndarray,
) -> bool:
    if is_label_absent(label, num_inputs, pos_vec, successor_tables):
        return True

    for label_other in range(sigma_len):
        if label_other == label:
            continue

        if is_dominated_by_other(
            label, label_other, num_inputs, pos_vec, successor_tables
        ):
            return True

    return False


def get_sigma_nd(
    num_inputs: int, sigma_len: int, pos_vec: list[int], successor_tables: np.ndarray
) -> list[int]:
    sigma_nd: list[int] = []

    for label in range(sigma_len):
        if not is_label_dominated(
            label, num_inputs, sigma_len, pos_vec, successor_tables
        ):
            sigma_nd.append(label)

    return sigma_nd


def create_pv_uv_from_label(
    label: int,
    num_inputs: int,
    u_v: int,
    pos_vec: list[int],
    successor_tables: np.ndarray,
) -> Pv_Uv:
    new_pv = [1] * num_inputs

    for i in range(num_inputs):
        label_succ_idx = get_successor_label(i, label, pos_vec, successor_tables)
        new_pv[i] = label_succ_idx + 2

    return Pv_Uv(new_pv, u_v)


def get_successor_label(
    i: int, label: int, pos_vec: list[int], successor_tables: np.ndarray
) -> int:
    return successor_tables[i][label, pos_vec[i] - 1]


def is_feasible(
    label: int,
    num_inputs: int,
    u_v: int,
    pos_vec: list[int],
    successor_tables: np.ndarray,
    embed_tables: np.ndarray,
):
    for i in range(num_inputs):
        label_succ_idx = get_successor_label(i, label, pos_vec, successor_tables)
        if label_succ_idx > embed_tables[i][u_v]:
            return False

    return True


def get_feasible_non_dominated_extensions(inst: CLCS, v: Node) -> list[Pv_Uv]:
    feasibles: list[Pv_Uv] = []

    if feasible_is_overshoot(inst.num_inputs, v.pv, inst.input_lens):
        return feasibles

    sigma_nd = get_sigma_nd(
        inst.num_inputs, inst.sigma_len, v.pv, inst.successor_tables
    )

    if v.u_v == inst.constraint_len:
        for label in sigma_nd:
            feasibles.append(
                create_pv_uv_from_label(
                    label, inst.num_inputs, v.u_v, v.pv, inst.successor_tables
                )
            )
        return feasibles

    for label in sigma_nd:
        is_next_constraint = label == inst.constraint[v.u_v]

        if not is_next_constraint and not is_feasible(
            label,
            inst.num_inputs,
            v.u_v,
            v.pv,
            inst.successor_tables,
            inst.embed_tables,
        ):
            continue

        new_uv = v.u_v + 1 if is_next_constraint else v.u_v
        feasibles.append(
            create_pv_uv_from_label(
                label, inst.num_inputs, new_uv, v.pv, inst.successor_tables
            )
        )

    return feasibles


def derive_solution(v: Node, first_input: list[int]) -> list[int]:
    solution: list[int] = []
    while v.parent is not None:
        solution.insert(0, first_input[v.pv[0] - 2])
        v = v.parent
    return solution

def is_ext_not_dominated(
    N_v_relative: list[tuple[int, int]],
    vExtendedL: int,
    vExtendedU: int,
    to_remove: list[tuple[int, int]],
):
    do_insert = True

    for vRelative in N_v_relative:
        vRelativeL = vRelative[0]
        vRelativeU = vRelative[1]

        L_relativeEQ = vRelativeL == vExtendedL
        U_relativeEQ = vRelativeU == vExtendedU

        L_relativeChosen = (vRelativeL > vExtendedL) or L_relativeEQ
        U_relativeChosen = (vRelativeU > vExtendedU) or U_relativeEQ

        L_relativeReject = (vRelativeL < vExtendedL) or L_relativeEQ
        U_relativeReject = (vRelativeU < vExtendedU) or U_relativeEQ

        if L_relativeChosen and U_relativeChosen:
            do_insert = False
            break
        if L_relativeReject and U_relativeReject:
            to_remove.append(vRelative)

    return do_insert


def astar_run(inst: CLCS):
    astar_sol = AStar_Solution.create()
    Q_p = MaxPriorityQueue()
    N_v: dict[tuple[int, ...], list[tuple[int, int]]] = dict()

    root = Node.create(inst.num_inputs)

    expanded = 0

    Q_p.push(root, ub_both(inst, root))

    while not Q_p.is_empty():
        v: Node = Q_p.pop()
        v_pv = v.get_pv()
        N_v_relative: list[tuple[int, int]] | None = N_v.get(v_pv)

        if N_v_relative is not None and check_is_outdated(v, N_v_relative):
            continue

        insert_new_to_nv(N_v, v)

        v_nd = get_feasible_non_dominated_extensions(inst, v)
        expanded += 1

        if not v_nd:
            sol = derive_solution(v, inst.inputs[0])
            astar_sol.solutions.append(sol)
            astar_sol.expandeds.append(expanded)
            break
        for pv_uv in v_nd:
            v_ext_l = v.l_v + 1
            v_ext_u = pv_uv.u_v
            v_ext_pv = pv_uv.pv

            v_ext_pv_tuple = tuple(v_ext_pv)

            do_insert = True

            N_v_relative = N_v.get(v_ext_pv_tuple)
            is_vext_visited = N_v_relative is not None

            if is_vext_visited:
                assert N_v_relative is not None
                to_remove: list[tuple[int, int]] = []
                do_insert = is_ext_not_dominated(
                    N_v_relative, v_ext_l, v_ext_u, to_remove
                )

                for v_rel in to_remove:
                    Q_p.remove_node(v_ext_pv, v_rel[0], v_rel[1])
                    N_v_relative.remove(v_rel)

            if do_insert:
                v_ext_l_u = (v_ext_l, v_ext_u)
                v_ext_node = Node(v_ext_pv, v_ext_l, v_ext_u, v)
                v_ext_ub = ub_both(inst, v_ext_node)
                if is_vext_visited:
                    assert N_v_relative is not None
                    N_v_relative.append(v_ext_l_u)
                else:
                    insert_new_to_nv(N_v, v_ext_node)
                Q_p.push(v_ext_node, v_ext_ub)

    return astar_sol
