# Copilot Instructions - Mobile Controller

> **Note**: This file should be moved to `.github/copilot-instructions.md` once the `.github` directory is created.

## Project Overview

Remote PC controller system with two components:

- **Android app** (Java) - smartphone acts as wireless touchpad/remote
- **Python server** - runs on Windows PC, executes commands received from phone

Communication uses custom UDP/TCP protocols with AES encryption, QR-based pairing, and HMAC authentication.

---

## Running the System

### Python Server (Windows PC)

```bash
cd python_server
python server_1.py              # Main server (UDP port 5005, TCP port 5006)
python qr_pairing.py            # Generate QR code for phone pairing
python watchdog.py              # Auto-restart server on crash (optional)
```

**Dependencies** (install via `pip`):

```
pyautogui screen-brightness-control psutil pyperclip win10toast pystray
Pillow customtkinter comtypes pycryptodome pycaw opencv-python numpy qrcode
```

### Android App

- **Location**: `MainActivity/` contains flattened Java source files (no Gradle structure in repo)
- **Package**: `com.prajwal.myfirstapp`
- **Build**: Import sources into Android Studio to build APK
- **Min SDK**: ~API 21 (Android 5.0)

---

## Architecture

### Communication Channels

| Purpose                              | Protocol      | Port  | Direction     |
| ------------------------------------ | ------------- | ----- | ------------- |
| Commands (mouse, keyboard, etc.)     | UDP           | 5005  | Phone → PC    |
| File transfers                       | TCP           | 5006  | Bidirectional |
| Server discovery                     | UDP broadcast | 37020 | Phone → PC    |
| Reverse commands (PC controls phone) | UDP           | 6000  | PC → Phone    |
| Heartbeat/ACK                        | UDP           | 6001  | Phone → PC    |
| Watchdog control                     | UDP           | 5007  | Phone → PC    |

### Security & Pairing

1. **QR Pairing**: Run `qr_pairing.py` → generates QR with `IP:PORT:AES_KEY:HMAC_KEY:SECRET_KEY`
2. **Phone scans** → stores keys in `SecurityUtils` (SharedPreferences)
3. **Packet format**: `ENCRYPTED_CMD|TIMESTAMP|SIGNATURE`
   - AES-128-CBC encryption (toggleable via `config.USE_ENCRYPTION`)
   - HMAC-SHA256 signature of `payload + timestamp`
   - Replay attack protection: ±10 second window
4. **Key storage**:
   - Server: `pairing_keys.json` (loaded by `config.py`)
   - Phone: Android SharedPreferences

---

## Key Conventions

### Command Protocol (Phone → PC)

Commands are string-based, sent via UDP to port 5005. After decryption:

```
MOUSE_MOVE:dx,dy          # Relative mouse movement
TASK_ADD:title:priority   # Add task (queued if offline)
CHAT_MSG:hello            # Chat message
SYNC_HANDSHAKE:tasks_since=2024-02-25T10:30:00  # Delta sync
```

All handlers live in `commands.py` → `execute_command(data, addr, sock)`

### Reverse Commands (PC → Phone)

Sent via UDP to port 6000. Handled by `ReverseCommandListener.java`:

```
TOAST:message             # Show toast on phone
TASKS_SYNC:{json}         # Full task list
SYNC_DELTA:{json}         # Incremental sync (only changes)
VIBRATE:500               # Vibrate 500ms
```

### Offline Sync ("Outbox" Pattern)

**Problem**: Commands sent while PC is offline are lost.

**Solution**: Queue & Reconcile with conflict resolution.

**Android Side**:

- `SyncOutbox.java` queues failed data commands to `SharedPreferences`
- `ConnectionManager.sendDataCommand()` auto-queues if server unreachable
- Background monitor (`ConnectionManager.startConnectionMonitor()`) flushes queue when server comes back online

**Python Side**:

- Tasks have `last_modified` timestamp (ISO-8601)
- `get_tasks_since(timestamp)` returns delta (tasks modified after given time)
- `SYNC_HANDSHAKE:tasks_since=<ts>` triggers delta response

**Flow**:

1. Phone stores `LAST_SYNC_TS` (most recent task timestamp)
2. On reconnect → sends `SYNC_HANDSHAKE:tasks_since=<LAST_SYNC_TS>`
3. Server responds with `SYNC_DELTA:{tasks: [...]}`
4. Phone merges using **last-write-wins**: newer `last_modified` wins
5. Phone updates `LAST_SYNC_TS` after successful merge

### Global State (Python)

`config.py` acts as a shared state module — imported and mutated directly:

```python
import config
config.preview_active = True  # Set by commands.py, read by services.py
config._phone_ip = "192.168.1.50"
```

All ports, gesture templates, and encryption keys are defined here.

### Threading Model

- **Server**: Each UDP command handled in a daemon thread
- **Background services**: Preview streaming, reverse command sender run as persistent daemons
- **Session management**: Old session threads stopped before starting new ones on IP change

### Data Persistence

**Python** (JSON files in `python_server/`):

- `tasks.json` — task list with `last_modified` timestamps
- `notes_data.json` — hierarchical notes tree
- `calendar_data.json` — calendar events
- `chat_history.json` — chat messages
- `pairing_keys.json` — crypto keys (regenerated by `qr_pairing.py`)

**Android** (SharedPreferences):

- Task manager: `task_manager_prefs → tasks_json`
- Sync outbox: `sync_outbox_prefs → pending_commands`
- Last sync: `task_manager_prefs → last_sync_ts`

