package base;

public class WGS84{
	public static final double EARTH_SEMIMAJOR_AXIS        = 6378137.0;
	public static final double EARTH_RECIPROCAL_FLATTENING = 1.0 / 298.257223563;
	public static final double EARTH_SEMIMINOR_AXIS        = EARTH_SEMIMAJOR_AXIS * (1.0 - EARTH_RECIPROCAL_FLATTENING);
	public static final double FIRST_ECCENTRICITY_SQUARED  = 2 * EARTH_RECIPROCAL_FLATTENING - Math.pow(EARTH_RECIPROCAL_FLATTENING, 2);
	public static final double SECOND_ECCENTRICITY_SQUARED = EARTH_RECIPROCAL_FLATTENING * (2 - EARTH_RECIPROCAL_FLATTENING) /
															(Math.pow(1.0-EARTH_RECIPROCAL_FLATTENING,2));
}