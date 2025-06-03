package ir.ac.kntu.abusafar.util.constants.enums;

public enum BusClass {
    VIP("VIP"),
    STANDARD("Standard"),
    SLEEPER("Sleeper");

    private final String dbValue;

    BusClass(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
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
}