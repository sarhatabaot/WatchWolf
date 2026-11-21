#!/usr/bin/env python3

from .minecraft_client import MinecraftClient


class OnClientDisconnected:
    def client_disconnected(self, client: MinecraftClient):
        pass
