package ir.ac.kntu.abusafar.model;

public class Location {

    private Long locationId;

    private String country;

    private String province;

    private String city;

    public Location(Long locationId, String country, String province, String city) {
        this.locationId = locationId;
        this.country = country;
        this.province = province;
        this.city = city;
    }

    public Long getLocationId() {
        return locationId;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }
}
