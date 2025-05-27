package ir.ac.kntu.abusafar.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class Location {

    private Long locationId;

    private String country;

    private String province;

    private String city;

}
