# Passmen

A simple and secure password manager app built with Android Studio and Kotlin.

---

## ✨ Features
- Securely save and manage passwords
- Built with Kotlin and Android Jetpack components
- Firebase integration for authentication and data storage
- Simple and clean UI

---

## 🔥 Firebase Setup (Important)

This app uses Firebase for authentication and database features.  
If you want to use it fully, follow these steps:

1. **Go to** [Firebase Console](https://console.firebase.google.com/) and create a new project.

2. **Set up Firebase for Android:**
    - Click **"Add app"** ➔ **Android**.
    - Enter your app's package name (e.g., `com.example.passmen`).
    - Download the generated `google-services.json` file.

3. **Place `google-services.json` inside your project:**
    - Put it inside the `/app/` folder.

4. **Enable Firebase services you need:**
    - Go to **Authentication** ➔ Enable **Email/Password** login.
    - Go to **Firestore Database** ➔ Create a database (in test mode for development).

5. **Sync your project:**
    - In Android Studio, click **"Sync Project with Gradle Files"**.

Now your app is fully integrated with your own Firebase backend!

---

## 📥 How to Build APK

1. **Clone the repository:**

    ```bash
    git clone https://github.com/darkstarexist/Passmen.git
    ```

2. **Open the project in Android Studio** (latest stable version recommended).

3. **Make sure you have:**
    - Android Studio installed
    - Kotlin plugin enabled
    - A physical device or emulator ready

4. **Build the APK:**
    - Go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
    - After the build, find your APK here:

      ```
      app/build/outputs/apk/debug/app-debug.apk
      ```

5. **Install** the APK on your Android device.

---


## 🛡️ Important Notes

- **Do not** commit your `google-services.json` to public GitHub repositories.
- **For production**, remember to secure your Firebase rules properly.
- If you see errors, try **File > Invalidate Caches / Restart** in Android Studio.

---

## 📜 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 🙌 Made with ❤️ by [Darkstar]
