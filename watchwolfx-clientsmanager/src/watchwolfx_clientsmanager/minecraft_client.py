#!/usr/bin/env python3


class MinecraftClient:
    # every client has a nickname and a server target
    def __init__(self, host: str, port: int, username: str):
        self._server_ip = host
        self._server_port = port
        self._username = username

    @property
    def server(self) -> str:
        return self._server_ip + ":" + str(self._server_port)

    @property
    def port(self) -> int:
        return self._server_port

    def synchronize(self) -> None:
        """Preserve the legacy petition barrier, which is currently a no-op."""
