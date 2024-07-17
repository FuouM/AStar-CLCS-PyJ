import numpy as np
from datastructure import (
    Node,
    Pv_Uv,
)
from astar_two_optimized import (
    two_feasible_is_overshoot,
    two_is_dominated_by_other,
    two_is_feasible,
    two_is_label_absent,
)

INT_MAX = np.iinfo(np.int32).max


def is_node_dominated(v: Node, v_rel: tuple[int, int]) -> bool:
    l_rel = v_rel[0]
    u_rel = v_rel[1]
    cond1 = l_rel > v.l_v and u_rel >= v.u_v
    cond2 = l_rel == v.l_v and u_rel > v.u_v

    if cond1 or cond2:
        return True
    return False


def feasible_is_overshoot(
    num_inputs: int, pos_vec: tuple[int, ...], input_lens: list[int]
) -> bool:
    is_overshoot = False
    if num_inputs == 2:
        is_overshoot = two_feasible_is_overshoot(pos_vec, input_lens)
    else:
        for i in range(num_inputs):
            if pos_vec[i] > input_lens[i]:
                is_overshoot = True
                break

    return is_overshoot


def is_label_absent(
    label: int,
    num_inputs: int,
    pos_vec: tuple[int, ...],
    successor_tables: np.ndarray,
    label_blacklist: set,
) -> bool:
    vector = (label, pos_vec)
    if vector in label_blacklist:
        return True

    is_absent = False
    if num_inputs == 2:
        is_absent = two_is_label_absent(label, pos_vec, successor_tables)
    else:
        for i in range(num_inputs):
            if get_successor_label(i, label, pos_vec, successor_tables) == INT_MAX:
                is_absent = True
                break

    if is_absent:
        label_blacklist.add(vector)

    return is_absent


def is_dominated_by_other(
    label: int,
    label_other: int,
    num_inputs: int,
    pos_vec: tuple[int, ...],
    successor_tables: np.ndarray,
) -> bool:
    if num_inputs == 2:
        return two_is_dominated_by_other(label, label_other, pos_vec, successor_tables)
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
    pos_vec: tuple[int, ...],
    successor_tables: np.ndarray,
    label_blacklist: set,
) -> bool:
    if is_label_absent(label, num_inputs, pos_vec, successor_tables, label_blacklist):
        return True
    vector = (label, pos_vec)
    if vector in label_blacklist:
        return False
    for label_other in range(sigma_len):
        if label_other == label:
            continue

        if is_dominated_by_other(
            label, label_other, num_inputs, pos_vec, successor_tables
        ):
            label_blacklist.add(vector)
            return True

    return False


def get_sigma_nd(
    num_inputs: int,
    sigma_len: int,
    pos_vec: tuple[int, ...],
    successor_tables: np.ndarray,
    label_blacklist: set,
) -> list[int]:
    sigma_nd: list[int] = []

    for label in range(sigma_len):
        is_dommed = is_label_dominated(
            label, num_inputs, sigma_len, pos_vec, successor_tables, label_blacklist
        )
        if (label, pos_vec) not in label_blacklist or not is_dommed:
            sigma_nd.append(label)

    return sigma_nd


def create_pv_uv_from_label(
    label: int,
    num_inputs: int,
    u_v: int,
    pos_vec: tuple[int, ...],
    successor_tables: np.ndarray,
) -> Pv_Uv:
    new_pv = [
        get_successor_label(i, label, pos_vec, successor_tables) + 2
        for i in range(num_inputs)
    ]

    return Pv_Uv(new_pv, u_v)


def get_successor_label(
    i: int, label: int, pos_vec: tuple[int, ...], successor_tables: np.ndarray
) -> int:
    return successor_tables[i][label, pos_vec[i] - 1]


def is_feasible(
    label: int,
    num_inputs: int,
    u_v: int,
    pos_vec: tuple[int, ...],
    successor_tables: np.ndarray,
    embed_tables: np.ndarray,
):
    if num_inputs == 2:
        return two_is_feasible(label, u_v, pos_vec, successor_tables, embed_tables)
    for i in range(num_inputs):
        label_succ_idx = get_successor_label(i, label, pos_vec, successor_tables)
        if label_succ_idx > embed_tables[i][u_v]:
            return False

    return True
