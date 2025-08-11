package ir.ac.kntu.abusafar.dto.response;

import ir.ac.kntu.abusafar.util.constants.enums.ResponseCode;
import ir.ac.kntu.abusafar.util.constants.enums.ResponseMessage;

public class BaseResponse<T> {

    private String message;
    private Integer code;
    private Long time;
    private boolean successful;
    private T data;

    public BaseResponse() {}

    public BaseResponse(String message, Integer code, Long time, boolean successful, T data) {
        this.message = message;
        this.code = code;
        this.time = time;
        this.successful = successful;
        this.data = data;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(
                ResponseMessage.SUCCESS.getMessage(),
                ResponseCode.SUCCESS.getCode(),
                System.currentTimeMillis(),
                true,
                data
        );
    }

    public static <T> BaseResponse<T> success(T data, String message, Integer code) {
        return new BaseResponse<>(message, code, System.currentTimeMillis(), true, data);
    }

    public static <T> BaseResponse<T> fail(T errors){
        return new BaseResponse<>(
                ResponseMessage.FAILED.getMessage(),
                ResponseCode.ERROR.getCode(),
                System.currentTimeMillis(),
                false,
                errors
        );
    }

    public static <T> BaseResponse<T> fail(T errors, String message, Integer code){
        return new BaseResponse<>(message, code, System.currentTimeMillis(), false, errors);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "message='" + message + '\'' +
                ", code=" + code +
                ", time=" + time +
                ", successful=" + successful +
                ", data=" + data +
                '}';
    }
}

