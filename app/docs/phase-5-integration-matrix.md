# End-to-End Integration Matrix: Jyotisha

| User Journey | UI Entry Point | ViewModel / Use Case | Repository / API Path | Output | Persisted Data | Test Status | Classification |
| --- | --- | --- | --- | --- | --- | --- | --- |
| **Google Sign-In & Session** | AuthScreen / Welcome | AuthRepository | Firebase Auth / CredentialManager | Authenticated User Session | SharedPreferences / Auth State | Verified | `VERIFIED END-TO-END` |
| **Birth Profile CRUD** | ProfileScreen / Stepper | JyotishaRepository | Room / Firestore | Active Birth Profile | SQLite / Firestore | Verified | `VERIFIED END-TO-END` |
| **Natal Chart Calculation** | ChartExplorerScreen | AstronomicalCalculator | Local Engine | South Indian Chart (D1/D9) | SQLite / Room | Verified | `VERIFIED END-TO-END` |
| **Numerology & Tri-Engine** | NumerologyAndScanScreen | NumerologyEngine | Local Deterministic Engine | Chaldean/Pythagorean Numbers | Local State | Verified | `VERIFIED END-TO-END` |
| **Palm & Face Capture** | CameraCaptureScreen | CameraX / MediaPipe | Local Vision Runtime | Landmarks & Quality Metrics | Local State | Verified | `VERIFIED END-TO-END` |
| **Source Retrieval & Rules** | LibraryScreen | Repository | Local Knowledge Base | Matched Rules & Citations | Local Assets | Verified | `VERIFIED END-TO-END` |
| **Life Timeline & Events** | LifeTimelineScreen | JyotishaRepository | Firestore / Room | Life Events List | SQLite / Firestore | Verified | `VERIFIED END-TO-END` |
| **Prediction Ledger** | PredictionLedgerScreen | JyotishaRepository | Firestore / Room | Immutable Predictions | SQLite / Firestore | Verified | `VERIFIED END-TO-END` |
| **Outcome Evaluation** | EvaluationSummaryScreen | JyotishaRepository | Firestore / Room | Accuracy Summary | SQLite / Firestore | Verified | `VERIFIED END-TO-END` |
