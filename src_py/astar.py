from datastructure import (
    Node,
    Pv_Uv,
    AStar_Solution,
    CLCS,
    # MaxPriorityQueueOptimized,
    # MaxPriorityQueueOptimizedSecond,
    MaxPriorityQueueOptimizedThird,
)
from astar_utils import (
    create_pv_uv_from_label,
    feasible_is_overshoot,
    get_sigma_nd,
    is_feasible,
    is_node_dominated,
)
from upperbound import ub_both


def astar_run(inst: CLCS):
    astar_sol = AStar_Solution.create()
    # Q_p = MaxPriorityQueueOptimized()
    # Q_p = MaxPriorityQueueOptimizedSecond()
    Q_p = MaxPriorityQueueOptimizedThird()
    N_v: dict[tuple[int, ...], list[tuple[int, int]]] = dict()
    label_blacklist: set = set()
    Node.set_pv_length(inst.num_inputs)
    root = Node.create()

    expanded = 0

    Q_p.push(root, ub_both(inst, root))

    while not Q_p.is_empty():
        v: Node = Q_p.pop()
        N_v_relative: list[tuple[int, int]] | None = N_v.get(v.pv)

        if N_v_relative is not None and check_is_outdated(v, N_v_relative):
            continue

        insert_new_to_nv(N_v, v)

        v_nd = get_feasible_non_dominated_extensions(inst, v, label_blacklist)
        expanded += 1

        if not v_nd:
            sol = derive_solution(v, inst.inputs[0])
            astar_sol.solutions.append(sol)
            astar_sol.expandeds.append(expanded)
            # print(f"Expanded {expanded}")
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
                # assert N_v_relative is not None
                to_remove: list[tuple[int, int]] = []
                do_insert = is_ext_not_dominated(
                    N_v_relative,  # type: ignore
                    v_ext_l,
                    v_ext_u,
                    to_remove,
                )

                for v_rel in to_remove:
                    # Q_p.remove_node(v_ext_pv_tuple, v_rel[0], v_rel[1])
                    Q_p.remove_node((v_ext_pv_tuple, v_rel[0], v_rel[1]))
                    N_v_relative.remove(v_rel)  # type: ignore

            if do_insert:
                v_ext_l_u = (v_ext_l, v_ext_u)
                v_ext_node = Node(v_ext_pv_tuple, v_ext_l, v_ext_u, v)
                v_ext_ub = ub_both(inst, v_ext_node)
                if is_vext_visited:
                    # assert N_v_relative is not None
                    N_v_relative.insert(0, v_ext_l_u)  # type: ignore
                else:
                    insert_new_to_nv(N_v, v_ext_node)
                Q_p.push(v_ext_node, v_ext_ub)

    return astar_sol


def insert_new_to_nv(
    N_v: dict[tuple[int, ...], list[tuple[int, int]]], v: Node
) -> None:
    N_v[v.get_pv()] = [v.get_l_u()]


def check_is_outdated(v: Node, N_v_relative: list[tuple[int, int]]) -> bool:
    outdated = False
    for v_rel in N_v_relative:
        outdated = is_node_dominated(v, v_rel)
        if outdated:
            break
    return outdated


def derive_solution(v: Node, first_input: list[int]) -> list[int]:
    solution: list[int] = []
    while v.parent is not None:
        solution.insert(0, first_input[v.pv[0] - 2])
        v = v.parent
    return solution


def get_feasible_non_dominated_extensions(
    inst: CLCS, v: Node, label_blacklist: set
) -> list[Pv_Uv]:
    if feasible_is_overshoot(inst.num_inputs, v.pv, inst.input_lens):
        return []

    sigma_nd = get_sigma_nd(
        inst.num_inputs, inst.sigma_len, v.pv, inst.successor_tables, label_blacklist
    )

    feasibles: list[Pv_Uv] = []
    if v.u_v == inst.constraint_len:
        feasibles = [
            create_pv_uv_from_label(
                label, inst.num_inputs, v.u_v, v.pv, inst.successor_tables
            )
            for label in sigma_nd
        ]
        return feasibles

    for label in sigma_nd:
        vector = (label, v.pv)
        if vector in label_blacklist:
            continue

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
