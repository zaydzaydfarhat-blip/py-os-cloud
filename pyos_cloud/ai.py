"""Server-side OpenAI Responses API adapter for PY-OS Cloud."""
from __future__ import annotations
import json
import os
from urllib.error import HTTPError, URLError
from urllib.request import Request, urlopen

def ai_status() -> dict:
    return {"configured": bool(os.environ.get("OPENAI_API_KEY")),
            "model": os.environ.get("OPENAI_MODEL", "gpt-5.5")}

def create_reply(messages: list[dict]) -> str:
    api_key = os.environ.get("OPENAI_API_KEY")
    if not api_key:
        raise RuntimeError("AI is not configured. Set OPENAI_API_KEY on the PY-OS Cloud server.")
    clean = []
    for item in messages[-20:]:
        if not isinstance(item, dict) or item.get("role") not in ("user", "assistant"):
            continue
        content = item.get("content")
        if isinstance(content, str) and content.strip():
            clean.append(f"{item['role'].upper()}: {content.strip()[:4000]}")
    if not clean or not any(m.startswith("USER:") for m in clean):
        raise ValueError("Send a message first.")
    payload = {
        "model": os.environ.get("OPENAI_MODEL", "gpt-5.5"),
        "instructions": ("You are PY-OS AI, the helpful assistant built into PY-OS Cloud. "
          "Be friendly, clear, age-appropriate, and concise. Help with learning, creative "
          "ideas, and safe coding. Never claim to control the device or perform actions "
          "you have not actually performed."),
        "input": "\n\n".join(clean),
        "max_output_tokens": 700,
        "store": False,
    }
    request = Request("https://api.openai.com/v1/responses",
        data=json.dumps(payload).encode("utf-8"),
        headers={"Authorization": f"Bearer {api_key}", "Content-Type": "application/json"},
        method="POST")
    try:
        with urlopen(request, timeout=60) as response:
            result = json.loads(response.read().decode("utf-8"))
    except HTTPError as exc:
        detail = exc.read().decode("utf-8", errors="replace")[:500]
        raise RuntimeError(f"AI provider returned HTTP {exc.code}: {detail}") from exc
    except (URLError, TimeoutError) as exc:
        raise RuntimeError(f"Could not reach the AI provider: {exc}") from exc
    chunks = []
    for item in result.get("output", []):
        if item.get("type") == "message":
            chunks.extend(part.get("text", "") for part in item.get("content", [])
                          if part.get("type") == "output_text")
    answer = "\n".join(chunks).strip()
    if not answer:
        raise RuntimeError("The AI provider returned an empty response.")
    return answer
