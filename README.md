# PY-OS Cloud ☁️

PY-OS Cloud is a browser-based desktop environment powered by a small Python server. It is designed so a future Android APK can share the same core and data format.

## Current version

**0.2.0 — browser desktop**

The browser edition now includes:

- Desktop workspace with app icons.
- Start menu and taskbar.
- Movable, minimizable, maximizable, and closable app windows.
- File Manager for PY-OS virtual text files.
- Built-in browser window (some sites block being shown inside another page).
- Notes app with automatic saving.
- Calculator.
- Dark and light themes.
- Shared state saved locally by the Python server.

This is an early desktop build. The File Manager manages PY-OS virtual files; it does not browse or modify Android's real storage.

## Run on Termux, Linux, or macOS

Clone the repository, then run:

```sh
git clone https://github.com/zaydzaydfarhat-blip/py-os-cloud.git
cd py-os-cloud
python -m pyos_cloud
```

Open this address in the browser on the same device:

```text
http://127.0.0.1:8765
```

Stop the server with `Ctrl+C`.

Python 3.10 or newer is recommended. The server binds to localhost by default. Keep this default: authentication and HTTPS are not implemented, so do not expose the server to the internet or an untrusted network.

Choose another local port with:

```sh
python -m pyos_cloud --port 9000
```

## Saved data

PY-OS Cloud stores its shared state at `~/.py-os-cloud/state.json`. You can choose another folder with:

```sh
export PYOS_CLOUD_HOME="$HOME/my-pyos-data"
```

## Planned for later

- **Backup and import:** portable `.pycloud` export/import.
- **Android APK:** a native app wrapper using the same desktop and shared data format.
- More desktop apps and deeper Android integration.

The backup format module is currently not exposed in the desktop interface or HTTP API. The APK is not built yet.

## Tests

```sh
python -m unittest discover -s tests -v
```

## Project layout

- `pyos_cloud/` — shared Python core and local server.
- `web/` — browser desktop.
- `android/` — planned Android app.
- `tests/` — core tests.
