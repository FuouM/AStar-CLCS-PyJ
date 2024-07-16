import numpy as np

try:
    from accelerated import DP_Deo_numba

    NUMBA_AVAILABLE = True
except ImportError:
    NUMBA_AVAILABLE = False

print(f"{NUMBA_AVAILABLE=}")

INT_MIN = np.iinfo(np.int32).min


def _DP_Deo_python(
    p_con: list[int],
    sigma_len: int,
    s0: list[int],
    s1: list[int],
    len_s0: int,
    len_s1: int,
    max_len: int,
) -> list[int]:
    p_len = len(p_con)

    T_vec = [[INT_MIN for _ in range(max_len)] for _ in range(max_len)]
    F_vec = [
        [[(INT_MIN, INT_MIN, INT_MIN) for _ in range(max_len)] for _ in range(max_len)]
        for _ in range(p_len + 1)
    ]

    n_pos = [0 for _ in range(sigma_len)]
    pos = [[INT_MIN for _ in range(max_len)] for _ in range(sigma_len)]

    for i in range(len_s0):
        pos[s0[i]][n_pos[s0[i]]] = i
        n_pos[s0[i]] = n_pos[s0[i]] + 1

    s_sol = []
    for k in range(p_len + 1):
        l_0 = [(INT_MIN, INT_MIN, INT_MIN)]
        if k == 0:
            l_0[0] = (l_0[0][0], l_0[0][1], 0)  # type: ignore
        n_0 = 1
        n_1 = 0
        for j in range(len_s1):
            l_1 = [l_0[0]]
            n_1 += 1
            p = 1
            for s in range(n_pos[s1[j]]):
                i = pos[s1[j]][s]
                while p < n_0:
                    if l_0[p][0] >= i:
                        break
                    elif l_1[n_1 - 1][2] < l_0[p][2] and l_0[p][2] > 0:
                        l_1.append((l_0[p][0], l_0[p][1], l_0[p][2]))
                        n_1 += 1
                    p += 1
                if k > 0 and s1[j] == p_con[k - 1]:
                    v = T_vec[j][i]
                    T_vec[j][i] = l_0[p - 1][2] + 1
                else:
                    v = l_0[p - 1][2] + 1
                    T_vec[j][i] = v
                F_vec[k][j][i] = (l_0[p - 1][0], l_0[p - 1][1], v)
                if l_1[n_1 - 1][2] < v:
                    l_1.append((i, j, v))
                    n_1 += 1
            while p < n_0 and l_1[n_1 - 1][2] >= l_0[p][2]:
                p += 1
            while p < n_0:
                l_1.append((l_0[p][0], l_0[p][1], l_0[p][2]))
                n_1 += 1
                p += 1
            l_0 = l_1
            n_0 = n_1
            n_1 = 0
        if k == p_len:
            item = (l_0[n_0 - 1][0], l_0[n_0 - 1][1], l_0[n_0 - 1][2])
            k_sol = p_len
            while item[2] > 1:
                s_sol.append(s1[item[1]])
                if k_sol > 0 and s1[item[1]] == p_con[k_sol - 1]:
                    k_sol -= 1
                item = F_vec[k_sol][item[1]][item[0]]
            s_sol.reverse()
            return s_sol

    return [-1]


def DP_Deo(
    p_con: list[int],
    sigma_len: int,
    s0: list[int],
    s1: list[int],
    len_s0: int,
    len_s1: int,
    max_len: int,
    use_numba=False,
) -> list[int]:
    if use_numba and NUMBA_AVAILABLE:
        result = DP_Deo_numba(
            np.array(p_con, dtype=np.int32),
            sigma_len,
            np.array(s0, dtype=np.int32),
            np.array(s1, dtype=np.int32),
            len_s0,
            len_s1,
            max_len,
        )
        return result.tolist()
    else:
        return _DP_Deo_python(p_con, sigma_len, s0, s1, len_s0, len_s1, max_len)
