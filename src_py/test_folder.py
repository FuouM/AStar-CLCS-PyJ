import os
import time

from datastructure import CLCS
from parsing import translate
from astar import astar_run
from dp_deo import DP_Deo


def write_to_file(filepath):
    output_file = "mismatched_lengths.txt"
    with open(output_file, "a") as f:
        f.write(f"{filepath}\n")

# folder = "testcase/real" # No need to use numba
folder = "testcase/artificial" # Should use numba
# folder = "testcase/CLCS_instances" # No need to use numba
testfiles = os.listdir(folder)
print(len(testfiles))
checkpoint = 0
# use_numba = False
use_numba = True
i = 4

for filepath in testfiles:
    if i < checkpoint:
        i += 1
        continue
    st = time.time()
    full_path = os.path.join(os.getcwd(), folder, filepath)
    inst = CLCS.fromfile(full_path, take_n=2, use_numba=use_numba)

    print(f"[{i}] Init took {time.time() - st} s")

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

    len_deo = len(deo_out)
    len_astar = len(astar_out)

    # print(f"DP_DEO [{len_deo}] {deo_out}")
    # print(f"A_STAR [{len_astar}] {astar_out}")
    print(f"DP_DEO took {deo_time} s")
    print(f"A_STAR took {astar_time} s")

    print(f"Match str: {deo_out == astar_out}")
    print(f"Match len: {len_astar == len_deo}")

    if not len_astar == len_deo:
        write_to_file(filepath)
        break

    i += 1
    if i == 7:
        break
else:
    print("All is good")
