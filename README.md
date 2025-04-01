# cs501_final_project
# 🐾WelliPet (Virtual Pet Health Companion)

A mobile health and wellness app that links your real-life habits to the well-being of a virtual pet. Stay healthy, stay happy — for you and your companion!

---

## 📱 Overview

**WelliPet** is an Android app that gamifies health tracking through emotional connection. By integrating your fitness, sleep, and hydration data with a virtual pet, the app encourages healthy behavior through positive reinforcement, daily challenges, and interactive rewards. Built with Kotlin, Jetpack Compose, and Android Health Connect, the app offers a playful and meaningful approach to wellness.

---

## 🎯 Features

- 🐶 **Virtual Pet**: Interact with a responsive pet that reflects your lifestyle.
- 📊 **Health Tracking**: Syncs sleep, steps, and hydration from Health Connect.
- 🏆 **Gamified Progress**: Earn rewards, unlock pet items, and maintain streaks.
- 📍 **Sensor Integration**:
  - GPS: Location-based walking logs.
  - Accelerometer: Motion detection.
- 🌙 **Sleep Affects Pet Mood**: Poor sleep? Your pet will be sluggish too!
- 🧭 **Multi-Device Support**: 
  - Phone: Full experience.
  - Smartwatch: Glanceable pet mood and health stats.
- 🖌️ **Customization & Store**: Buy outfits and items using earned points.
- 🌈 **Accessibility**: Voice commands, dark mode, text scaling, high-contrast UI.

---

## 🛠️ Tech Stack

| Category        | Tools & Frameworks                        |
|----------------|-------------------------------------------|
| Language        | Kotlin                                   |
| UI              | Jetpack Compose + Material Design         |
| Database        | Firebase (Auth + Sync) + Room (Local)     |
| Health API      | Android Health Connect                    |
| Sensors         | GPS, Accelerometer     |
| Testing Devices | Phone + smartwatch                        |

---

## 📸 Screenshots / Wireframes

> Coming soon...

---

## 🚧 Project Timeline

| Milestone      | Features Implemented                                      |
|----------------|------------------------------------------------------------|
| Week of Mar 16 | Proposal + GitHub setup + wireframes                      |
| Mar 31         | Firebase DB, basic Compose UI                        |
| Apr 13         | Health Connect integration, dual device testing           |
| Apr 27         | Sensors + animations                             |
| May 4          | Final version, polish, presentation & report              |

---

## 📦 Project Structure

```
com.example.wellipet
├── data
│   ├── model
│   │   ├── HealthData.kt         // Data model for health metrics (e.g., steps, sleep, hydration)
│   │   ├── PetStatus.kt          // Data model for pet status (e.g., mood, health progress)
│   │   └── User.kt               // Data model for user-related info
│   ├── repository
│   │   ├── HealthRepository.kt   // Handles Health Connect integration and local data access
│   │   ├── PetRepository.kt      // Manages pet state updates and logic
│   │   └── UserRepository.kt     // Handles user login, signup, and authentication (optional backend)
│   └── source
│       └── HealthConnectSource.kt// Direct communication with Health Connect
│
├── domain
│   └── usecase
│       ├── GetHealthDataUseCase.kt   // Business logic to fetch health data
│       ├── UpdatePetStatusUseCase.kt // Business logic to update pet status
│       └── UserAuthUseCase.kt        // Logic for handling login/signup flows
│
├── ui
│   ├── mobile
│   │   ├── home
│   │   │   ├── HomeScreen.kt         // Main screen (pet display, background, progress)
│   │   │   └── HomeViewModel.kt
│   │   ├── login
│   │   │   ├── LoginScreen.kt        // Login UI
│   │   │   └── LoginViewModel.kt
│   │   ├── signup
│   │   │   ├── SignUpScreen.kt       // Signup UI
│   │   │   └── SignUpViewModel.kt
│   │   ├── healthdata
│   │   │   ├── HealthDataScreen.kt   // Health data UI (lists and charts)
│   │   │   └── HealthDataViewModel.kt
│   │   └── store
│   │       ├── StoreScreen.kt        // Pet item store UI
│   │       └── StoreViewModel.kt
│   └── wear
│       ├── home
│       │   ├── WearHomeScreen.kt         // Simplified home UI for wearable
│       │   └── WearHomeViewModel.kt
│       └── healthdata
│           ├── WearHealthDataScreen.kt  // Simplified health data UI for wearable
│           └── WearHealthDataViewModel.kt
│
├── navigation
│   └── AppNavHost.kt               // Navigation graph and routing
│
└── utils
├── Extensions.kt               // Shared extension and utility functions
└── Constants.kt                // Constant definitions (e.g., keys, API URLs)

```


## 🙋‍♀️ Authors

- Chaojen Chiu ， Ruiyang Cao  

---

