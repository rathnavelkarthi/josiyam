import os
from typing import Optional

class Settings:
    PROJECT_NAME: str = "Jyotisha Astronomical Calculation Service"
    VERSION: str = "1.0.0"
    API_V1_STR: str = "/v1"
    
    # Environment & Security
    ENVIRONMENT: str = os.getenv("ENVIRONMENT", "development")
    SUPABASE_URL: Optional[str] = os.getenv("SUPABASE_URL")
    SUPABASE_JWT_SECRET: Optional[str] = os.getenv("SUPABASE_JWT_SECRET")
    
    # Calculation Engine Configuration
    CALCULATION_ENGINE_ENABLED: bool = os.getenv("CALCULATION_ENGINE_ENABLED", "true").lower() in ("true", "1", "yes")
    CALCULATION_ENGINE_NAME: str = "JyotishaMeeusEphemeris"
    CALCULATION_ENGINE_VERSION: str = "2.1.0"
    
    # CORS
    BACKEND_CORS_ORIGINS: list[str] = ["*"]

settings = Settings()
