import unittest
from datetime import datetime
from app.engine.astronomy_provider import AstronomicalEphemerisProvider
from app.schemas.chart import (
    NatalChartRequest,
    TraditionEnum,
    AyanamshaEnum,
    ConfidenceEnum
)

class TestAstronomicalProvider(unittest.TestCase):
    def setUp(self):
        self.provider = AstronomicalEphemerisProvider(enabled=True)

    def test_julian_day_j2000(self):
        # 2000-01-01 12:00:00 UTC corresponds to JD 2451545.0
        utc_dt = datetime(2000, 1, 1, 12, 0, 0)
        jd = self.provider._calculate_julian_day(utc_dt)
        self.assertAlmostEqual(jd, 2451545.0, places=2)

    def test_lahiri_ayanamsha_at_j2000(self):
        # At epoch J2000.0, Lahiri ayanamsha is ~23.85°
        jd = 2451545.0
        ayanamsha = self.provider._calculate_ayanamsha(jd, AyanamshaEnum.LAHIRI)
        self.assertAlmostEqual(ayanamsha, 23.85, places=1)

    def test_natal_calculation_with_exact_time(self):
        request = NatalChartRequest(
            date_of_birth="1997-12-24",
            time_of_birth="16:30:00",
            time_confidence=ConfidenceEnum.EXACT,
            place_name="Chennai, Tamil Nadu, India",
            latitude=13.0827,
            longitude=80.2707,
            timezone_id="Asia/Kolkata",
            tradition=TraditionEnum.VEDIC_SIDEREAL,
            ayanamsha=AyanamshaEnum.LAHIRI
        )
        response = self.provider.calculate_natal_chart(request)
        
        self.assertIsNotNone(response.chart_id)
        self.assertFalse(response.is_time_uncertain)
        self.assertIsNotNone(response.ascendant_longitude)
        self.assertIsNotNone(response.ascendant_sign)
        self.assertTrue(len(response.placements) >= 9)

        # Sun in late December is in Sagittarius / Dhanu
        sun_placement = next(p for p in response.placements if p.planet_key == "Su")
        self.assertEqual(sun_placement.sign_code, "Sagittarius")
        self.assertEqual(sun_placement.sign_sanskrit, "Dhanu")

    def test_natal_calculation_unknown_birth_time_safety(self):
        # Unknown birth time MUST NOT produce falsely precise Lagnam or houses
        request = NatalChartRequest(
            date_of_birth="1997-12-24",
            time_of_birth=None,
            time_confidence=ConfidenceEnum.UNKNOWN,
            place_name="Chennai, Tamil Nadu, India",
            latitude=13.0827,
            longitude=80.2707,
            timezone_id="Asia/Kolkata",
            tradition=TraditionEnum.VEDIC_SIDEREAL,
            ayanamsha=AyanamshaEnum.LAHIRI
        )
        response = self.provider.calculate_natal_chart(request)
        
        self.assertTrue(response.is_time_uncertain)
        self.assertIsNone(response.ascendant_longitude)
        self.assertIsNone(response.ascendant_sign)
        
        # All planets must have house_number = None when Lagna is unknown
        for p in response.placements:
            self.assertIsNone(p.house_number)

    def test_tropical_vs_sidereal_difference(self):
        # Tropical longitude - Sidereal longitude must equal Ayanamsha offset
        sidereal_req = NatalChartRequest(
            date_of_birth="2020-03-21",
            time_of_birth="12:00:00",
            time_confidence=ConfidenceEnum.EXACT,
            latitude=0.0,
            longitude=0.0,
            timezone_id="UTC",
            tradition=TraditionEnum.VEDIC_SIDEREAL,
            ayanamsha=AyanamshaEnum.LAHIRI
        )
        tropical_req = NatalChartRequest(
            date_of_birth="2020-03-21",
            time_of_birth="12:00:00",
            time_confidence=ConfidenceEnum.EXACT,
            latitude=0.0,
            longitude=0.0,
            timezone_id="UTC",
            tradition=TraditionEnum.WESTERN_TROPICAL
        )
        sid_res = self.provider.calculate_natal_chart(sidereal_req)
        trop_res = self.provider.calculate_natal_chart(tropical_req)

        sun_sid = next(p for p in sid_res.placements if p.planet_key == "Su").longitude
        sun_trop = next(p for p in trop_res.placements if p.planet_key == "Su").longitude

        diff = (sun_trop - sun_sid) % 360.0
        self.assertAlmostEqual(diff, sid_res.ayanamsha_value_degrees, places=2)

if __name__ == "__main__":
    unittest.main()
