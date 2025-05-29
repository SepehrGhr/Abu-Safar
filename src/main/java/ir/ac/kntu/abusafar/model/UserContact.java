package ir.ac.kntu.abusafar.model;

import ir.ac.kntu.abusafar.util.constants.enums.ContactType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class UserContact {
    private Long userId;
    private ContactType contactType;
    private String contactInfo;


}
