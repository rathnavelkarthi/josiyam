# Phase 8: Root Cause & Resolution Report

## Executive Summary
This document records the systematic investigation, root causes, and resolutions for data integrity, session persistence, profile isolation, deletion safety, life timeline historical ephemeris calculation, date/time pickers, and complete localization across English, Tamil, Hindi, and Telugu.

## Identified Issues & Technical Root Causes

1. **Hardcoded User Data Contamination**
   - **Symptom**: User name 'Rathnavel', sample dates, and fixed credentials appeared across screens.
   - **Root Cause**: `AuthScreen.kt`, `BirthProfileStepperScreen.kt`, and `JyotishaRepository.kt` initialized mutable state with string literals ('Rathnavel', '24 Dec 1997', 'rathnavelkarthi1@gmail.com').
   - **Resolution**: Cleared default state in input composables to rely solely on user input or active profile data. Updated `JyotishaRepository` fallback email and greeting composables.

2. **Repeated Onboarding & Session Restoration Failure**
   - **Symptom**: Authenticated users were routed through onboarding repeatedly.
   - **Root Cause**: `MainActivity.kt` relied on transient in-memory screen state that reset during recomposition or process recreation, and `JyotishaRepository` did not query persistent Room cache or Firestore for existing user profiles on launch.
   - **Resolution**: Implemented `BirthProfileRepository` for Firestore profile management and `AuthRepository` for persistent session management. Updated `JyotishaRepository.initialize()` to restore cached profiles and set `hasCompletedOnboarding = true`.

3. **Incomplete Profile Deletion & Stale Cache**
   - **Symptom**: Deleting a profile in Settings did not remove associated readings or Firestore subcollections, leaving orphan records.
   - **Root Cause**: Profile deletion was implemented as a local array filter without calling Room DAO or Firestore delete endpoints.
   - **Resolution**: Added `BirthProfileRepository.deleteProfile()` and `JyotishaDao.deleteProfileById()` / `deleteChartsByProfileId()`. Implemented confirmation dialogs and active profile fallback selection upon deletion.

4. **Unclear 'Add Life Event' & Generic Retrospective Output**
   - **Symptom**: The timeline 'Add' button lacked context, and retrospective analysis returned fixed template strings.
   - **Root Cause**: `runRetrospectiveAnalysis()` returned a hardcoded `RetrospectiveAnalysis` object without calling ephemeris calculations for the event date.
   - **Resolution**: Added `AstronomicalCalculator.computeHistoricalContext()` to compute Julian Day, Ephemeris centuries T, Lahiri Chitrapaksha ayanamsha, and Jupiter/Saturn transits for the specific event date. Added a prominent `Add Life Event` button with DatePicker integration.

5. **Text-Field Date Inputs**
   - **Symptom**: Date/time input fields behaved like plain text fields without picker affordances.
   - **Root Cause**: Form fields lacked `DatePickerDialog` and `TimePickerDialog` launchers attached to trailing icons.
   - **Resolution**: Integrated native `DatePickerDialog` and `TimePickerDialog` in `BirthProfileStepperScreen` and `LifeEventsAndPredictionScreens`.

6. **Incomplete Localization Coverage**
   - **Symptom**: Strings were partially hardcoded in English across screens.
   - **Root Cause**: Missing resource keys in `values-ta`, `values-hi`, and `values-te`.
   - **Resolution**: Expanded `strings.xml` in `values`, `values-ta`, `values-hi`, and `values-te` with comprehensive UI keys and wired `JyotishaStrings` throughout composables.
