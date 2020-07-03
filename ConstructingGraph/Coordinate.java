package base;

import java.util.Objects;

//DTO Coordinate
public class Coordinate {

  private final double X;
  private final double Y;
  private final double Z;

  public Coordinate(double _X, double _Y, double _Z) {
      this.X = _X;
      this.Y = _Y;
      this.Z = _Z;
  }

  public double getX() {
      return X;
  }

  public double getY() {
      return Y;
  }

  public double getZ() {
      return Z;
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Coordinate that = (Coordinate) o;
      return Double.compare(that.X, X) == 0 &&
             Double.compare(that.Y, Y) == 0 &&
             Double.compare(that.Z, Z) == 0;
  }

  @Override
  public int hashCode() {
      return Objects.hash(X, Y, Z);
  }

}