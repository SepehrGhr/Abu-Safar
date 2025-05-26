package ir.ac.kntu.abusafar.model;

import ir.ac.kntu.abusafar.util.constants.enums.ContactType;

public class UserContact {
    private Long userId;
    private ContactType contactType;
    private String contactInfo;

    public UserContact(Long userId, ContactType contactType, String contactInfo) {
        this.userId = userId;
        this.contactType = contactType;
        this.contactInfo = contactInfo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
