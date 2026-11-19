# Simple TV

A very light IPTV player for Android TV, phones and tablets. It loads an M3U
playlist, shows a searchable channel list, and plays the stream you pick with
ExoPlayer (Media3). Works with a TV remote (D-pad) and with touch.

- **Playlist:** `https://romaxa55.github.io/world_ip_tv/output/index.m3u`
  (change it in [`MainActivity.kt`](app/src/main/java/com/simpletv/player/MainActivity.kt), the `playlistUrl` line)
- **Min Android:** 5.0 (API 21) — covers almost every Android TV in use
- **No heavy libraries** — Media3 + a tiny built-in image loader

## Get the APK without installing anything (GitHub Actions)

1. Create a free account at https://github.com if you don't have one.
2. Make a new repository (e.g. `simple-tv`). Keep it empty (no README).
3. Upload this whole folder to the repo. Two easy ways:
   - **Web:** on the repo page click **Add file → Upload files**, drag in all
     the files and folders here (including the `.github` folder), then **Commit**.
   - **Git:** from this folder run:
     ```bash
     git init
     git add .
     git commit -m "Simple TV"
     git branch -M main
     git remote add origin https://github.com/<you>/simple-tv.git
     git push -u origin main
     ```
4. Go to the repo's **Actions** tab. The **Build APK** job runs automatically on
   push (or click **Run workflow**). Wait ~3–5 minutes for the green check.
5. Open the finished run, scroll to **Artifacts**, and download
   **SimpleTV-debug-apk**. Unzip it to get `app-debug.apk`.

## Install on a device

- **Android TV / box:** copy the APK to a USB stick or use an app like
  *Downloader* / *Send Files to TV*, then open it. Allow "install from unknown
  sources" when asked. It appears on the home screen with the other apps.
- **Phone/tablet:** open the APK file and tap Install (allow unknown sources).

## Build locally instead (optional)

Open the folder in **Android Studio** (it will download the Gradle wrapper and
SDK bits), then **Build → Build Bundle(s)/APK(s) → Build APK(s)**.

## Notes

- This is a **debug** APK — fine for personal use and sideloading. For the Play
  Store you'd sign a release build.
- The app only plays what's in the playlist; it hosts no content itself.
- Some channels in any public playlist may be offline or geo-blocked; the app
  shows a short "cannot play" message and you can pick another.
