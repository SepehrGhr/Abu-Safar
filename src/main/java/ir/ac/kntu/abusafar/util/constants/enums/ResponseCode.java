package ir.ac.kntu.abusafar.util.constants.enums;

public enum ResponseCode {
    SUCCESS(0),
    ERROR(1),
    NOT_FOUND(404);

    private final int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
