"""Small JSON-backed state store shared by PY-OS Cloud clients."""

from __future__ import annotations

import json
import os
from pathlib import Path
from typing import Any

DEFAULT_STATE: dict[str, Any] = {
    "schema_version": 1,
    "settings": {
        "theme": "dark",
        "wallpaper": "default",
    },
    "apps": [],
    "files": [],
}


def data_directory() -> Path:
    """Return the user's PY-OS Cloud data directory."""
    override = os.environ.get("PYOS_CLOUD_HOME")
    root = Path(override).expanduser() if override else Path.home() / ".py-os-cloud"
    root.mkdir(parents=True, exist_ok=True)
    return root


class StateStore:
    """Load and save portable application state as JSON."""

    def __init__(self, path: Path | None = None) -> None:
        self.path = path or data_directory() / "state.json"

    def load(self) -> dict[str, Any]:
        if not self.path.exists():
            return json.loads(json.dumps(DEFAULT_STATE))
        try:
            state = json.loads(self.path.read_text(encoding="utf-8"))
        except (OSError, json.JSONDecodeError) as exc:
            raise ValueError(f"Could not read PY-OS Cloud state: {exc}") from exc
        return validate_state(state)

    def save(self, state: dict[str, Any]) -> dict[str, Any]:
        clean = validate_state(state)
        self.path.parent.mkdir(parents=True, exist_ok=True)
        temporary = self.path.with_suffix(".json.tmp")
        temporary.write_text(
            json.dumps(clean, indent=2, ensure_ascii=False) + "\n",
            encoding="utf-8",
        )
        temporary.replace(self.path)
        return clean


def validate_state(value: Any) -> dict[str, Any]:
    if not isinstance(value, dict):
        raise ValueError("State must be a JSON object.")
    if value.get("schema_version") != 1:
        raise ValueError("Unsupported PY-OS Cloud state version.")
    for key in ("settings", "apps", "files"):
        if key not in value:
            value[key] = {} if key == "settings" else []
    if not isinstance(value["settings"], dict):
        raise ValueError("'settings' must be an object.")
    if not isinstance(value["apps"], list) or not isinstance(value["files"], list):
        raise ValueError("'apps' and 'files' must be arrays.")
    return value
