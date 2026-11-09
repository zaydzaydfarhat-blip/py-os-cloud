"""Safe .pycloud backup creation and restoration."""

from __future__ import annotations

import io
import json
import zipfile
from typing import Any

from . import __version__
from .store import validate_state

FORMAT_VERSION = 1
MAX_BACKUP_BYTES = 20 * 1024 * 1024


def export_backup(state: dict[str, Any]) -> bytes:
    clean = validate_state(state)
    manifest = {
        "format": "pycloud",
        "format_version": FORMAT_VERSION,
        "app_version": __version__,
    }
    output = io.BytesIO()
    with zipfile.ZipFile(output, "w", compression=zipfile.ZIP_DEFLATED) as archive:
        archive.writestr("manifest.json", json.dumps(manifest, indent=2))
        archive.writestr("state.json", json.dumps(clean, indent=2, ensure_ascii=False))
    return output.getvalue()


def import_backup(data: bytes) -> dict[str, Any]:
    if len(data) > MAX_BACKUP_BYTES:
        raise ValueError("Backup is too large (20 MB maximum).")
    try:
        with zipfile.ZipFile(io.BytesIO(data), "r") as archive:
            names = set(archive.namelist())
            if not {"manifest.json", "state.json"}.issubset(names):
                raise ValueError("This is not a valid .pycloud backup.")
            manifest_info = archive.getinfo("manifest.json")
            state_info = archive.getinfo("state.json")
            if manifest_info.file_size > 64_000 or state_info.file_size > MAX_BACKUP_BYTES:
                raise ValueError("Backup contains an oversized metadata file.")
            manifest = json.loads(archive.read("manifest.json"))
            if manifest.get("format") != "pycloud":
                raise ValueError("Unrecognized backup format.")
            if manifest.get("format_version") != FORMAT_VERSION:
                raise ValueError("This .pycloud version is not supported.")
            state = json.loads(archive.read("state.json"))
    except (zipfile.BadZipFile, KeyError, json.JSONDecodeError) as exc:
        raise ValueError("The selected file is not a readable .pycloud backup.") from exc
    return validate_state(state)
