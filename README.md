# PY-OS Cloud ☁️

PY-OS Cloud is a cross-platform desktop project with a shared data format and core.

The project is being built in stages so the Android APK and browser edition can share the same settings, app data, and portable backups.

## Current status

**0.1.0 — shared-core preview**

- JSON-backed shared state store.
- Portable `.pycloud` export and import.
- Local browser interface served by Python.
- Browser controls for changing the theme and importing/exporting backups.
- Python standard library only; no third-party packages required.

The full window manager, taskbar, Start menu, file manager, and built-in browser are planned next. They are not implemented in this preview yet.

## Run on Termux, Linux, or macOS

From the repository directory:

```sh
python -m pyos_cloud
```

Then open this address in your browser:

```text
http://127.0.0.1:8765
```

Stop the server with `Ctrl+C`.

Python 3.10 or newer is recommended. The server binds to localhost by default, so it is only reachable from the same device. Do not bind it to a public network: authentication and HTTPS are not implemented yet.

## Commands

```sh
python -m pyos_cloud                 # start local web edition
python -m pyos_cloud --port 9000     # choose another local port
```

## Data and backups

State is stored at `~/.py-os-cloud/state.json`. You can override the location with:

```sh
export PYOS_CLOUD_HOME="$HOME/my-pyos-data"
```

The browser interface has **Export .pycloud** and **Import .pycloud** controls. A `.pycloud` file is a ZIP archive containing a versioned `manifest.json` and `state.json`. Import validates the format and state schema before saving. Backups currently contain app state only; user documents and browser history are not included yet.

## Run tests

```sh
python -m unittest discover -s tests -v
```

## Planned architecture

- `pyos_cloud/` — shared Python core and backup format.
- `web/` — browser edition.
- `android/` — future Android APK wrapper.
- `tests/` — core tests.

The APK and web edition will use the same versioned state schema and `.pycloud` format. The Android app is a future target; this repository does not build an APK yet.

## Safety

The local server has no login system yet. Keep the default localhost binding. Do not expose the server to the internet or an untrusted network.
