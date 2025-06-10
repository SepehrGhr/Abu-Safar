package ir.ac.kntu.abusafar.util.constants.enums;

public enum ServiceType {
    INTERNET("Internet"),
    FOOD_SERVICE("Food service"),
    BED("Bed");

    private final String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ServiceType getEnumName(String text) {
        for (ServiceType b : ServiceType.values()) {
            if (b.displayName.equalsIgnoreCase(text)) {
                return b;
            }
        }
        if ("Internet".equalsIgnoreCase(text)) return INTERNET;
        if ("Food service".equalsIgnoreCase(text)) return FOOD_SERVICE;
        if ("Bed".equalsIgnoreCase(text)) return BED;

        throw new IllegalArgumentException("No constant with text " + text + " found");
    }

}
