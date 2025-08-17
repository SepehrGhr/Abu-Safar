package ir.ac.kntu.abusafar.util.constants.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TrainRoomType {
    FOUR_BED("4-BED"),
    SIX_BED("6-BED");

    private final String dbValue;

    TrainRoomType(String dbValue) {
        this.dbValue = dbValue;
    }

    @JsonValue
    public String getDbValue() {
        return dbValue;
    }

    @JsonCreator
    public static TrainRoomType fromString(String text) {
        if (text == null) {
            return null;
        }
        for (TrainRoomType trt : TrainRoomType.values()) {
            if (trt.dbValue.equalsIgnoreCase(text) || trt.name().equalsIgnoreCase(text)) {
                return trt;
            }
        }
        throw new IllegalArgumentException("No TrainRoomType constant corresponds to value: '" + text + "'");
    }
}