# DevSync

**Your laptop's errors, solved on your phone. Offline. Private.**

## The Problem

Developers spend hours debugging build failures and cryptic error messages. When errors occur:
- You're interrupted from your flow state
- You paste sensitive logs into online AI tools, exposing API keys and credentials
- You wait for cloud-based AI responses
- Your debugging history is stored on external servers

## Our Solution

DevSync is an Android application that brings AI-powered debugging directly to your phone—completely offline and privacy-first.

### Core Architecture

**1. Laptop CLI Watcher & Local Wi-Fi Bridge**

A lightweight terminal watcher (`$ devsync watch`) detects non-zero build or test exits on the laptop and streams the failure over local WebSocket to the phone.

**2. Deterministic Privacy Gate (Before AI)**

Strict pattern-based rules strip API keys, Bearer tokens, .env credentials, and private filesystem paths before any model inference occurs.

**3. On-Device 4-Bit Local AI & Phone Inputs**

A compact quantized local model (with zero internet permission and 0 B sent to the cloud) explains the root cause in plain English, supported by Camera OCR and offline voice commands.

**4. Human Control & Offline Fix Cards**

Every suggested command requires manual developer approval and can be saved on-device as a reusable Fix Card for teammates on the local network.

---

## Notice for Hackathon Judges

This application is an interactive MVP prototype showcase illustrating what we are going to build during the hackathon.

It demonstrates our end-to-end product workflow—specifically highlighting the User Onboarding Flow, the Deterministic Privacy Gate (which scrubs secrets locally), and the Primary Task Dashboard—so you can experience how DevSync operates in practice.

### How to Test This Prototype

1. Walk through the 3-step User Onboarding Flow (Laptop Pairing, Privacy Gate Rules, and Local 4-Bit Model Setup).

2. Explore the Primary Task Dashboard: inspect scrubbed secrets, trigger simulated laptop build crashes, paste custom terminal logs, and manually approve fixes.

3. Test the Camera OCR to Markdown scanner, Offline Voice triggers, and persistent local Fix Cards.

---

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (local SQLite)
- **Design System**: Neo-Brutalist Swiss style with Space Grotesk and JetBrains Mono fonts

---

## Build Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34 (API Level 34)

### Build the Project

```bash
# Clone the repository
git clone https://github.com/harshqs/DEVSYNC_.git
cd DEVSYNC_

# Build the project
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

---

## Design Philosophy

DevSync embraces neo-brutalist design principles—thick borders, high contrast, no gradients, and developer-friendly typography—to create an interface that respects user privacy and developer craft.

**Debug anywhere. Leak nothing.**
