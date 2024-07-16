import numpy as np
import heapq

from parsing import get_sigma, get_mapping, translate, parsefile
from processing import (
    build_embed,
    build_occurrence,
    build_successor,
    lcs_mscore_ij,
    NUMBA_AVAILABLE,
)


class Node:
    """A node in the state graph for the CLCS problem."""

    __slots__ = ("pv", "l_v", "u_v", "parent")
    PV_LENGTH = None

    @classmethod
    def set_pv_length(cls, length: int):
        cls.PV_LENGTH = length

    @staticmethod
    def create():
        return Node(pv=(1,) * Node.PV_LENGTH, l_v=0, u_v=0, parent=None)

    def __init__(self, pv: tuple[int, ...], l_v: int, u_v: int, parent=None) -> None:
        self.pv = pv
        self.l_v = l_v
        self.u_v = u_v
        self.parent = parent

    def get_l_u(self) -> tuple[int, int]:
        """returns a tuple of the length of the partial solution
        and the length of the pattern prefix"""
        return (self.l_v, self.u_v)

    def get_plu(self) -> tuple[tuple[int, ...], int, int]:
        return (self.pv, self.l_v, self.u_v)

    def get_pv(self) -> tuple[int, ...]:
        return self.pv

    def __repr__(self) -> str:
        return f"({self.pv}, {self.l_v}, {self.u_v})"

    def __eq__(self, other) -> bool:
        if not isinstance(other, Node):
            raise NotImplementedError

        return self.get_plu() == other.get_plu()

    def __hash__(self):
        return hash(self.get_plu())


class Pv_Uv:
    def __init__(self, pv: list[int], u_v: int):
        self.pv = pv
        self.u_v = u_v

    def __repr__(self) -> str:
        return f"Pv {self.pv} {self.u_v}"


class AStar_Solution:
    @staticmethod
    def create() -> "AStar_Solution":
        return AStar_Solution(list(), list())

    def __init__(self, solutions: list[list[int]], expandeds: list[int]):
        self.solutions = solutions
        self.expandeds = expandeds


class CLCS:
    @staticmethod
    def fromfile(file_path: str, do_sort=True, use_numba=False, take_n=-1) -> "CLCS":
        str_constraint, str_inputs = parsefile(file_path)
        return CLCS.fromstring(str_constraint, str_inputs, do_sort, use_numba, take_n)

    @staticmethod
    def fromstring(
        str_constraint: str,
        str_inputs: list[str],
        do_sort=True,
        use_numba=False,
        take_n=0,
    ) -> "CLCS":
        sigma = get_sigma(str_inputs, do_sort)
        int_to_char, char_to_int = get_mapping(sigma)
        constraint = translate(str_constraint, char_to_int)
        inputs = [translate(str_input, char_to_int) for str_input in str_inputs]
        if take_n > 0:
            inputs = inputs[:take_n]
        return CLCS.create_instance(
            len(sigma), constraint, inputs, int_to_char, char_to_int, use_numba
        )

    @classmethod
    def create_instance(
        cls,
        sigma_len: int,
        constraint: list[int],
        inputs: list[list[int]],
        int_to_char: dict[int, str],
        char_to_int: dict[str, int],
        use_numba=False,
    ):
        input_lens = [len(inp) for inp in inputs]

        return cls(
            sigma_len=sigma_len,
            constraint=constraint,
            inputs=inputs,
            num_inputs=len(inputs),
            input_lens=input_lens,
            max_len=len(max(inputs, key=len)),
            min_len=len(min(inputs, key=len)),
            constraint_len=len(constraint),
            int_to_char=int_to_char,
            char_to_int=char_to_int,
            use_numba=use_numba,
        )

    def __init__(
        self,
        sigma_len: int,
        constraint: list[int],
        inputs: list[list[int]],
        num_inputs: int,
        input_lens: list[int],
        max_len: int,
        min_len: int,
        constraint_len: int,
        int_to_char: dict[int, str],
        char_to_int: dict[str, int],
        use_numba=False,
    ) -> None:
        self.sigma_len = sigma_len
        self.constraint = constraint
        self.inputs = inputs
        self.num_inputs = num_inputs
        self.input_lens = input_lens
        self.max_len = max_len
        self.min_len = min_len
        self.constraint_len = constraint_len
        self.use_numba = use_numba

        if self.use_numba and NUMBA_AVAILABLE:
            print("Using numba jit")
        else:
            print("Not using numba njit")

        self.successor_tables = np.empty(self.num_inputs, dtype=object)
        self.embed_tables = np.empty(self.num_inputs, dtype=object)
        self.occurrence_tables = np.empty(self.num_inputs, dtype=object)
        self.mscores = self.build_mscores()
        self.populate_tables()

        self.int_to_char = int_to_char
        self.char_to_int = char_to_int

    def populate_tables(self):
        for i in range(self.num_inputs):
            self.successor_tables[i] = build_successor(
                self.sigma_len, self.max_len, self.inputs[i], self.use_numba
            )
            self.embed_tables[i] = build_embed(
                self.constraint_len, self.constraint, self.inputs[i], self.use_numba
            )
            self.occurrence_tables[i] = build_occurrence(
                self.sigma_len, self.max_len, self.inputs[i], self.use_numba
            )

    def build_mscores(self) -> np.ndarray:
        mscores = np.empty(self.num_inputs - 1, dtype=object)
        for i in range(self.num_inputs - 1):
            mscores[i] = lcs_mscore_ij(
                self.input_lens[i],
                self.input_lens[i + 1],
                self.inputs[i],
                self.inputs[i + 1],
            )
        return mscores


