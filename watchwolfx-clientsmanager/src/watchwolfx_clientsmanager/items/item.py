#!/usr/bin/env python3

from typing import Any


class Item:
    def __init__(self, type: Any, amount: int):
        self._type = type
        self._amount = amount

    def set_amount(self, amount: int):
        self._amount = amount

    @property
    def type(self) -> Any:
        return self._type

    @property
    def amount(self) -> int:
        return self._amount

    def __eq__(self, obj: Any) -> bool:
        if type(self) is not type(obj):
            return False
        return self._type == obj._type and self._amount == obj._amount

    def __str__(self):
        return str(self._type) + "{" + str(self._amount) + "}"
