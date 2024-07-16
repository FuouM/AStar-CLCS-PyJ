import os


def get_sigma(str_inputs: list[str], do_sort=True) -> list[str]:
    sigma = list(set("".join(str_inputs)))
    if do_sort:
        sigma.sort()
    return sigma

def get_mapping(sigma: list[str]) -> tuple[dict[int, str], dict[str, int]]:
    int_to_char: dict[int, str] = dict()
    char_to_int: dict[str, int] = dict()
    for num, char in enumerate(sigma):
        int_to_char[num] = char
        char_to_int[char] = num
    return int_to_char, char_to_int

def translate(src: list[int] | str, mapping: dict[int, str] | dict[str, int]):
    from_type = int if isinstance(src[0], int) else str
    default = "a" if from_type is int else 0
    res = [mapping.get(from_type(key), default) for key in src]
    return res

def parsefile(file_path: str) -> tuple[str, list[str]]:
    assert os.path.isfile(file_path), f"{file_path} does not exist"

    with open(file_path, "r") as inp_f:
        lines = inp_f.readlines()[1:]
        data = [line.split()[1] for line in lines]

        str_constraint = data[0]
        str_inputs = data[1:]

        return str_constraint, str_inputs