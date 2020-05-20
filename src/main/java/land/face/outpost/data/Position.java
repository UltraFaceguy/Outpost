package land.face.outpost.data;

import org.bukkit.Location;

public class Position {

  private final int x;
  private final int y;
  private final int z;

  public Position(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }

  public static boolean isWithin(Outpost outpost, Location location, Position pos1, Position pos2) {
    if (!outpost.getWorld().equals(location.getWorld().getName())) {
      return false;
    }
    if (pos1 == null || pos2 == null) {
      return false;
    }
    return isBetweenValues(location.getX(), pos1.getX(), pos2.getX())
        && isBetweenValues(location.getY(), pos1.getY(), pos2.getY())
        && isBetweenValues(location.getZ(), pos1.getZ(), pos2.getZ());
  }

  private static boolean isBetweenValues(double amount, double boundA, double boundB) {
    if (boundA >= boundB) {
      return amount <= boundA && amount >= boundB;
    } else {
      return amount <= boundB && amount >= boundA;
    }
  }

}
