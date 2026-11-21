#!/usr/bin/env python3

import socket  # for the video stream
import threading

from javascript import require

from .image_list import ImageList
from .viewer import Viewer


class MineflayerViewer(threading.Thread, Viewer):
    def __init__(self, bot, port: int, size: tuple[int, int] = (512, 512), printer=lambda msg: print(msg)):
        threading.Thread.__init__(self)
        Viewer.__init__(self, size, printer)

        self._bot = bot
        self._port = port
        self._socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self._socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self._socket.bind(("0.0.0.0", port))
        self._socket.listen(10)

        self._images = ImageList()

    def setup(self):
        self.start()

    def run(self):
        mineflayerViewer = require("prismarine-viewer").headless
        self._client_socket = mineflayerViewer(
            self._bot,
            {
                "output": "127.0.0.1:" + str(self._port),
                "frames": -1,
                "width": self._size[0],
                "height": self._size[1],
                "firstPerson": True,
                "logFFMPEG": True,
            },
        )

        self._printer("[v] Starting video thread...")
        self._conn, _ = self._socket.accept()

        while True:
            length = MineflayerViewer.recvint(self._conn)
            if length is None:
                break  # closed connection
            stringData = MineflayerViewer.recvall(self._conn, int(length))
            if stringData is None:
                break
            self._images.append(stringData)

        self._printer("[v] Terminating video socket connection...")
        self._socket.shutdown(socket.SHUT_RDWR)
        self._socket.close()

    @property
    def get_id(self) -> int | None:
        return self.ident

    def close(self):
        # Disconnecting the client terminates the video socket through the bridge.
        pass

    def start_recording(self) -> int:
        return self._images.start_recording()

    def stop_recording(self, id: int, out: str):
        try:
            self._images.stop_recording(id, out)
        except Exception as ex:
            self._printer(f"[e] {ex}")

    @staticmethod
    def recvall(sock, count) -> bytes | None:
        buf = b""
        while count:
            newbuf = sock.recv(count)
            if not newbuf:
                return None
            buf += newbuf
            count -= len(newbuf)
        return buf

    @staticmethod
    def recvint(sock) -> int | None:
        bytes_read = MineflayerViewer.recvall(sock, 4)
        if bytes_read is None:
            return None
        return int.from_bytes(bytes_read, byteorder="little")
