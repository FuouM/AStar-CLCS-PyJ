import numpy as np
from numba import njit, prange

INT_MAX = np.iinfo(np.int32).max
INT_MIN = np.iinfo(np.int32).min


# build_successor
@njit(parallel=True)
def build_successor_numba(
    sigma_length: int, max_length: int, input_sequence: np.ndarray
) -> np.ndarray:
    successor_table = np.full((sigma_length, max_length), INT_MAX, dtype=np.int32)

    for label in prange(sigma_length):
        number = INT_MAX
        for i in range(len(input_sequence) - 1, -1, -1):
            if input_sequence[i] == label:
                number = i
            successor_table[label, i] = number

    return successor_table


# build_embed
@njit
def build_embed_numba(
    constraint_len: int, constraint: np.ndarray, input_sequence: np.ndarray
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


# build_occurrence
@njit
def build_occurrence_numba(
    sigma_len: int, max_len: int, input_sequence: np.ndarray
) -> np.ndarray:
    occurrence_table = np.zeros((sigma_len, max_len), dtype=np.int32)
    count = np.zeros(sigma_len, dtype=np.int32)

    for ch_idx in range(len(input_sequence) - 1, -1, -1):
        count[input_sequence[ch_idx]] += 1
        occurrence_table[:, ch_idx] = count

    return occurrence_table


# lcs_mscore_ij
@njit
def lcs_mscore_ij_numba(
    len_i: int, len_j: int, input_i: np.ndarray, input_j: np.ndarray
) -> np.ndarray:
    m_ij = np.zeros((len_i + 1, len_j + 1), dtype=np.int32)

    for x in range(len_i - 1, -1, -1):
        for y in range(len_j - 1, -1, -1):
            if input_i[x] == input_j[y]:
                m_ij[x, y] = m_ij[x + 1, y + 1] + 1
            else:
                m_ij[x, y] = max(m_ij[x, y + 1], m_ij[x + 1, y])

    return m_ij


@njit
def DP_Deo_numba(
    p_con: np.ndarray,
    sigma_len: int,
    s0: np.ndarray,
    s1: np.ndarray,
    len_s0: int,
    len_s1: int,
    max_len: int,
) -> np.ndarray:
    p_len = len(p_con)

    T_vec = np.full((max_len, max_len), INT_MIN, dtype=np.int32)
    F_vec = np.full((p_len + 1, max_len, max_len, 3), INT_MIN, dtype=np.int32)

    n_pos = np.zeros(sigma_len, dtype=np.int32)
    pos = np.full((sigma_len, max_len), INT_MIN, dtype=np.int32)

    for i in range(len_s0):
        pos[s0[i], n_pos[s0[i]]] = i
        n_pos[s0[i]] += 1

    s_sol = np.empty(max_len, dtype=np.int32)
    s_sol_len = 0

    for k in range(p_len + 1):
        l_0 = np.full((max_len, 3), INT_MIN, dtype=np.int32)
        if k == 0:
            l_0[0, 2] = 0
        n_0 = 1
        n_1 = 0

        for j in range(len_s1):
            l_1 = np.full((max_len, 3), INT_MIN, dtype=np.int32)
            l_1[0] = l_0[0]
            n_1 = 1
            p = 1

            for s in range(n_pos[s1[j]]):
                i = pos[s1[j], s]
                while p < n_0 and l_0[p, 0] < i:
                    if l_1[n_1 - 1, 2] < l_0[p, 2] and l_0[p, 2] > 0:
                        l_1[n_1] = l_0[p]
                        n_1 += 1
                    p += 1

                if k > 0 and s1[j] == p_con[k - 1]:
                    v = T_vec[j, i]
                    T_vec[j, i] = l_0[p - 1, 2] + 1
                else:
                    v = l_0[p - 1, 2] + 1
                    T_vec[j, i] = v

                F_vec[k, j, i] = l_0[p - 1]
                F_vec[k, j, i, 2] = v

                if l_1[n_1 - 1, 2] < v:
                    l_1[n_1, 0] = i
                    l_1[n_1, 1] = j
                    l_1[n_1, 2] = v
                    n_1 += 1

            while p < n_0 and l_1[n_1 - 1, 2] >= l_0[p, 2]:
                p += 1
            while p < n_0:
                l_1[n_1] = l_0[p]
                n_1 += 1
                p += 1

            l_0 = l_1[:n_1]
            n_0 = n_1

        if k == p_len:
            item = l_0[n_0 - 1]
            k_sol = p_len
            while item[2] > 1:
                s_sol[s_sol_len] = s1[item[1]]
                s_sol_len += 1
                if k_sol > 0 and s1[item[1]] == p_con[k_sol - 1]:
                    k_sol -= 1
                item = F_vec[k_sol, item[1], item[0]]

    if s_sol_len > 0:
        return np.flip(s_sol[:s_sol_len])
    else:
        return np.array([-1], dtype=np.int32)
