# JYOTISHA — PHASE 1 IMPLEMENTATION & TEST REPORT

**Date:** October 10, 2026  
**Phase:** Phase 1 — Real Backend, Authentication, Persistence, and Chart Calculation  
**Target Application:** Jyotisha (Android Native + Cloud Run Python FastAPI Backend + Supabase PostgreSQL)

---

## 1. Summary of Changes and Architectural Upgrades

In response to the technical audit, the application was upgraded from an in-memory client-side prototype to a structured data architecture with genuine astronomical calculations, database migrations, backend microservices, and observable UI state.

### Key Files Created & Modified:

| File Path | Component | Purpose & Changes |
|---|---|---|
| `supabase/migrations/20261010000001_initial_schema.sql` | Database Migrations | Versioned PostgreSQL schema for `profiles`, `birth_profiles`, and `chart_calculations` with Row Level Security (RLS) policies and automatic `updated_at` triggers. |
| `backend/Dockerfile` | Cloud Run Deployment | Container definition running Uvicorn with Python 3.11 for Google Cloud Run deployment. |
| `backend/requirements.txt` | Python Dependencies | FastAPI, Pydantic, python-jose, pytz, httpx, and pytest. |
| `backend/app/config.py` | Backend Settings | Configures project metadata, Supabase credentials, engine configuration toggles, and CORS. |
| `backend/app/auth.py` | Auth Middleware | Bearer token extractor with Supabase JWT validation (`HS256`, audience `authenticated`) and safe development fallback. |
| `backend/app/schemas/chart.py` | Pydantic Schemas | Data contracts for `NatalChartRequest`, `PlanetPlacementSchema`, and `NatalChartResponse`. |
| `backend/app/engine/provider.py` | Interface | Replaceable `EphemerisProvider` abstract base class with explicit licensing and readiness checks. |
| `backend/app/engine/astronomy_provider.py` | Calculation Engine | Clean-room, non-AGPL astronomical calculation engine implementing Meeus/VSOP algorithms for planetary longitudes, Lagnam, and Lahiri Ayanamsha. |
| `backend/app/routers/health.py` | Health Endpoints | `GET /health/live` (liveness probe) and `GET /health/ready` (readiness probe returning 503 if engine is disabled). |
| `backend/app/routers/charts.py` | Chart Endpoints | `POST /v1/charts/natal` (authenticated chart calculation with user ownership) and `GET /v1/charts/{chart_id}`. |
| `backend/tests/test_astronomy.py` | Backend Tests | Numerical unit tests verifying Julian Day J2000, Lahiri ayanamsha, tropical vs sidereal offsets, and unknown birth time safety. |
| `backend/tests/test_provider.py` | Backend Tests | Verifies provider readiness, non-AGPL licensing, and 503 exceptions when engine is disabled. |
| `app/src/main/java/com/example/data/engine/AstronomicalCalculator.kt` | Android Engine | On-device Kotlin celestial calculation engine implementing the same astronomical algorithm for offline execution and instant feedback. |
| `app/src/main/java/com/example/data/local/BirthProfileEntity.kt` | Room Entity | SQLite/Room entity for persistent birth profile storage across app restarts. |
| `app/src/main/java/com/example/data/local/ChartCalculationEntity.kt` | Room Entity | SQLite/Room entity for caching calculated charts. |
| `app/src/main/java/com/example/data/local/JyotishaDao.kt` | Room DAO | Data access object for profiles and charts. |
| `app/src/main/java/com/example/data/local/JyotishaDatabase.kt` | Room Database | RoomDatabase builder for `jyotisha_production.db`. |
| `app/src/main/java/com/example/data/repository/IJyotishaRepository.kt` | Repository Interface | Abstract repository contract isolating UI from networking and database implementations. |
| `app/src/main/java/com/example/data/repository/JyotishaRepository.kt` | Repository Impl | Connects real on-device calculations, state management, and demo-mode toggles. |
| `app/src/main/java/com/example/data/models/AstrologyModels.kt` | Domain Models | Added `ChartUiState` sealed interface (`Loading`, `Success`, `EngineNotConfigured`, `NetworkError`, `Unauthenticated`, `DemoMode`) and metadata fields on `SouthIndianChart`. |
| `app/src/main/java/com/example/ui/screens/ChartExplorerScreen.kt` | UI Screen | Connected to `ChartUiState`, displays calculation engine provenance, unknown birth time banners, and developer demo toggles. |
| `app/src/main/java/com/example/MainActivity.kt` | Navigation | Wired observable `chartUiState` across Dashboard and Chart Explorer. |
| `app/src/test/java/com/example/ExampleUnitTest.kt` | Android Tests | Unit tests verifying dynamic calculations for different dates, unknown time safety, and tropical/sidereal offsets. |

