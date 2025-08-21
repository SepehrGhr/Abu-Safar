package ir.ac.kntu.abusafar.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailDTO extends UserInfoDTO {
    private String email;
    private String phoneNumber;
}