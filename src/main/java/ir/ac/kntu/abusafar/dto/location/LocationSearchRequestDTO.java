package ir.ac.kntu.abusafar.dto.location;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationSearchRequestDTO {
    private String country;
    private String province;
    private String city;
}
