import numpy as np

INT_MAX = np.iinfo(np.int32).max

IDX_I, IDX_J = 0, 1


def two_feasible_is_overshoot(pos_vec: tuple[int, int], input_lens: tuple[int, int]):
    return pos_vec[IDX_I] > input_lens[IDX_I] or pos_vec[IDX_J] > input_lens[IDX_J]


def two_is_label_absent(
    label: int, pos_vec: tuple[int, int], successor_tables: np.ndarray
):
    return (
        successor_tables[IDX_I][label, pos_vec[IDX_I] - 1] == INT_MAX
        or successor_tables[IDX_J][label, pos_vec[IDX_J] - 1] == INT_MAX
    )


def two_is_dominated_by_other(
    label: int,
    label_other: int,
    pos_vec: tuple[int, int],
    successor_tables: np.ndarray,
):
    return (
        successor_tables[IDX_I][label, pos_vec[IDX_I] - 1]
        >= successor_tables[IDX_I][label_other, pos_vec[IDX_I] - 1]
    ) and (
        successor_tables[IDX_J][label, pos_vec[IDX_J] - 1]
        >= successor_tables[IDX_J][label_other, pos_vec[IDX_J] - 1]
    )


def two_is_feasible(
    label: int,
    u_v: int,
    pos_vec: tuple[int, int],
    successor_tables: np.ndarray,
    embed_tables: np.ndarray,
):
    return (
        successor_tables[IDX_I][label, pos_vec[IDX_I] - 1] <= embed_tables[IDX_I][u_v]
    ) and (
        successor_tables[IDX_J][label, pos_vec[IDX_J] - 1] <= embed_tables[IDX_J][u_v]
    )


def two_ub_min_occurrence(
    sigma_len: int,
    pos_vec: tuple[int, int],
    input_lens: tuple[int, int],
    occurrence_tables: np.ndarray,
):
    ub = 0
    for label in range(sigma_len):
        I_min = (
            0
            if pos_vec[IDX_I] > input_lens[IDX_I]
            else occurrence_tables[IDX_I][label, pos_vec[IDX_I] - 1]
        )
        J_min = (
            0
            if pos_vec[IDX_J] > input_lens[IDX_J]
            else occurrence_tables[IDX_J][label, pos_vec[IDX_J] - 1]
        )
        ub += min(I_min, J_min)
    return ub
