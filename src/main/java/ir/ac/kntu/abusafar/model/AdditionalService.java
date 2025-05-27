package ir.ac.kntu.abusafar.model;

import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AdditionalService {

    private Long tripId;

    private ServiceType serviceType;
}