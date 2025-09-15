# SlitherSync 🐍

## A Fitness-Driven Twist on a Classic Game  

SlitherSync is a modern take on the classic Snake game, transforming your real-world movements into in-game action! By using your mobile device's sensors, every step you take in real life makes your snake move in the game. It's a fun and interactive way to get active and stay fit.  

---

### Key Features ✨

* **Step-Controlled Gameplay:** Your physical steps directly control the snake's movement.  
* **Integrated with Google Fit:** Tracks and displays your daily step count in real time.  
* **Real-Time Weather:** Shows live weather updates using the OpenWeatherMap API.  
* **Location Awareness:** Uses device location to fetch localized weather and optionally display the city name.  
* **Sensor Fusion Technology:** Utilizes accelerometer + gyroscope data for step-based controls.  
* **Classic Gameplay:** Nostalgic Snake fun with a modern, health-driven twist.  

---

### How It Works ⚙️  

SlitherSync uses your mobile phone's built-in sensors (accelerometer, gyroscope) to detect steps. These inputs are fused for accuracy and then translated into in-game commands for snake movement.  

The app connects to two main APIs:  
* **Google Fit API** → Fetches and displays daily step counts.  
* **OpenWeatherMap API** → Provides real-time weather data.  

Additionally, device **GPS location** is used to fetch coordinates, which can be reverse-geocoded into a city name for contextual weather info.  

---

### Technologies Used 💻  

* **Mobile Sensors (Accelerometer, Gyroscope)**  
* **Sensor Fusion**  
* **Google Fit API** (Fitness data integration)  
* **OpenWeatherMap API** (Weather updates)  
* **Fused Location Provider (Google Play Services)**  
* **Android + Kotlin + Ktor**  

---

### Current Limitations ⚠️  

* **Google Fit API Access:**  
  * While in testing phase, Google Fit API data may **not match the official Google Fit app**.  
  * Only **test users registered in the Google Cloud Console** can access fitness data until the app is verified.  
  * Real-time sync may have slight delays depending on network and Google Fit data refresh.  

---

### Future Improvements 🚀  

* **Google Fit API Verification:** Full production release so any user can connect their Google Fit account.  
* **Customizable Controls:** Allow users to switch between step-based and on-screen controls.  
* **Achievements & Rewards:** Gamify fitness further with goals, streaks, and badges.  
* **Offline Mode:** Cache weather and step data for limited offline functionality.  
* **Enhanced Geocoding:** Integrate a more reliable reverse geocoding API (e.g., Google Maps, Mapbox).  

---

### Why SlitherSync? 🎮  

SlitherSync turns **daily activity into gameplay**. It motivates you to walk more, move more, and enjoy the nostalgia of Snake — all while staying connected with your fitness goals and the world around you.  
