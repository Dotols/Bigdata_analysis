package base;

import java.util.Objects;

//DTO LonLatAltitue
public class LonLatAltitute {

  private final double LONGITUDE;
  private final double LATITUDE;
  private final double ALTITUTE;

  public LonLatAltitute(double _LONGITUDE, double _LATITUDE, double _ALTITUTE) {
      this.LONGITUDE = _LONGITUDE;
      this.LATITUDE  = _LATITUDE;
      this.ALTITUTE  = _ALTITUTE;
  }

  public double getLongitude() {
      return LONGITUDE;
  }

  public double getLatitude() {
      return LATITUDE;
  }

  public double getAltitute() {
      return ALTITUTE;
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      LonLatAltitute that = (LonLatAltitute) o;
      return Double.compare(that.LONGITUDE, LONGITUDE) == 0 &&
             Double.compare(that.LATITUDE, LATITUDE)   == 0 &&
             Double.compare(that.ALTITUTE, ALTITUTE)   == 0;
  }

  @Override
  public int hashCode() {
      return Objects.hash(LONGITUDE, LATITUDE, ALTITUTE);
  }
}