class MaxPriorityQueueOptimized:
    def __init__(self):
        self._queue = []
        self._entry_finder = {}
        self._counter = 0

    def push(self, item: Node, priority: int):
        item_plu = item.get_plu()
        if item_plu in self._entry_finder:
            self.remove_node(item.pv, item.l_v, item.u_v)
        entry = [-priority, self._counter, item]
        self._entry_finder[item_plu] = entry
        heapq.heappush(self._queue, entry)
        self._counter += 1

    def pop(self) -> Node:
        while self._queue:
            priority, count, item = heapq.heappop(self._queue)
            if item is not None:
                del self._entry_finder[item.get_plu()]
                return item
        raise KeyError("pop from an empty priority queue")

    def remove_node(self, pv: tuple[int, ...], l_v: int, u_v: int) -> bool:
        key = (pv, l_v, u_v)
        entry = self._entry_finder.pop(key, None)
        if entry is not None:
            entry[-1] = None  # Mark as removed
            return True
        return False

    def is_empty(self) -> bool:
        return not bool(self._entry_finder)

    def __len__(self) -> int:
        return len(self._entry_finder)


class MaxPriorityQueueOptimizedSecond:
    REMOVED = "<removed-node>"  # Placeholder for a removed node

    def __init__(self):
        self._queue = []
        self._entry_finder = {}
        self._counter = 0

    def push(self, item: Node, priority: int):
        item_plu = item.get_plu()
        if item_plu in self._entry_finder:
            self.remove_node(item_plu)
        entry = [-priority, self._counter, item]
        self._entry_finder[item_plu] = entry
        heapq.heappush(self._queue, entry)
        self._counter += 1

    def pop(self) -> Node:
        while self._queue:
            priority, count, item = heapq.heappop(self._queue)
            if item is not self.REMOVED:
                del self._entry_finder[item.get_plu()]
                return item
        raise KeyError("pop from an empty priority queue")

    def remove_node(self, item_plu: tuple[tuple[int, ...], int, int]) -> bool:
        entry = self._entry_finder.pop(item_plu, None)
        if entry is not None:
            entry[-1] = self.REMOVED
            return True
        return False

    def is_empty(self) -> bool:
        return not self._entry_finder

    def __len__(self) -> int:
        return len(self._entry_finder)