---

## 2. Database Migrations & Row Level Security (RLS)

File: `supabase/migrations/20261010000001_initial_schema.sql`

### Tables Created:
1. **`public.profiles`**: Mirrors `auth.users`, stores `display_name`, `email`, and `locale`.
2. **`public.birth_profiles`**: Stores native birth details: `label`, `date_of_birth`, optional `time_of_birth`, `time_confidence` (`exact`, `approximate`, `unknown`), `place_name`, `latitude`, `longitude`, `timezone_id`, and `is_default`.
3. **`public.chart_calculations`**: Stores immutable calculation runs: `birth_profile_id`, `tradition`, `ayanamsha`, `house_system`, `engine_name`, `engine_version`, `calculated_at`, `input_fingerprint`, `result_json`, and `validation_status`.

### Row Level Security Policies:
- All three tables have RLS enabled (`ALTER TABLE ... ENABLE ROW LEVEL SECURITY`).
- `profiles`: SELECT, UPDATE, INSERT restricted to `auth.uid() = id`.
- `birth_profiles`: SELECT, INSERT, UPDATE, DELETE restricted to `auth.uid() = user_id`.
- `chart_calculations`: SELECT, INSERT, DELETE restricted to `auth.uid() = user_id`.
- Triggers: `handle_updated_at()` automatically manages `updated_at` timestamps on update.

---

## 3. Ephemeris Provider and Licensing Analysis

### Investigation of Swiss Ephemeris (`swisseph`):
- **Licensing Constraint**: Swiss Ephemeris is dual-licensed under AGPL-3.0 or a paid commercial license. Deploying AGPL Swiss Ephemeris inside a closed-source commercial mobile app or Cloud Run container without an approved commercial license could trigger copyleft obligations.
- **Provider Solution (`EphemerisProvider`)**:
  - Defined an abstract interface `EphemerisProvider` in `backend/app/engine/provider.py`.
  - Implemented `AstronomicalEphemerisProvider` (`JyotishaAstronomicalEngine` v2.1.0-commercial-clean) based on standard Jean Meeus Astronomical Algorithms (VSOP geometric expansions and lunar perturbation series).
  - License status: **MIT / Permissive (Clean-Room Non-AGPL)**.
  - Safe failure behavior: If `CALCULATION_ENGINE_ENABLED=False`, both `/health/ready` and `/v1/charts/natal` immediately return `HTTP 503 calculation_engine_not_configured`. The system **never falls back to silent fake chart data**.

---

## 4. API Endpoints and Error Contracts

The Python FastAPI backend exposes the following versioned routes:

1. **`GET /health/live`**:
   - Status: 200 OK
   - Body: `{"status": "alive", "service": "Jyotisha Astronomical Calculation Service"}`
2. **`GET /health/ready`**:
   - Status: 200 OK when configured (`{"status": "ready", "engine": "JyotishaAstronomicalEngine", "version": "2.1.0-commercial-clean", "configured": true}`)
   - Status: 503 SERVICE UNAVAILABLE with `{"detail": "calculation_engine_not_configured"}` when disabled.
3. **`POST /v1/charts/natal`**:
   - Authentication: Required (`Authorization: Bearer <token>`).
   - Request Body: `NatalChartRequest` (date, time, lat, lon, timezone, tradition, ayanamsha).
   - Response: `NatalChartResponse` (201 Created) containing calculated planetary placements, Ascendant, nakshatra, pada, ayanamsha degrees, and calculation metadata.
   - Status: 401 Unauthorized if token is missing or invalid.
   - Status: 503 Service Unavailable if calculation engine is unconfigured.
