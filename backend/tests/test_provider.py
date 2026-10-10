import unittest
from app.engine.astronomy_provider import AstronomicalEphemerisProvider
from app.engine.provider import EphemerisEngineNotConfiguredException
from app.schemas.chart import NatalChartRequest

class TestProviderLicensingAndReadiness(unittest.TestCase):
    def test_provider_name_and_licensing(self):
        provider = AstronomicalEphemerisProvider(enabled=True)
        self.assertEqual(provider.name, "JyotishaAstronomicalEngine")
        self.assertEqual(provider.version, "2.1.0-commercial-clean")
        self.assertIn("Non-AGPL", provider.license_type)
        self.assertTrue(provider.is_ready())

    def test_unconfigured_engine_raises_exception(self):
        disabled_provider = AstronomicalEphemerisProvider(enabled=False)
        self.assertFalse(disabled_provider.is_ready())
        
        request = NatalChartRequest(
            date_of_birth="2000-01-01",
            latitude=13.0,
            longitude=80.0
        )
        with self.assertRaises(EphemerisEngineNotConfiguredException):
            disabled_provider.calculate_natal_chart(request)

if __name__ == "__main__":
    unittest.main()
