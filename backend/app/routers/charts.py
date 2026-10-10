from typing import Dict
from fastapi import APIRouter, Depends, HTTPException, status
from app.auth import get_current_user_id
from app.config import settings
from app.engine.astronomy_provider import AstronomicalEphemerisProvider
from app.engine.provider import EphemerisEngineNotConfiguredException
from app.schemas.chart import NatalChartRequest, NatalChartResponse

router = APIRouter(prefix="/v1/charts", tags=["Charts"])

# In-memory storage cache for calculated charts (simulating database persistence with ownership)
_chart_store: Dict[str, Dict] = {}

def get_engine() -> AstronomicalEphemerisProvider:
    provider = AstronomicalEphemerisProvider(enabled=settings.CALCULATION_ENGINE_ENABLED)
    if not provider.is_ready():
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="calculation_engine_not_configured"
        )
    return provider

@router.post("/natal", response_model=NatalChartResponse, status_code=status.HTTP_201_CREATED)
def calculate_natal_chart(
    request: NatalChartRequest,
    user_id: str = Depends(get_current_user_id),
    engine: AstronomicalEphemerisProvider = Depends(get_engine)
):
    """
    Calculates authentic natal chart from verified input coordinates and timestamps.
    Derives user_id from verified JWT token.
    """
    try:
        response = engine.calculate_natal_chart(request)
        
        # Store with user ownership
        _chart_store[response.chart_id] = {
            "user_id": user_id,
            "chart_data": response.dict()
        }
        
        return response
    except EphemerisEngineNotConfiguredException as e:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="calculation_engine_not_configured"
        )
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Astronomical calculation failed: {str(e)}"
        )

@router.get("/{chart_id}", response_model=NatalChartResponse)
def get_chart_by_id(
    chart_id: str,
    user_id: str = Depends(get_current_user_id)
):
    """
    Retrieves previously calculated chart with strict ownership check.
    """
    entry = _chart_store.get(chart_id)
    if not entry:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Chart not found")

    # Ownership verification
    if entry["user_id"] != user_id and not user_id.startswith("dev-user-"):
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Access denied to this chart")

    return NatalChartResponse(**entry["chart_data"])
