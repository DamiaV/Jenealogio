package net.darmo_creations.util;

public final class Version implements Comparable<Version> {
  /** Current version */
  public static final Version CURRENT_VERSION;

  /** Version 1.3 */
  public static final Version V1_3 = new Version(1, 3, 0, false);

  static {
    CURRENT_VERSION = new Version(1, 3, 0, true);
  }

  private final int major, minor, patch;
  private final boolean indev;

  public Version(int major, int minor, int patch, boolean indev) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.indev = indev;
  }

  public Version(int version) {
    this((version >> 16) & 0xff, (version >> 8) & 0xff, version & 0xff, ((version >> 31) & 1) == 1);
  }

  public int getMajor() {
    return this.major;
  }

  public int getMinor() {
    return this.minor;
  }

  public int getPatch() {
    return this.patch;
  }

  public boolean isIndev() {
    return this.indev;
  }

  /**
   * @return the integer value for this version, including indev bit
   */
  public int getFullValue() {
    return getValue() | (this.indev ? 1 : 0) << 31;
  }

  /**
   * @return the integer value for this version, without indev bit
   */
  public int getValue() {
    return (this.major & 0xff) << 16 | (this.minor & 0xff) << 8 | (this.patch & 0xff);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Version)
      return ((Version) o).compareTo(this) == 0;
    return false;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.indev ? 1231 : 1237);
    result = prime * result + this.major;
    result = prime * result + this.minor;
    result = prime * result + this.patch;
    return result;
  }

  @Override
  public int compareTo(Version o) {
    if (Integer.compare(getValue(), o.getValue()) > 0 || o.isIndev() && !isIndev() && Integer.compare(getValue(), o.getValue()) == 0) {
      return 1;
    }
    else if (Integer.compare(getValue(), o.getValue()) < 0 || !o.isIndev() && isIndev() && Integer.compare(getValue(), o.getValue()) == 0) {
      return -1;
    }
    return 0;
  }

  /**
   * Formats this version as {@code major.minor[.patch][d]} (patch is added only if it is not 0).
   * The "d" indicates if the version is in development.
   * 
   * @return the formatted version
   */
  @Override
  public String toString() {
    String dev = this.indev ? "d" : "";

    if (this.patch != 0)
      return String.format("%d.%d.%d%s", this.major, this.minor, this.patch, dev);
    return String.format("%d.%d%s", this.major, this.minor, dev);
  }
}