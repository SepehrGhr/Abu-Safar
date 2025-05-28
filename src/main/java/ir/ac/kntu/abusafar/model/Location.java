package ir.ac.kntu.abusafar.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Location {

    private Long locationId;

    private String country;

    private String province;

    private String city;

}
