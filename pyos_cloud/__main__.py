"""Run the PY-OS Cloud local web interface with python -m pyos_cloud."""

from __future__ import annotations

import argparse
from http.server import ThreadingHTTPServer

from .server import make_handler


def main() -> None:
    parser = argparse.ArgumentParser(description="Run the PY-OS Cloud web desktop.")
    parser.add_argument("--host", default="127.0.0.1", help="Bind address (default: localhost only)")
    parser.add_argument("--port", type=int, default=8765, help="Port (default: 8765)")
    args = parser.parse_args()

    if args.host not in ("127.0.0.1", "localhost", "::1"):
        print("WARNING: this server will be reachable from other devices on your network.")
        print("Do not expose it to the public internet; authentication is not implemented yet.")

    server = ThreadingHTTPServer((args.host, args.port), make_handler())
    print(f"PY-OS Cloud is running at http://127.0.0.1:{args.port}")
    print("Press Ctrl+C to stop the server.")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping PY-OS Cloud...")
    finally:
        server.server_close()


if __name__ == "__main__":
    main()
