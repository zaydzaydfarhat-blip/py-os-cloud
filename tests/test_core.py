import tempfile
import unittest
from pathlib import Path

from pyos_cloud.backup import export_backup, import_backup
from pyos_cloud.store import StateStore


class CloudCoreTests(unittest.TestCase):
    def test_state_round_trip(self):
        with tempfile.TemporaryDirectory() as directory:
            store = StateStore(Path(directory) / "state.json")
            state = store.load()
            state["settings"]["theme"] = "light"
            store.save(state)
            self.assertEqual(store.load()["settings"]["theme"], "light")

    def test_pycloud_round_trip(self):
        state = {
            "schema_version": 1,
            "settings": {"theme": "everforest"},
            "apps": ["clock"],
            "files": [],
        }
        self.assertEqual(import_backup(export_backup(state)), state)

    def test_reject_invalid_backup(self):
        with self.assertRaises(ValueError):
            import_backup(b"not a backup")


if __name__ == "__main__":
    unittest.main()
