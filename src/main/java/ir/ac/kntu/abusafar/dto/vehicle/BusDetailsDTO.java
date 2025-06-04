package ir.ac.kntu.abusafar.dto.vehicle;

import ir.ac.kntu.abusafar.util.constants.enums.BusChairCountType;
import ir.ac.kntu.abusafar.util.constants.enums.BusClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class BusDetailsDTO {
    private BusClass classType;

    private BusChairCountType chairType;
}
