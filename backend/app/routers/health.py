from fastapi import APIRouter, HTTPException, status
from app.config import settings

router = APIRouter(prefix="/health", tags=["Health"])

@router.get("/live")
def liveness_check():
    """Kubernetes / Cloud Run liveness probe."""
    return {"status": "alive", "service": settings.PROJECT_NAME}

@router.get("/ready")
def readiness_check():
    """Readiness probe verifying engine configuration."""
    if not settings.CALCULATION_ENGINE_ENABLED:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="calculation_engine_not_configured"
        )
    return {
        "status": "ready",
        "engine": settings.CALCULATION_ENGINE_NAME,
        "version": settings.CALCULATION_ENGINE_VERSION,
        "configured": True
    }
