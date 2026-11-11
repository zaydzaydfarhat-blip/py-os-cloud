"""Small offline assistant bundled inside the PY-OS Android APK."""
import re
from datetime import datetime

def reply(message: str) -> str:
    text = str(message or "").strip()
    value = text.lower()
    if not text:
        return "Type a message and I will try to help."
    if re.match(r"^(hi|hello|hey|yo)\b", value):
        return "Hello! I am PY-OS AI, the built-in offline Python assistant. I can help with PY-OS, basic coding, maths and general questions."
    if any(word in value for word in ("what can you do", "help", "commands")):
        return "I am the offline PY-OS assistant. I can explain PY-OS apps, help with simple Python and JavaScript questions, explain basic maths, and give general guidance. I do not use the internet and may not know recent information."
    if "who are you" in value or "your name" in value:
        return "I am PY-OS AI, a small built-in assistant running locally inside PY-OS using Python."
    if "py-os" in value or "pyos" in value:
        return "PY-OS Cloud is your desktop project. Use File Manager for virtual files, Notes for writing, Calculator for arithmetic, Mini Games for games, and Settings for appearance and backups."
    if "python" in value:
        return 'Python is a programming language. For example, print("Hello, world!") displays a message. You can use variables, functions, loops and modules to build programs.'
    if "javascript" in value or re.search(r"\bjs\b", value):
        return 'JavaScript makes web pages interactive. For example: const message = "Hello!"; console.log(message);. In PY-OS, JavaScript handles the desktop interface.'
    if re.search(r"\b2\s*\+\s*2\b", value):
        return "2 + 2 = 4."
    if "thank" in value:
        return "You are welcome!"
    if "time" in value or "date" in value:
        return "Your device date and time are: " + datetime.now().astimezone().strftime("%Y-%m-%d %H:%M %Z") + "."
    if any(word in value for word in ("offline", "internet", "connect")):
        return "I work locally and do not need a server, internet connection, ChatGPT account, or API key. My answers are simpler than a large online AI model."
    return "I am running in offline mode, so my knowledge is limited. I can still help with PY-OS basics, simple programming explanations, and common questions. Try asking about Python, JavaScript, or a PY-OS app."
