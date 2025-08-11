package ir.ac.kntu.abusafar.util.constants.enums;

public enum BusChairCountType {
    ONE_TWO("1-2"),
    TWO_TWO("2-2"); 

    private final String dbValue;

    BusChairCountType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static BusChairCountType getEnumValue(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Input text cannot be null for BusChairCountType");
        }
        for (BusChairCountType fc : BusChairCountType.values()) {
            if (fc.dbValue.equals(text)) {
                return fc;
            }
        }
        throw new IllegalArgumentException("No BusChairCountType constant corresponds to the database value: '" + text + "'");
    }
}