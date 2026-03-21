# Privacy Policy — HeartBeep

**Last updated: 2026-03-21**

HeartBeep is a free, open-source Android application for monitoring heart rate via Bluetooth Low Energy (BLE) heart rate devices. This policy explains what data the app collects, how it is used, and what your rights are.

---

## Data collected

### Heart rate data
HeartBeep reads real-time heart rate measurements from a paired BLE heart rate device (such as a Polar H10 chest strap). This data is used only to display your current heart rate, trigger configurable BPM alerts, and record session history on your device.

### Session history
HeartBeep stores session history (start/end timestamps, heart rate measurements during the session) in a local database on your device. This data is never transmitted to any server or third party.

### Location data
HeartBeep may request location permission, which is required by Android for Bluetooth device scanning on Android 12 and below. Location data is used solely to estimate distance traveled during a session if you grant the permission. It is stored only locally as part of session history and is never transmitted.

---

## Data sharing

HeartBeep does not collect, transmit, or share any personal data with third parties. All data remains on your device.

---

## Data retention and deletion

All app data (session history, preferences) is stored locally on your device. You can delete all data at any time by uninstalling the app or using the in-app delete functionality. No data is stored elsewhere.

---

## Permissions

| Permission | Purpose |
|---|---|
| `BLUETOOTH_SCAN` | Scan for nearby BLE heart rate devices |
| `BLUETOOTH_CONNECT` | Connect to and read data from your heart rate device |
| `ACCESS_FINE_LOCATION` | Required by Android for BLE scanning; optionally used to track distance |
| `POST_NOTIFICATIONS` | Show BPM alert notifications |
| `FOREGROUND_SERVICE` | Keep the monitoring session running while the app is in the background |

---

## Open source

HeartBeep is open source. You can review the full source code at [github.com/RatyLasse/heartbeep](https://github.com/RatyLasse/heartbeep).

---

## Contact

If you have questions about this privacy policy, open an issue at [github.com/RatyLasse/heartbeep/issues](https://github.com/RatyLasse/heartbeep/issues).
