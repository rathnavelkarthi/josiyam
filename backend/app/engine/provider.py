from abc import ABC, abstractmethod
from typing import Optional
from app.schemas.chart import NatalChartRequest, NatalChartResponse

class EphemerisEngineNotConfiguredException(Exception):
    """Raised when calculation engine is unconfigured or unavailable."""
    pass

class EphemerisProvider(ABC):
    """
    Abstract interface for astronomical and ephemeris calculation engines.
    Permits hot-swapping providers (e.g. Jean Meeus Algorithms, Skyfield, Swiss Ephemeris)
    while adhering strictly to licensing terms and numerical contracts.
    """

    @property
    @abstractmethod
    def name(self) -> str:
        """Returns the canonical provider name."""
        pass

    @property
    @abstractmethod
    def version(self) -> str:
        """Returns provider version."""
        pass

    @property
    @abstractmethod
    def license_type(self) -> str:
        """Explicit license under which this provider operates."""
        pass

    @abstractmethod
    def is_ready(self) -> bool:
        """Checks if ephemeris data/engine files are ready."""
        pass

    @abstractmethod
    def calculate_natal_chart(self, request: NatalChartRequest) -> NatalChartResponse:
        """Calculates authentic planetary longitudes and houses from validated inputs."""
        pass
