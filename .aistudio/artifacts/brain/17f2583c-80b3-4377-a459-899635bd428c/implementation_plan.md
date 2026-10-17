# GitHub Repository Synchronization & Workspace Update

This plan outlines the workflow to pull the latest commits from your GitHub repository into this AI Studio workspace while preserving required AI Studio build and Firebase configurations so the app compiles and runs seamlessly in the preview emulator.

### User Review & Critical Decisions

> [!IMPORTANT]
> **Required Input Before Execution**: When you click **Proceed** or reply in chat, please provide:
> 1. Your **GitHub Repository URL** (e.g., `https://github.com/username/repo-name`)
> 2. The **Branch Name** you want to pull from (default: `main` or `master`)
> *(Note: If the repository is private, please either make it temporarily public or provide a read-only Personal Access Token / upload the modified files directly.)*

- **Confirmed Decision 1**: Pull changes from your GitHub repository into this workspace.
- **Confirmed Decision 2**: You will provide the GitHub repository URL and branch name.

---

### 1. Overview & Core Concept

- **What It Does**: Initializes a Git remote connection in the current AI Studio container, fetches the latest branch state from your GitHub repository, merges/applies your updated source code and resources, and protects AI Studio's mandatory signing and Firebase configuration files.
- **Target Audience / Persona**: Developers working across local Android Studio / Git and Google AI Studio Build who want their latest pushed code reflected in the live cloud emulator.
- **Key Value**: Brings all your local code edits into AI Studio in one step without breaking the cloud build pipeline, keystore signing, or Firebase connection.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Repository Fetch**: Connect the workspace to your provided GitHub URL and fetch the target branch.
  2. **Diff & Selective Sync**: Apply all updated Kotlin UI screens, ViewModels, repositories, and resources from your GitHub branch while preserving AI Studio environment-specific configuration (`debug.keystore`, `signingConfigs`, and `google-services.json`).
  3. **Build Verification**: Compile the updated Android application (`assembleDebug`) and refresh the streaming Android emulator.
- **Visual Identity & Theme**:
  - Preserves your existing **Celestial / Vedic Jyotisha** theme (`CelestialBg`, `CelestialGold`, `CelestialSurface`) alongside any visual or UI updates pushed to your GitHub branch.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Safe Config Preservation During Git Pull**
  - *Chosen Approach*: Back up and restore AI Studio's `signingConfigs` in `app/build.gradle.kts`, `debug.keystore`, `app/google-services.json`, and `metadata.json` after pulling from GitHub.
  - *Why*: Local Android Studio projects often have different Gradle versions, local keystore paths, or modified `google-services.json` files. Preserving the container's signing and SDK configuration ensures the streaming emulator can install the APK without signature mismatches.
  - *Alternatives Considered*: Blindly overwriting all root and Gradle files (`git reset --hard`), which would break AI Studio's APK signing and emulator installation.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

- **Architecture & Sync Pipeline Diagram**:

```
┌──────────────────────────────────────────────────────────────┐
│                   GitHub Remote Repository                   │
│             (User-provided URL & Target Branch)              │
└──────────────────────────────┬───────────────────────────────┘
                               │ git fetch / checkout
                               ▼
┌──────────────────────────────────────────────────────────────┐
│                 AI Studio Workspace Staging                  │
│  ┌────────────────────────┐      ┌────────────────────────┐  │
│  │  Synced from GitHub    │      │ Preserved Cloud Config │  │
│  │  • Kotlin Source Code  │      │ • debug.keystore       │  │
│  │  • Compose Screens     │      │ • signingConfigs       │  │
│  │  • Res / Strings / XML │      │ • google-services.json │  │
│  │  • Domain & Data Logic │      │ • SDK 36 / AGP Compat  │  │
│  └───────────┬────────────┘      └───────────┬────────────┘  │
└──────────────┼───────────────────────────────┼───────────────┘
               └───────────────┬───────────────┘
                               ▼
┌──────────────────────────────────────────────────────────────┐
│          Gradle Verification (compile_applet) & APK          │
└──────────────────────────────────────────────────────────────┘
```

- **Sync & Verification Sequence**:
  1. Receive the GitHub repository URL and branch name from you.
  2. Initialize git or fetch the remote archive into a staging directory.
  3. Diff and copy updated source code and resources into the workspace.
  4. Verify `app/build.gradle.kts` retains the required AI Studio `signingConfigs` and `compileSdk` compatibility.
  5. Run `compile_applet` to verify a clean build and summarize every synced change.
