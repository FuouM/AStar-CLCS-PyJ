import numpy as np

try:
    from accelerated import (
        build_embed_numba,
        build_occurrence_numba,
        build_successor_numba,
        lcs_mscore_ij_numba,
    )

    NUMBA_AVAILABLE = True

except ImportError:
    NUMBA_AVAILABLE = False

print(f"{NUMBA_AVAILABLE=}")

INT_MAX = np.iinfo(np.int32).max


def _build_successor_python(
    sigma_length: int, max_length: int, input_sequence: list[int]
) -> np.ndarray:
    successor_table = np.full((sigma_length, max_length), INT_MAX, dtype=np.int32)

    for label in range(sigma_length):
        number = INT_MAX
        for i in range(len(input_sequence) - 1, -1, -1):
            if input_sequence[i] == label:
                number = i
            successor_table[label, i] = number

    return successor_table


def build_successor(
    sigma_length: int, max_length: int, input_sequence: list[int], use_numba=False
) -> np.ndarray:
    if use_numba and NUMBA_AVAILABLE:
        return build_successor_numba(
            sigma_length, max_length, np.array(input_sequence, dtype=np.int32)
        )
    else:
        return _build_successor_python(sigma_length, max_length, input_sequence)


def _build_embed_python(
    constraint_len: int, constraint: list[int], input_sequence: list[int]
) -> np.ndarray:
    embed = np.full(constraint_len, INT_MAX, dtype=np.int32)
    p_idx = constraint_len - 1

    for i in range(len(input_sequence) - 1, -1, -1):
        if p_idx < 0:
            break
        if input_sequence[i] == constraint[p_idx]:
            embed[p_idx] = i
            p_idx -= 1

    return embed


def build_embed(
    constraint_len: int,
    constraint: list[int],
    input_sequence: list[int],
    use_numba=False,
) -> np.ndarray:
    if use_numba and NUMBA_AVAILABLE:
        return build_embed_numba(
            constraint_len,
            np.array(constraint, dtype=np.int32),
            np.array(input_sequence, dtype=np.int32),
        )
    else:
        return _build_embed_python(constraint_len, constraint, input_sequence)


def _build_occurrence_python(
    sigma_len: int, max_len: int, input_sequence: list[int]
) -> np.ndarray:
    occurrence_table = np.zeros((sigma_len, max_len), dtype=np.int32)
    count = np.zeros(sigma_len, dtype=np.int32)

    for ch_idx in range(len(input_sequence) - 1, -1, -1):
        count[input_sequence[ch_idx]] += 1
        occurrence_table[:, ch_idx] = count

    return occurrence_table


def build_occurrence(
    sigma_len: int, max_len: int, input_sequence: list[int], use_numba=False
) -> np.ndarray:
    if use_numba and NUMBA_AVAILABLE:
        return build_occurrence_numba(
            sigma_len, max_len, np.array(input_sequence, dtype=np.int32)
        )
    else:
        return _build_occurrence_python(sigma_len, max_len, input_sequence)


def _lcs_mscore_ij_python(
    len_i: int, len_j: int, input_i: list[int], input_j: list[int]
) -> np.ndarray:
    m_ij = np.zeros((len_i + 1, len_j + 1), dtype=np.int32)

    for x in range(len_i - 1, -1, -1):
        for y in range(len_j - 1, -1, -1):
            if input_i[x] == input_j[y]:
                m_ij[x, y] = m_ij[x + 1, y + 1] + 1
            else:
                m_ij[x, y] = max(m_ij[x, y + 1], m_ij[x + 1, y])

    return m_ij


def lcs_mscore_ij(
    len_i: int, len_j: int, input_i: list[int], input_j: list[int], use_numba=False
) -> np.ndarray:
    if use_numba and NUMBA_AVAILABLE:
        return lcs_mscore_ij_numba(
            len_i,
            len_j,
            np.array(input_i, dtype=np.int32),
            np.array(input_j, dtype=np.int32),
        )
    else:
        return _lcs_mscore_ij_python(len_i, len_j, input_i, input_j)
