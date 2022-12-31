package londonSafeTravel.schema;

public class Location {
    private double longitude;
    private double latitude;

    // create and initialize a point with given name and
    // (latitude, longitude) specified in degrees
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double metricNorm(Location l2) {
        double earthRadiusKm = 6371.0 * 1000;

        double lat1Rad = Math.toRadians(latitude);
        double lon1Rad = Math.toRadians(longitude);
        double lat2Rad = Math.toRadians(l2.latitude);
        double lon2Rad = Math.toRadians(l2.longitude);

        return earthRadiusKm * Math.acos(Math.sin(lat1Rad) * Math.sin(lat2Rad) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.cos(lon1Rad - lon2Rad));
    }

    // return distance between this location and that location
    // measured in statute miles
    public double distanceTo(Location that) {
        double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
        double lat1 = Math.toRadians(this.latitude);
        double lon1 = Math.toRadians(this.longitude);
        double lat2 = Math.toRadians(that.latitude);
        double lon2 = Math.toRadians(that.longitude);

        // great circle distance in radians, using law of cosines formula
        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        // each degree on a great circle of Earth is 60 nautical miles
        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

    // return string representation of this point
    public String toString() {
        return " (" + latitude + ", " + longitude + ")";
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // test client
    public static void main(String[] args) {
        Location loc1 = new Location(40.366633, 74.640832);
        Location loc2 = new Location(42.443087, 76.488707);
        double distance = loc1.distanceTo(loc2);
        System.out.printf("%6.3f miles from\n", distance);
        System.out.println("test" + loc1.metricNorm(loc2));
        System.out.println(loc1 + " to " + loc2);
    }
}