"""WatchWolfX ClientsManager and headless Minecraft client."""

from importlib.metadata import PackageNotFoundError, version

try:
    __version__ = version("watchwolfx-clientsmanager")
except PackageNotFoundError:
    __version__ = "0.0.0+unknown"
