<div align="center">

<img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
<img src="https://img.shields.io/badge/Hilt-0059B2?style=for-the-badge&logo=dagger&logoColor=white" />
<img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" />
<img src="https://img.shields.io/badge/Room-4CAF50?style=for-the-badge&logo=sqlite&logoColor=white" />
<img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" />

# ⚡ FocusAI (Pro Version)

### *Your AI-powered immersive focus companion*

> Transform the way you concentrate. FocusAI blends deep work science, ambient soundscapes, and intelligent insights to help you enter — and stay in — the flow state.

</div>

---

## 📖 Description

**FocusAI** is a premium, startup-grade Android productivity application. This version features a complete modernization of the tech stack (the "Pro Stack"), including reactive state management with **StateFlow**, dependency injection with **Hilt**, and high-fidelity media streaming with **ExoPlayer**.

Whether you're a student, developer, or creative, FocusAI adapts to your focus patterns and gives you actionable insights via **Lumina**, your integrated AI coach.

---

## ✨ Key Features (Pro)

| Feature | Description |
|---|---|
| 🔐 **Authentication** | Email/Password + Google Sign-In via Firebase & modern Credential Manager |
| 🧠 **Immersive Sessions** | Redesigned cinematic focus mode with breathing orb, glassmorphism, and live equalizer |
| 🎵 **ExoPlayer Streaming** | Stable high-fidelity streaming from Radio-Browser API (Ambient/Lofi stations) |
| ✨ **Lumina AI Chat** | Personalized AI coach with "thinking" simulation and keyword-aware insights |
| 📊 **Advanced Charts** | Professional weekly activity visualization using **MPAndroidChart** |
| 🌊 **Shimmer Effects** | Smooth loading states using Facebook Shimmer for a premium feel |
| 🔔 **Smart Reminders** | Daily background focus reminders powered by **WorkManager** |
| 🎨 **Bento Grid UI** | Modern dashboard inspired by high-end design systems |

---

## 📱 UI Preview

```
┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐
│  🏠 HOME            │  │  ⚡ SESSION          │  │  ✨ LUMINA IA       │
│                     │  │                     │  │                     │
│  👋 Bonjour         │  │  SESSION EN COURS   │  │  Focus Score: 78   │
│  🔥 5 jours streak  │  │  ⚡ Mode Immersif   │  │                     │
│                     │  │        ◷             │  │  [Sessions] [Time]  │
│  ╭───────────────╮  │  │      25:00           │  │  [Best]             │
│  │  ◷  FOCUS  ◷  │  │  │   ╭─────────╮       │  │                     │
│  │   (orb orb)   │  │  │   │  FOCUS  │       │  │  ┌────────────────┐ │
│  ╰───────────────╯  │  │   ╰─────────╯       │  │  │ (shimmer...)   │ │
│                     │  │                     │  │  │ ✨ Lumina      │ │
│  ┌───────┬────────┐ │  │  ●●○○○ Nightwave    │  │  │ "Bonjour !..." │ │
│  │ 15m   │  Stats │ │  │  (Equalizer ▂▃▅▆)   │  │  └────────────────┘ │
│  │ 30m ✓ │  0h 00 │ │  │  [⏸]──────────[✕]  │  │  > Posez une quest. │
│  └───────┴────────┘ │  │                     │  │                     │
│  [⚡ Démarrer]      │  │                     │  │  [Bilan][Conseil]   │
└─────────────────────┘  └─────────────────────┘  └─────────────────────┘
```

---

## 🛠 Tech Stack (The Pro Stack)

### Core & Architecture
| Technology | Purpose |
|---|---|
| **Kotlin Coroutines / Flow** | Modern reactive stream management (StateFlow) |
| **Hilt (Dagger)** | Industry-standard dependency injection |
| **MVVM Architecture** | Clean separation of UI, Logic, and Data |
| **Timber** | Professional logging system (secure for production) |

### Networking & Data
| Technology | Purpose |
|---|---|
| **Retrofit 2 + OkHttp** | Type-safe REST client for Radio-Browser API |
| **Room 2.6** | Local persistence with KSP support |
| **WorkManager** | Reliable background task scheduling for reminders |

### Media & UI
| Technology | Purpose |
|---|---|
| **ExoPlayer (Media3)** | High-performance audio streaming engine |
| **MPAndroidChart** | Professional data visualization |
| **Facebook Shimmer** | Skeleton loading animations |
| **Material 3** | Latest Android design components |

---

## 🏗 Architecture Overview

```
┌──────────────────────────────────────────────────────────┐
│                        UI LAYER                          │
│  Activities ──── ViewBinding ──── StateFlow (Collect)    │
└───────────────────────┬──────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────┐
│                   VIEWMODEL LAYER                        │
│  @HiltViewModel ── StateFlow (Emit) ── ViewModelScope   │
└───────────────────────┬──────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────┐
│                   REPOSITORY LAYER                       │
│  @Singleton Repositories ──── Data Sources Abstraction   │
└───────────────────────┬──────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────┐
│                    DATA LAYER                            │
│  Room (DB) ─ Retrofit (API) ─ Firebase (Auth) ─ ExoPlayer │
└──────────────────────────────────────────────────────────┘
```

### Folder Structure (Updated)

```
app/src/main/java/com/focus/mob/
│
├── di/                         # Hilt Modules (DataModule, RepositoryModule)
├── data/
│   ├── AppDatabase.kt          # Room DB (v2)
│   ├── repository/             # Injected repositories
│   └── model/                  # Data classes (SessionRecord, RadioStation)
│
├── network/                    # Retrofit Interface & Client
├── worker/                     # WorkManager Tasks (FocusReminder)
│
├── ui/
│   ├── auth/                   # Login, SignUp, Welcome
│   ├── main/                   # Home, Stats (MPAndroidChart), Insights (Shimmer)
│   ├── session/                # Immersive Session (ExoPlayer)
│   └── viewmodel/              # @HiltViewModels (StateFlow based)
│
└── utils/                      # Navigation, Extensions, Timber trees
```

---

## 🚀 Installation Guide

### Prerequisites
- Android Studio **Ladybug** (2024.2+) or newer
- JDK **17** (IMPORTANT: Use Android Studio's bundled JBR)
- Android device or emulator with **API 31+**

### 1. Clone & Open
```bash
git clone https://github.com/your-username/focusai.git
```
In Android Studio: `File → Open` select the project.

### 2. JDK Configuration (Crucial)
Go to `File → Settings → Build, Execution, Deployment → Build Tools → Gradle`.
Set **Gradle JDK** to **JetBrains Runtime (jbr-17)**. 
*Note: Using a system GraalVM will cause `jlink.exe` compilation errors.*

### 3. Firebase Setup
1. Add `google-services.json` to the `app/` directory.
2. Enable Email/Password and Google sign-in in the Firebase Console.
3. Update `WEB_CLIENT_ID` in `LoginActivity.kt` with your Web Client ID.

---

## 🤝 Contributing

1. Fork the project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

MIT License - Copyright (c) 2026 FocusAI

---

## 🔮 Roadmap & Future Improvements

- [ ] **Firestore Sync**: Real-time cloud sync for focus stats.
- [ ] **Gemini AI**: Upgrade Lumina to use Google's Gemini API for dynamic coaching.
- [ ] **WearOS App**: Start sessions directly from your watch.
- [ ] **Social Goals**: Join focus rooms with friends.

---

<div align="center">

Made with ❤️ and ⚡ by the FocusAI team

*Stay in the zone. Every session counts.*

</div>
