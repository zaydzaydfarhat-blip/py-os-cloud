"""Local-only HTTP API and static web desktop for PY-OS Cloud."""

from __future__ import annotations

import json
from http.server import BaseHTTPRequestHandler
from pathlib import Path
from urllib.parse import urlparse

from .store import StateStore

WEB_ROOT = Path(__file__).resolve().parent.parent / "web"
MAX_REQUEST_BYTES = 20 * 1024 * 1024


def make_handler():
    store = StateStore()

    class Handler(BaseHTTPRequestHandler):
        server_version = "PYOSCloud/0.2"

        def log_message(self, fmt, *args):
            print("PY-OS Cloud:", fmt % args)

        def send_bytes(self, status: int, content: bytes, content_type: str) -> None:
            self.send_response(status)
            self.send_header("Content-Type", content_type)
            self.send_header("Content-Length", str(len(content)))
            self.send_header("X-Content-Type-Options", "nosniff")
            self.send_header("Cache-Control", "no-store")
            self.end_headers()
            self.wfile.write(content)

        def send_json(self, status: int, value) -> None:
            self.send_bytes(status, json.dumps(value).encode("utf-8"), "application/json; charset=utf-8")

        def read_body(self) -> bytes:
            length = int(self.headers.get("Content-Length", "0"))
            if length < 0 or length > MAX_REQUEST_BYTES:
                raise ValueError("Request is too large.")
            return self.rfile.read(length)

        def do_GET(self):
            path = urlparse(self.path).path
            if path == "/api/state":
                try:
                    self.send_json(200, store.load())
                except ValueError as exc:
                    self.send_json(500, {"error": str(exc)})
                return
            if path in ("/", "/index.html"):
                target = WEB_ROOT / "index.html"
            else:
                self.send_json(404, {"error": "Not found"})
                return
            try:
                self.send_bytes(200, target.read_bytes(), "text/html; charset=utf-8")
            except OSError:
                self.send_json(500, {"error": "Web interface files are missing."})

        def do_POST(self):
            path = urlparse(self.path).path
            try:
                body = self.read_body()
                if path == "/api/state":
                    state = json.loads(body.decode("utf-8"))
                    self.send_json(200, store.save(state))
                else:
                    self.send_json(404, {"error": "Not found"})
            except (ValueError, UnicodeDecodeError, json.JSONDecodeError) as exc:
                self.send_json(400, {"error": str(exc)})
            except Exception as exc:
                self.send_json(500, {"error": f"Server error: {exc}"})

    return Handler
