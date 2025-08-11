package ir.ac.kntu.abusafar.util.constants.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlightClass {
    ECONOMY_CLASS("Economy class"),
    BUSINESS_CLASS("Business class"),
    FIRST_CLASS("First class");

    private final String dbValue;

    FlightClass(String dbValue) {
        this.dbValue = dbValue;
    }

    public static FlightClass getEnumValue(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Input text cannot be null for FlightClass");
        }
        for (FlightClass fc : FlightClass.values()) {
            if (fc.dbValue.equals(text)) {
                return fc;
            }
        }
        throw new IllegalArgumentException("No FlightClass constant corresponds to the database value: '" + text + "'");
    }

    @JsonValue
    public String getDbValue() {
        return dbValue;
    }

    @JsonCreator
    public static FlightClass fromString(String text) {
        if (text == null) {
            return null;
        }
        for (FlightClass fc : FlightClass.values()) {
            if (fc.dbValue.equalsIgnoreCase(text) || fc.name().equalsIgnoreCase(text)) {
                return fc;
            }
        }
        throw new IllegalArgumentException("No FlightClass constant corresponds to value: '" + text + "'");
    }
}