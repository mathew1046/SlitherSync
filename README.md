# SlitherSync 🐍

## A Fitness-Driven Twist on a Classic Game

SlitherSync is a modern take on the classic Snake game, transforming your real-world movements into in-game action. By using your mobile device's sensors, every step you take in real life makes your snake move in the game. It's a fun and interactive way to get active and stay fit.

---

### Key Features ✨

* **Step-Controlled Gameplay:** Your physical steps directly control the snake's movement.  
* **Integrated with Google Fit:** Tracks and displays your daily step count in real time (see limitations below).  
* **Real-Time Weather:** Shows live weather updates using the OpenWeatherMap API.  
* **Location Awareness:** Uses device location to fetch localized weather and optionally display the city name.  
* **Sensor Fusion Technology:** Utilizes accelerometer + gyroscope data for step-based controls.  
* **Classic Gameplay:** Nostalgic Snake fun with a modern, health-driven twist.

---

### Demo & Download

* **Download APK:** [SlitherSync App](./SlitherSync.apk)  
* **Demo video:** [Watch on Google Drive](https://drive.google.com/file/d/1BaDDMSwiNeCuopvrDzFQ_Xg3csOomBmo/view?usp=sharing)

---

### How It Works ⚙️

SlitherSync uses your mobile phone's built-in sensors (accelerometer, gyroscope) to detect steps. These inputs are fused for accuracy and then translated into in-game commands for snake movement.

The app connects to two main APIs:  
* **Google Fit API** → Fetches and displays daily step counts.  
* **OpenWeatherMap API** → Provides real-time weather data based on device coordinates.

Device **GPS location** is used to fetch coordinates, which can be reverse-geocoded into a city name for contextual weather info.

---

### Technologies Used 💻

* **Mobile Sensors (Accelerometer, Gyroscope)**  
* **Sensor Fusion**  
* **Google Fit API** (Fitness data integration)  
* **OpenWeatherMap API** (Weather updates)  
* **Fused Location Provider (Google Play Services)**  
* **Android + Kotlin + Ktor**

---

### Installation & Run (Step-by-step)

> **Prerequisites:** An Android device (with Google Play services), USB / file access to copy the APK (or open via GitHub mobile), and an internet connection.

1. **Download & install APK**
   * Download `SlitherSync.apk` from the repository root or transfer it to your device.
   * If installing outside Play Store, enable **Install unknown apps** (Settings → Apps → choose installer → Allow from this source), then open the APK and install.

2. **First launch — grant runtime permissions**  
   On first run the app will request the permissions it needs. Grant these when prompted:
   * **Location** (`ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`) — used to fetch local weather.
   * **Activity recognition** (`ACTIVITY_RECOGNITION`) — required on Android 10+ to allow step detection / activity data.
   * **Internet** is required but is not a runtime permission (ensure network access is available).  
   If you accidentally deny a permission, enable it manually: Settings → Apps → SlitherSync → Permissions → Allow.

3. **Google Sign-in / Google Fit (important — test phase note)**
   * The app uses Google Sign-In to request fitness scopes (Google Fit). **Google Fit integration may not work for arbitrary accounts while the project is in testing**. To use Google Fit with this APK:
     1. Contact the app author to have your Google account added as a **test user** in the project’s OAuth consent screen.  
     2. Once added, sign into the app using that same Google account.  
     3. Grant the requested fitness permissions when prompted.  
   * If you don’t see data or the sign-in fails, it’s likely your account has not been added as a test user yet.

4. **Weather data**
   * The app uses OpenWeatherMap. If weather shows `"API Key Missing"`, check your internet connection or restart the app.

5. **Troubleshooting**
   * If step count reads `0` or very low: check that you granted `ACTIVITY_RECOGNITION`, and that Google Fit has data for that day.
   * If location or weather fails: ensure Location permission is allowed and device location services are enabled.
   * If APK won’t install: verify you enabled install from unknown sources and the APK is intact.

---

### Current Limitations ⚠️

* **Google Fit API (testing)**  
  * The app is currently in **test** mode for OAuth; only accounts added as **test users** in the Google Cloud Console can grant Fit scopes and return fitness data. See “Installation & Run” step 3 above for how to add test users.  
  * While testing, OAuth tokens and behavior have additional restrictions (e.g., refresh token rules). Expect small differences compared to production verification

---

### Future Improvements 🚀

* Google Fit verification & public release (remove test-user requirement).  
* Customizable controls (switch between step-based / on-screen).  
* Rewards / achievements to gamify fitness.  
* Improved reverse-geocoding (Mapbox / Google Maps) for more accurate place names.  
* Background-safe step aggregation with optimized battery usage.

---

### Why SlitherSync? 🎮

SlitherSync turns **daily activity into gameplay** — motivating users to walk more while enjoying the nostalgia of Snake. It combines simple, fun gameplay with live context (steps + weather + location).

---