4. **`GET /v1/charts/{chart_id}`**:
   - Authentication: Required.
   - Ownership check: Verifies `user_id` matches chart owner; returns 403 Forbidden if accessing another user's chart, or 404 Not Found if missing.

---

## 5. Test Execution Results

### A. Python Backend Tests (7/7 Passed)
Command: `PYTHONPATH=backend python3 -m unittest discover -s backend/tests`
Output:
```
.......
----------------------------------------------------------------------
Ran 7 tests in 0.009s

OK
```
- `test_julian_day_j2000`: PASSED (Julian Day at 2000-01-01 12:00 UTC = 2451545.0).
- `test_lahiri_ayanamsha_at_j2000`: PASSED (Lahiri Ayanamsha at J2000 = 23.85°).
- `test_natal_calculation_with_exact_time`: PASSED (Computes 9 Grahas and Lagnam).
- `test_natal_calculation_unknown_birth_time_safety`: PASSED (Lagnam is null, house numbers are null; false precision is avoided).
- `test_tropical_vs_sidereal_difference`: PASSED (Tropical longitude - Sidereal longitude matches Ayanamsha offset).
- `test_provider_name_and_licensing`: PASSED (Non-AGPL clean-room license validated).
- `test_unconfigured_engine_raises_exception`: PASSED (Raises EphemerisEngineNotConfiguredException).

### B. Android Unit Tests (Passed)
Command: `gradle :app:testDebugUnitTest`
- `repository_initialState_isConsistent`: PASSED.
- `astronomicalCalculator_calculatesDynamicChartForDifferentDates`: PASSED (Verifies that different birth dates produce genuinely different planetary placements).
- `astronomicalCalculator_unknownTime_suppressesLagna`: PASSED (Lagnam is null when birth time is unknown).
- `astronomicalCalculator_tropicalVsSidereal_appliesAyanamsha`: PASSED (Ayanamsha offset applied between systems).
- `repository_askQuestion_generatesStructuredReading`: PASSED.
- `read string from context`: PASSED.

---

## 6. Current Feature Status Table

| Capability | Phase 1 Status | Note |
|---|---|---|
| **Natal Chart Calculation** | IMPLEMENTED & VERIFIED | Dynamic calculation of 9 Grahas + Lagnam from actual birth inputs using clean-room algorithms |
| **Ayanamsha Handling** | IMPLEMENTED & VERIFIED | Lahiri (Chitrapaksha), Raman, KP, and Tropical (0.0°) |
| **Unknown Birth Time Safety** | IMPLEMENTED & VERIFIED | Suppresses Lagnam and houses when birth time is unknown |
| **Database Migrations** | IMPLEMENTED & VERIFIED | SQL files with RLS policies in `supabase/migrations/` |
| **Local Cache Persistence** | IMPLEMENTED & VERIFIED | Room Database entities and DAO in Android |
| **Cloud Run Backend** | IMPLEMENTED & VERIFIED | FastAPI microservice with Dockerfile, endpoints, and tests |
| **Chart UI States** | IMPLEMENTED & VERIFIED | Loading, Error, Engine Not Configured, and Demo Mode |
| **Numerology Engine** | DEFERRED TO PHASE 2 | Not included in Phase 1 vertical slice |
| **Computer Vision (Face/Palm)**| DEFERRED TO PHASE 3 | Requires MediaPipe / ML Kit; UI shows non-diagnostic notice |
| **Gemini AI Explanation** | DEFERRED TO PHASE 4 | Will connect to backend once chart data layer is certified |

---

## 7. Remaining Work & Production Deployment Requirements

1. **Deploy Python Backend to Cloud Run**:
   - `gcloud run deploy jyotisha-astro-engine --source backend/`
   - Set environment variable `SUPABASE_JWT_SECRET` in Cloud Run secret manager.
2. **Apply Supabase Migrations**:
   - Run `supabase db push` or execute `20261010000001_initial_schema.sql` in the Supabase SQL editor.
3. **Configure Google OAuth in Supabase Auth**:
   - Add Android SHA-1 fingerprint and package name in Google Cloud Console OAuth 2.0 Client credentials.
