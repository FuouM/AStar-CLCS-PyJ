import time


from datastructure import CLCS
from dp_deo import DP_Deo
from parsing import translate
from astar import astar_run

use_numba = False
st = time.time()

# raw_p_con = "cbb"
# raw_s0 = "bcaacbdba"
# raw_s1 = "cbccadcbbd"

# raw_p_con = "ddddcacbcbcbbaadccddadbcbccbcbdcdccdbaccdabcdaacdb"
# raw_s0 = "dbdddbacbdccacbdbcbbccbdbcaacbdacdacdbddadbcacacbbbcdaabbccbbccbdbcdccbdabdaaacccdcaabbcbdcaaccbddbc"
# raw_s1 = "adddbdddcacbaaabcabcbbcadaababddccddcaddbcaddbccbcbccbdddcdadcdbccddbcacacdcddccadbcdccccbdcadacdcdb"

raw_p_con = "HKH"
raw_s0 = "MVVDLPRYLPLLLLLELWEPMYLLCSQPKGLSRAHWFEIQHVQTSRQPCNTAMRGVNNYTQHCKQINTFLHESFQNVAATCSLHNITCKNGRKNCHESAEPVKMTDCSHTGGAYPNCRYSSDKQYKFFIVACEHPKKEDPPYQLVPVHLDKIV"
raw_s1 = "MKPLVIKFAWPLPLLLLLLLPPKLQGNYWDFGEYELNPEVRDFIREYESTGPTKPPTVKRIIEMITIGDQPFNDYDYCNTELRTKQIHYKGRCYPEHYIAGVPYGELVKACDGEEVQCKNGVKSCRRSMNLIEGVRCVLETGQQMTNCTYKTILMIGYPVVSCQWDEETKIFIPDHIYNMSLPK"

# raw_p_con =	"aabdbdccddddcdbbcbbdaddbd"
# raw_s0 = "daabcbaccacabbdabbdacabccdcbbaacdbdddcadcbbdbddcacddaacbbbbcaadacbbbbbcdacddabadcbdacdddddbdbdbccadd"
# raw_s1 = "aaadcaaadcbdbbaaddccbddbaadabacddbcdaddddacbabaaadbbbcaadccbbcbbdbbccbdabbbbadaacdddcbabcbdbdbdbadbd"
# raw_s2 = "bddbababaaaabbdacabddbadbddbadbbcbbbcdbadbacdddbcdbdcaddabcccacdbdbaabdccbbbdcddcbccaaadabdcabdbddca"


inst = CLCS.fromstring(raw_p_con, [raw_s0, raw_s1], use_numba=use_numba)
# inst = CLCS.fromstring(raw_p_con, [raw_s0, raw_s1, raw_s2], use_numba=use_numba)
print(f"Init took {time.time() - st} s")
st = time.time()
deo_out = "".join(
    translate(
        DP_Deo(
            p_con=inst.constraint,
            sigma_len=inst.sigma_len,
            s0=inst.inputs[0],
            s1=inst.inputs[1],
            len_s0=inst.input_lens[0],
            len_s1=inst.input_lens[1],
            max_len=inst.max_len,
            use_numba=use_numba,
        ),
        inst.int_to_char,
    )
)
deo_time = time.time() - st

st = time.time()

astar_out = "".join(translate(astar_run(inst).solutions[0], inst.int_to_char))

astar_time = time.time() - st

print(f"DP_DEO [{len(deo_out)}] {deo_out}")
print(f"A_STAR [{len(astar_out)}] {astar_out}")
print(f"DP_DEO took {deo_time} s")
print(f"A_STAR took {astar_time} s")
