package ir.ac.kntu.abusafar.dto.vehicle;

import ir.ac.kntu.abusafar.util.constants.enums.BusChairCountType;
import ir.ac.kntu.abusafar.util.constants.enums.BusClass;

public record BusDetailsDTO(BusClass classType, BusChairCountType chairType) implements VehicleDetailsDTO {}