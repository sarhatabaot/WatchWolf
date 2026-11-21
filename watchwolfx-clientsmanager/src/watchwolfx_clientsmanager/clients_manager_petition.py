#!/usr/bin/env python3

from typing import Protocol


class ClientsManagerPetition(Protocol):
    def start_client(self, username: str, server_ip: str, reply_host: str | None = None) -> str: ...
