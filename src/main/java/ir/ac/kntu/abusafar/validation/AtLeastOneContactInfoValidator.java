package ir.ac.kntu.abusafar.validation;

import ir.ac.kntu.abusafar.dto.User.SignUpRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneContactInfoValidator implements ConstraintValidator<AtLeastOneContactInfo, SignUpRequestDTO> {

    @Override
    public void initialize(AtLeastOneContactInfo constraintAnnotation) {
    }

    @Override
    public boolean isValid(SignUpRequestDTO signUpRequest, ConstraintValidatorContext context) {
        if (signUpRequest == null) {
            return false;
        }
        boolean isEmailProvided = signUpRequest.getEmail() != null && !signUpRequest.getEmail().trim().isEmpty();
        boolean isPhoneNumberProvided = signUpRequest.getPhoneNumber() != null && !signUpRequest.getPhoneNumber().trim().isEmpty();
        return isEmailProvided || isPhoneNumberProvided;
    }
}
