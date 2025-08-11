package ir.ac.kntu.abusafar.util.constants.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BusClass {
    VIP("VIP"),
    STANDARD("Standard"),
    SLEEPER("Sleeper");

    private final String dbValue;

    BusClass(String dbValue) {
        this.dbValue = dbValue;
    }

    public static BusClass getEnumValue(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Input text cannot be null for BusClass");
        }
        for (BusClass fc : BusClass.values()) {
            if (fc.dbValue.equals(text)) {
                return fc;
            }
        }
        throw new IllegalArgumentException("No BusClass constant corresponds to the database value: '" + text + "'");
    }

    @JsonValue
    public String getDbValue() {
        return dbValue;
    }

    @JsonCreator
    public static BusClass fromString(String text) {
        if (text == null) {
            return null;
        }
        for (BusClass bc : BusClass.values()) {
            if (bc.dbValue.equalsIgnoreCase(text) || bc.name().equalsIgnoreCase(text)) {
                return bc;
            }
        }
        throw new IllegalArgumentException("No BusClass constant corresponds to value: '" + text + "'");
    }
}