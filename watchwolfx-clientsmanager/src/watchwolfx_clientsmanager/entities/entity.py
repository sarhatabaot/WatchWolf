#!/usr/bin/env python3

from __future__ import annotations

from typing import Any

from ..position import Position


class Entity:
    def __init__(self, uuid: str, pos: Position):
        self._uuid = uuid
        self._position = pos

    @property
    def uuid(self):
        return self._uuid

    @property
    def position(self):
        return self._position

    def __eq__(self, obj: Any) -> bool:
        if type(self) is not type(obj):
            return False
        return self._uuid == obj._uuid

    def __str__(self):
        return self.__class__.__name__ + "{" + f"uuid={self._uuid},position={self._position}" + "}"
