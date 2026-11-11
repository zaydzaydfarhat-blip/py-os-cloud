# PY-OS Cloud ☁️

PY-OS Cloud is a browser-based desktop environment with an official Android APK edition.

## Current version

**0.5.0 — shared cloud sync and AI assistant**

The browser edition now includes:

- Desktop workspace with app icons.
- Start menu and taskbar.
- Movable, minimizable, maximizable, and closable app windows.
- File Manager for PY-OS virtual text files.
- Notes app with automatic saving.
- Calculator.
- Dark and light themes.
- Touch-friendly controls and window dragging on Android tablets and phones.
- Mouse support for desktop browsers.
- Keyboard shortcuts and calculator keyboard input.
- Shared state saved by the Python server, with browser-local fallback when offline or hosted as a static site.
- Portable `.pycloud` backup export/import for notes, virtual files, and settings.

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

## AI assistant

PY-OS AI uses the OpenAI Responses API through the Python server. The API key stays on the server and is never placed in the browser or APK.

Set the key in the server environment before starting PY-OS Cloud:

```sh
export OPENAI_API_KEY="your-api-key"
export OPENAI_MODEL="gpt-5.5"
python -m pyos_cloud
```

Keep the key private. Do not paste it into the app, commit it to GitHub, or put it in the APK. API usage may incur charges, so have a parent or guardian manage the API account and spending limits. Without the server key, the desktop and offline apps still work; only AI chat is unavailable.

## Connect web and Android

1. Start the Python server on the device that stores your PY-OS data.
2. In PY-OS, open **Settings** and enter the server URL under **Cloud server URL**.
3. Use the same URL in the web edition and APK. Both editions will then read and save to the same server state.
4. If no server is connected, each edition keeps its own local data. `.pycloud` export/import remains available for manual transfers.

The server binds to localhost by default. It has no user accounts or sign-in protection yet; keep it on the same device and do not expose it to the public internet or an untrusted network.

Python 3.10 or newer is recommended.

Choose another local port with:

```sh
python -m pyos_cloud --port 9000
```

## Saved data

PY-OS Cloud stores its shared state at `~/.py-os-cloud/state.json`. You can choose another folder with:

```sh
export PYOS_CLOUD_HOME="$HOME/my-pyos-data"
```

## Official Android app

**The official PY-OS Cloud Android APK has been added.** The Android app is available in the project's GitHub Releases. It embeds the same desktop interface as the web version.

GitHub Actions can build a debug APK as a workflow artifact. Version tags beginning with `v` also attach the APK to a GitHub Release.

## Tests

```sh
python -m unittest discover -s tests -v
```

## Project layout

- `pyos_cloud/` — shared Python core and local server.
- `web/` — browser desktop.
- `android/` — official Android APK project.
- `tests/` — core tests.
