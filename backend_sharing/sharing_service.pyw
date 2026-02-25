import socket
import os
import threading
import time

# Configuration
PORT_WIFI = 5009  # Using a different port to avoid conflict with main server and watchdog
SAVE_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "Received_Files")
NEWLINE = "\n"

if not os.path.exists(SAVE_DIR):
    os.makedirs(SAVE_DIR)

def handle_client(client_socket, addr):
    print(f"[*] Connection from {addr}")
    try:
        # 1. Read Header byte-by-byte until newline (filename|filesize\n)
        header_bytes = b""
        while True:
            byte = client_socket.recv(1)
            if not byte:
                break
            if byte == b"\x0a":  # newline character
                break
            header_bytes += byte

        if not header_bytes:
            return

        header = header_bytes.decode('utf-8').strip()
        filename, filesize = header.split("|")
        filesize = int(filesize)

        # Security: Prevent path traversal
        filename = os.path.basename(filename)
        filepath = os.path.join(SAVE_DIR, filename)

        print(f"[*] Receiving {filename} ({filesize} bytes)...")

        # 2. Receive file content
        with open(filepath, 'wb') as f:
            remaining = filesize
            while remaining > 0:
                chunk = client_socket.recv(min(remaining, 8192))
                if not chunk:
                    break
                f.write(chunk)
                remaining -= len(chunk)

        print(f"[*] Successfully saved to {filepath}")
    except Exception as e:
        print(f"[!] Error: {e}")
    finally:
        client_socket.close()

def start_wifi_server():
    server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server.bind(("0.0.0.0", PORT_WIFI))
    server.listen(5)
    print(f"[*] Wi-Fi Sharing Service started on port {PORT_WIFI}")
    
    while True:
        client, addr = server.accept()
        threading.Thread(target=handle_client, args=(client, addr), daemon=True).start()

def start_bluetooth_server():
    """Start Bluetooth RFCOMM server using native Python sockets (Python 3.9+ on Windows)."""
    try:
        if not hasattr(socket, "AF_BLUETOOTH"):
            print("[!] Bluetooth disabled - requires Python 3.9+ with AF_BLUETOOTH support.")
            return

        BTPROTO_RFCOMM = 3
        server = socket.socket(socket.AF_BLUETOOTH, socket.SOCK_STREAM, BTPROTO_RFCOMM)
        # Bind to all adapters, RFCOMM channel 1
        server.bind(("00:00:00:00:00:00", 1))
        server.listen(1)

        print("[*] Bluetooth Sharing Service started on RFCOMM Channel 1")

        while True:
            try:
                client, addr = server.accept()
                print(f"[*] Bluetooth connection from {addr}")
                threading.Thread(target=handle_client, args=(client, addr), daemon=True).start()
            except Exception as e:
                print(f"[!] Bluetooth accept error: {e}")

    except Exception as e:
        print(f"[!] Bluetooth init error: {e}")
        print("[!] Make sure Bluetooth is on and not in use by another app.")

if __name__ == "__main__":
    # Start Wi-Fi listener in a thread
    wifi_thread = threading.Thread(target=start_wifi_server, daemon=True)
    wifi_thread.start()

    # Start Bluetooth listener in a thread
    bt_thread = threading.Thread(target=start_bluetooth_server, daemon=True)
    bt_thread.start()
    
    # Keep the main script alive
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print('[*] Service stopping...')
