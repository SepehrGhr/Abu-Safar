package ir.ac.kntu.abusafar.util.constants.enums;

public enum TrainRoomType {
    FOUR_BED("4-BED"),
    SIX_BED("6-BED");

    private final String dbValue;

    TrainRoomType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static TrainRoomType getEnumValue(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Input text cannot be null for TrainRoomType");
        }
        for (TrainRoomType fc : TrainRoomType.values()) {
            if (fc.dbValue.equals(text)) {
                return fc;
            }
        }
        throw new IllegalArgumentException("No TrainRoomType constant corresponds to the database value: '" + text + "'");
    }
}
