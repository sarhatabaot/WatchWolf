#!/usr/bin/env python3

from .minecraft_client import MinecraftClient


class OnClientConnected:
    def client_connected(self, client: MinecraftClient):
        pass
