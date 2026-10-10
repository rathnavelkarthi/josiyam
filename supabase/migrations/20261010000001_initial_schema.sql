-- Jyotisha Schema Migration: 20261010000001_initial_schema.sql
-- Description: Core tables for profiles, birth profiles, and verified chart calculations with strict RLS.

-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. PROFILES TABLE (Mirrors auth.users)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    display_name TEXT NOT NULL,
    email TEXT,
    locale TEXT DEFAULT 'en' NOT NULL,
    created_at TIMESTAMPTZ DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 2. BIRTH PROFILES TABLE
CREATE TABLE IF NOT EXISTS public.birth_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    label TEXT NOT NULL,
    date_of_birth DATE NOT NULL,
    time_of_birth TIME,
    time_confidence TEXT NOT NULL CHECK (time_confidence IN ('exact', 'approximate', 'unknown')),
    place_name TEXT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    timezone_id TEXT NOT NULL,
    is_default BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- 3. CHART CALCULATIONS TABLE
CREATE TABLE IF NOT EXISTS public.chart_calculations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    birth_profile_id UUID NOT NULL REFERENCES public.birth_profiles(id) ON DELETE CASCADE,
    tradition TEXT NOT NULL CHECK (tradition IN ('vedic_sidereal', 'western_tropical', 'compare_both')),
    ayanamsha TEXT NOT NULL DEFAULT 'lahiri',
    house_system TEXT NOT NULL DEFAULT 'whole_sign',
    engine_name TEXT NOT NULL,
    engine_version TEXT NOT NULL,
    calculated_at TIMESTAMPTZ DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL,
    input_fingerprint TEXT NOT NULL,
    result_json JSONB NOT NULL,
    validation_status TEXT NOT NULL CHECK (validation_status IN ('calculated', 'verified', 'error')),
    created_at TIMESTAMPTZ DEFAULT TIMEZONE('utc'::text, NOW()) NOT NULL
);

-- INDEXES
CREATE INDEX IF NOT EXISTS idx_birth_profiles_user_id ON public.birth_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_birth_profiles_default ON public.birth_profiles(user_id, is_default) WHERE is_default = TRUE;
CREATE INDEX IF NOT EXISTS idx_chart_calculations_user_id ON public.chart_calculations(user_id);
CREATE INDEX IF NOT EXISTS idx_chart_calculations_profile_id ON public.chart_calculations(birth_profile_id);

-- ENABLE ROW LEVEL SECURITY
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.birth_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.chart_calculations ENABLE ROW LEVEL SECURITY;

-- RLS POLICIES FOR PROFILES
CREATE POLICY "Users can view own profile"
    ON public.profiles FOR SELECT
    USING (auth.uid() = id);

CREATE POLICY "Users can update own profile"
    ON public.profiles FOR UPDATE
    USING (auth.uid() = id);

CREATE POLICY "Users can insert own profile"
    ON public.profiles FOR INSERT
    WITH CHECK (auth.uid() = id);

-- RLS POLICIES FOR BIRTH PROFILES
CREATE POLICY "Users can view own birth profiles"
    ON public.birth_profiles FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own birth profiles"
    ON public.birth_profiles FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update own birth profiles"
    ON public.birth_profiles FOR UPDATE
    USING (auth.uid() = user_id);

CREATE POLICY "Users can delete own birth profiles"
    ON public.birth_profiles FOR DELETE
    USING (auth.uid() = user_id);

-- RLS POLICIES FOR CHART CALCULATIONS
CREATE POLICY "Users can view own chart calculations"
    ON public.chart_calculations FOR SELECT
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert own chart calculations"
    ON public.chart_calculations FOR INSERT
    WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can delete own chart calculations"
    ON public.chart_calculations FOR DELETE
    USING (auth.uid() = user_id);

-- UPDATED_AT TRIGGER FUNCTION
CREATE OR REPLACE FUNCTION public.handle_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = TIMEZONE('utc'::text, NOW());
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER on_profiles_updated
    BEFORE UPDATE ON public.profiles
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();

CREATE TRIGGER on_birth_profiles_updated
    BEFORE UPDATE ON public.birth_profiles
    FOR EACH ROW
    EXECUTE FUNCTION public.handle_updated_at();
