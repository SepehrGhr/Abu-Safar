package ir.ac.kntu.abusafar.model;

import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AdditionalService {

    private Long tripId;

    private ServiceType serviceType;
}