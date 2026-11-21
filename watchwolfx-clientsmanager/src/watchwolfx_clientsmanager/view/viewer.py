#!/usr/bin/env python3

from collections.abc import Callable
from typing import Protocol


class Viewer(Protocol):
    _size: tuple[int, int]
    _printer: Callable[[str], None]

    def __init__(self, size: tuple[int, int] = (512, 512), printer: Callable[[str], None] = print):
        self._size = size
        self._printer = printer

    def setup(self): ...

    def close(self): ...

    def start_recording(self) -> int: ...

    def stop_recording(self, id: int, out: str): ...
