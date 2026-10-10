from enum import Enum
from typing import List, Optional

try:
    from pydantic import BaseModel, Field
except ImportError:
    from dataclasses import dataclass, field, asdict
    class BaseModel:
        def __init__(self, **kwargs):
            for k, v in kwargs.items():
                setattr(self, k, v)
        def dict(self):
            res = {}
            for k, v in self.__dict__.items():
                if isinstance(v, list):
                    res[k] = [item.dict() if hasattr(item, "dict") else item for item in v]
                elif hasattr(v, "dict"):
                    res[k] = v.dict()
                elif hasattr(v, "value"):
                    res[k] = v.value
                else:
                    res[k] = v
            return res
    def Field(default=None, **kwargs):
        return default

class TraditionEnum(str, Enum):
    VEDIC_SIDEREAL = "vedic_sidereal"
    WESTERN_TROPICAL = "western_tropical"
    COMPARE_BOTH = "compare_both"

class AyanamshaEnum(str, Enum):
    LAHIRI = "lahiri"
    RAMAN = "raman"
    KRISHNAMURTI = "krishnamurti"

class ConfidenceEnum(str, Enum):
    EXACT = "exact"
    APPROXIMATE = "approximate"
    UNKNOWN = "unknown"

class NatalChartRequest(BaseModel):
    birth_profile_id: Optional[str] = Field(None, description="Optional ID of saved birth profile")
    date_of_birth: str = Field(..., description="Date of birth in YYYY-MM-DD format", example="1997-12-24")
    time_of_birth: Optional[str] = Field(None, description="Time of birth in HH:MM or HH:MM:SS format", example="16:30:00")
    time_confidence: ConfidenceEnum = Field(ConfidenceEnum.EXACT, description="Confidence level in birth time")
    place_name: str = Field("Chennai, Tamil Nadu, India", description="Birthplace label")
    latitude: float = Field(..., ge=-90.0, le=90.0, description="Latitude in decimal degrees", example=13.0827)
    longitude: float = Field(..., ge=-180.0, le=180.0, description="Longitude in decimal degrees", example=80.2707)
    timezone_id: str = Field("Asia/Kolkata", description="IANA Timezone Identifier", example="Asia/Kolkata")
    tradition: TraditionEnum = Field(TraditionEnum.VEDIC_SIDEREAL, description="Astrology tradition")
    ayanamsha: AyanamshaEnum = Field(AyanamshaEnum.LAHIRI, description="Ayanamsha setting for sidereal calculations")

class PlanetPlacementSchema(BaseModel):
    planet_key: str = Field(..., example="Su")
    planet_name: str = Field(..., example="Sun")
    sanskrit_name: str = Field(..., example="Surya")
    longitude: float = Field(..., description="Ecliptic longitude in degrees [0, 360)")
    degrees_formatted: str = Field(..., example="08°45'")
    sign_code: str = Field(..., example="Sagittarius")
    sign_sanskrit: str = Field(..., example="Dhanu")
    sign_index: int = Field(..., ge=0, le=11, description="Zodiac sign index (0=Aries, 11=Pisces)")
    house_number: Optional[int] = Field(None, ge=1, le=12, description="1-12 house index relative to Lagna (null if time unknown)")
    nakshatra_name: str = Field(..., example="Moola")
    nakshatra_pada: int = Field(..., ge=1, le=4, example=2)
    is_retrograde: bool = Field(False)
    dignity: str = Field("Neutral", example="Friendly")

class NatalChartResponse(BaseModel):
    chart_id: str
    calculated_at: str
    engine_name: str
    engine_version: str
    tradition: TraditionEnum
    ayanamsha_name: str
    ayanamsha_value_degrees: float
    is_time_uncertain: bool
    ascendant_longitude: Optional[float] = None
    ascendant_degrees_formatted: Optional[str] = None
    ascendant_sign: Optional[str] = None
    moon_sign: str
    sun_sign: str
    birth_nakshatra: str
    birth_pada: int
    placements: List[PlanetPlacementSchema]
    input_fingerprint: str
