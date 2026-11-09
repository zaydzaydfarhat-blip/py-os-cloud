# PY-OS Cloud ☁️

PY-OS Cloud is a browser-based desktop environment powered by a small Python server. It is designed so a future Android APK can share the same core and data format.

## Current version

**0.3.0 — touch and keyboard desktop**

The browser edition now includes:

- Desktop workspace with app icons.
- Start menu and taskbar.
- Movable, minimizable, maximizable, and closable app windows.
- File Manager for PY-OS virtual text files.
- Notes app with automatic saving.
- Calculator.
- Dark and light themes.\n- Touch-friendly controls and window dragging on Android tablets and phones.\n- Mouse support for desktop browsers.\n- Keyboard shortcuts and calculator keyboard input.
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
- **Native Android app:** a Java-based APK project is now in `android/`.
- **APK builds:** GitHub Actions builds a debug APK and publishes it as a workflow artifact. Version tags beginning with `v` also attach the APK to a GitHub Release.
- More desktop apps and deeper Android integration.

The Android APK now embeds the same `web/index.html` desktop UI in a WebView, including the desktop icons, Start menu, taskbar, app windows, themes, and mini arcade. Android saves its PY-OS state locally in WebView storage. The APK also uses a custom PY-OS launcher icon. See [android/README.md](android/README.md) for build instructions.

## Tests

```sh
python -m unittest discover -s tests -v
```

## Project layout

- `pyos_cloud/` — shared Python core and local server.
- `web/` — browser desktop.
- `android/` — planned Android app.
- `tests/` — core tests.
