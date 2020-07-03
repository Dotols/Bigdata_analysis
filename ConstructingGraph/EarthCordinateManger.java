package base;

public class EarthCordinateManger {

    protected static final double DEGREE_TO_RADIANS = Math.PI / 180;
  
    EarthCordinateManger() {}
    
    public Coordinate LLAtoXYZ(final LonLatAltitute _LLA) {
    	
        final double LON = _LLA.getLongitude() * DEGREE_TO_RADIANS;
        final double LAT = _LLA.getLatitude()  * DEGREE_TO_RADIANS;
        final double ALT = _LLA.getAltitute();
        final double CHI = Math.sqrt(1-WGS84.FIRST_ECCENTRICITY_SQUARED * Math.pow(Math.sin(LAT),2));

        final double X   = (WGS84.EARTH_SEMIMAJOR_AXIS/CHI + ALT) * Math.cos(LAT) * Math.cos(LON);
        final double Y   = (WGS84.EARTH_SEMIMAJOR_AXIS/CHI + ALT) * Math.cos(LAT) * Math.sin(LON);
        final double Z   = (WGS84.EARTH_SEMIMAJOR_AXIS * (1-WGS84.FIRST_ECCENTRICITY_SQUARED)/CHI + ALT) * Math.sin(LAT);

        return new Coordinate(X, Y, Z);
    }
    
    public LonLatAltitute XYZtoLLA(final Coordinate _XYZ){
    	
    	final double X = _XYZ.getX();
    	final double Y = _XYZ.getY();
    	final double Z = _XYZ.getZ();

    	double asq = Math.pow(WGS84.EARTH_SEMIMAJOR_AXIS,2);
		double bsq = Math.pow(WGS84.EARTH_SEMIMINOR_AXIS,2);

		double ep = Math.sqrt((asq-bsq)/bsq);

		double p = Math.sqrt(Math.pow(X,2) + Math.pow(Y,2));

		double th = Math.atan2(WGS84.EARTH_SEMIMAJOR_AXIS * Z, 
								WGS84.EARTH_SEMIMINOR_AXIS * p);

		double longitude = Math.atan2(Y,
										X);

		double latitude = Math.atan2((Z + Math.pow(ep,2) * WGS84.EARTH_SEMIMINOR_AXIS * Math.pow(Math.sin(th),3)), 
								(p - WGS84.FIRST_ECCENTRICITY_SQUARED * WGS84.EARTH_SEMIMAJOR_AXIS * Math.pow(Math.cos(th),3)));

		double n = WGS84.EARTH_SEMIMAJOR_AXIS/(Math.sqrt(1-WGS84.FIRST_ECCENTRICITY_SQUARED *
								Math.pow(Math.sin(latitude),2)));

		double altitude = p / Math.cos(latitude) - n;
		longitude = longitude % (2 * Math.PI);

		//Preserves the units which it was created in.
		final double LAT = latitude * 180 / Math.PI;
		final double LON = longitude * 180 / Math.PI;
		final double ALT = altitude;
		
        return new LonLatAltitute(LAT, LON, ALT);
	}
}