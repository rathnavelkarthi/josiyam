# Phase 5 Baseline Report: Jyotisha

**Date:** October 10, 2026  
**Environment:** Android Gradle (Kotlin DSL), Jetpack Compose, Firebase Auth, Firestore, Room, CameraX, MediaPipe.  

## 1. Baseline Build & Test Status
- **Build System:** Gradle `compile_applet` compiles successfully without fatal errors.
- **Dependencies:** Kotlin 2.x, Compose BOM, Room, Firebase BOM, CameraX, MediaPipe Tasks Vision.
- **Missing / Partial Configurations:** 
  - External FastAPI backend endpoint is configured via mock/local calculation fallback where server is unreachable.
  - Google Sign-In Credential Manager is stubbed/mocked for offline test environments.

## 2. Modules to Verify
1. **Auth & Ownership Security:** Firebase Auth, Google Sign-In, Firestore rules.
2. **CameraX & MediaPipe Inference:** Palm and Face local feature extraction pipeline.
3. **Astronomical Calculation Engine:** Sidereal/Tropical calculations, Ayanamsha, D1/D9 charts.
4. **Tri-Engine & Numerology:** Chaldean & Pythagorean calculations, source citations.
5. **Life Events & Prediction Ledger:** Immutability, historical context, outcome evaluation.