---

## Module Breakdown

### Python Server Core

| File              | Purpose                                                    |
| ----------------- | ---------------------------------------------------------- |
| `server_1.py`     | Main UDP listener; dispatches to `commands.py`             |
| `commands.py`     | Command router + all `CMD:` handlers                       |
| `config.py`       | Global settings, ports, encryption keys, gesture templates |
| `qr_pairing.py`   | Generate pairing QR codes                                  |
| `sync_manager.py` | Handles `SYNC_HANDSHAKE`, builds delta responses           |

### Python Modules (Feature-specific)

| Module                    | Functionality                             |
| ------------------------- | ----------------------------------------- |
| `task_manager.py`         | CRUD for tasks, delta sync, notifications |
| `notes_module.py`         | Hierarchical notes (folders + files)      |
| `calendar_module.py`      | Calendar events                           |
| `chat_module.py`          | Bidirectional messaging                   |
| `file_service.py`         | TCP file transfer server                  |
| `reverse_commands.py`     | PC→Phone command sender (port 6000)       |
| `system_monitor.py`       | CPU/RAM/battery stats streaming           |
| `media_controller.py`     | Audio control via `pycaw`                 |
| `notification_manager.py` | Mirror phone notifications to PC          |
| `clipboard_sync.py`       | Sync clipboard bidirectionally            |

### Android Key Files

| File                          | Purpose                                        |
| ----------------------------- | ---------------------------------------------- |
| `MainActivity.java`           | Entry point, touchpad logic                    |
| `ConnectionManager.java`      | UDP/TCP sender, encryption, outbox integration |
| `SecurityUtils.java`          | AES + HMAC crypto operations                   |
| `SyncOutbox.java`             | Offline queue with retry logic                 |
| `QRPairingManager.java`       | Scan QR, extract server IP + keys              |
| `ReverseCommandListener.java` | UDP listener (port 6000) for PC commands       |
| `TaskManagerActivity.java`    | Task CRUD, delta sync merge logic              |
| `KeepAliveService.java`       | Background service maintains connection        |

---

## Important Patterns

### Adding a New Command

**Python** (`commands.py`):

```python
elif command.startswith("MY_CMD:"):
    payload = command.split(":", 1)[1]
    # Do something
    return "MY_CMD_RESPONSE"
```

**Android** (`ConnectionManager.java`):

```java
connectionManager.sendCommand("MY_CMD:" + data);
```

### Adding a Reverse Command (PC → Phone)

**Python** (`reverse_commands.py`):

```python
def my_action():
    send_to_phone("MY_ACTION:payload")
```

**Android** (`ReverseCommandListener.java` in `handleCommand()`):

```java
else if (command.startsWith("MY_ACTION:")) {
    String data = command.substring(10);
    // Handle on main thread if UI update needed
    mainHandler.post(() -> doSomething(data));
}
```

### Reliable Data Commands (Use Outbox)

For commands that must **never be lost** (tasks, notes, calendar):

```java
// DON'T use sendCommand() — it fires and forgets
// DO use sendDataCommand() — queues if offline
connectionManager.sendDataCommand(context, "TASK_ADD:Buy milk:high");
```

### Conflict Resolution

All synced data (tasks, notes, calendar) uses **last-write-wins**:

- Every item has `last_modified` (ISO-8601 timestamp)
- On conflict: newer timestamp wins
- Updated on every mutation (add, edit, delete, complete)

---

## Testing Sync Offline Behavior

1. Turn off PC server
2. Add task on phone → queued in `SyncOutbox`
3. Check queue: `outbox.getPendingCount()` should be > 0
4. Turn on PC server
5. Background monitor detects server → auto-flushes queue
6. Phone sends `SYNC_HANDSHAKE` with last sync timestamp
7. Server responds with `SYNC_DELTA` containing only new/changed tasks
8. Phone merges delta using last-write-wins

---

## Logging

Both systems use print-based logging with emoji prefixes:

```
[*] Server started
[Security] Invalid signature
[💬 Chat] Message received
[TaskManager] Task added: Buy milk (from mobile)
[SyncManager] Tasks delta since 2024-02-25T10:30:00: 3 item(s)
```

No logging framework — output goes to console/logcat.

---

## Common Issues

### "Packet not delivered" / Commands ignored

- Check firewall on PC (allow UDP 5005, TCP 5006)
- Verify phone and PC on same Wi-Fi network
- Check `pairing_keys.json` matches phone's stored keys

### Tasks not syncing after reconnect

- Check `LAST_SYNC_TS` in `task_manager_prefs` (should update after sync)
- Verify `last_modified` field exists on all tasks (migration runs on `_load_tasks()`)
- Check server logs for `SYNC_HANDSHAKE` + `SYNC_DELTA` messages

### "Encryption error"

- Toggle `config.USE_ENCRYPTION = False` for debugging
- Re-pair phone with fresh QR code (`qr_pairing.py`)
- Check AES key length = 16 bytes

---

## Build & Test Commands

**Python Server**:

```bash
# No build step — just run:
python server_1.py

# Test ping:
# From Android, send "PING" → should get "PONG" back
```

**Android**:

```bash
# No standard Gradle files in repo
# Import MainActivity/ sources into Android Studio manually
# Build APK via Android Studio GUI
```

**Run Tests**: None currently. Manual testing via UI.

**Linting**: None currently.
