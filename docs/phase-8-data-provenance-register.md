# Phase 8: Data Provenance Register

| Screen / UI Component | Displayed Field | Data Origin | Source File & Function | Active Profile Scoped | Safety Status |
|---|---|---|---|---|---|
| Home Dashboard | User Name & Greeting | Active Profile / Firestore | `HomeScreen.kt` / `JyotishaStrings.getGreeting` | Yes | Production Safe |
| Home Dashboard | Rasi (D1) Natal Chart | Ephemeris Engine | `AstronomicalCalculator.calculateNatalChart` | Yes | Production Safe |
| Chart Explorer | Planetary Degrees & Houses | Astronomical Calculator | `AstronomicalCalculator.kt` | Yes | Production Safe |
| Chart Explorer | Dasa Timeline | Vimshottari Dasa Engine | `AstronomicalCalculator.calculateVimshottariDasaTimeline` | Yes | Production Safe |
| Profile & Settings | Saved Birth Profiles | Room Cache & Firestore | `BirthProfileRepository.kt` / `JyotishaDao` | Yes | Production Safe |
| Life Timeline | Life Events List | User-entered State | `LifeEventsAndPredictionScreens.kt` | Yes | Production Safe |
| Retrospective Analysis | Historical Ephemeris Context | Astronomical Ephemeris | `AstronomicalCalculator.computeHistoricalContext` | Yes | Production Safe |
| Retrospective Analysis | Scripture Citations | Parashara Canon | `JyotishaRepository.kt` | Yes | Production Safe |
| Siddha AI | Interactive Chat | Rule/Text Assistant | `JyotishaRepository.sendChatMessage` | Yes | Production Safe |
| Panchangam | Tithi / Nakshatra | Solar-Lunar Engine | `PanchangamSnapshot` | Yes | Production Safe |
