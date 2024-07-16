import numpy as np

from datastructure import Node, CLCS

INT_MAX = np.iinfo(np.int32).max

def ub_is_overshoot(num_inputs: int, pos_vec: list[int], input_lens: list[int]) -> list[bool]:
    return [pos_vec[i] > input_lens[i] for i in range(num_inputs)]

def ub_min_occurrence(sigma_len: int, num_inputs: int, pos_vec: list[int], input_lens: list[int], occurrence_tables: np.ndarray) -> int:
    is_overshoot = ub_is_overshoot(num_inputs, pos_vec, input_lens)
    ub = 0
    for label in range(sigma_len):
        label_min = INT_MAX
        
        for i in range(num_inputs):
            input_min = 0 if is_overshoot[i] else occurrence_tables[i][label, pos_vec[i] - 1]
            label_min = min(label_min, input_min)
        ub += label_min
    
    return ub

def ub_mscore(min_len: int, num_inputs: int, pos_vec: list[int], mscores: np.ndarray) -> int:
    ub = min_len
    
    for i in range(num_inputs-1):
        ub = min(ub, mscores[i][pos_vec[i] - 1, pos_vec[i+1] - 1])
    
    return ub

def ub_both(inst: CLCS, v: Node) -> int:
    ub_minc = ub_min_occurrence(inst.sigma_len, inst.num_inputs, v.pv, inst.input_lens, inst.occurrence_tables)
    ub_mscr = ub_mscore(inst.min_len, inst.num_inputs, v.pv, inst.mscores)
    
    ub = min(ub_minc, ub_mscr)
    
    return v.l_v + ub