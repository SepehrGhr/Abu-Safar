package ir.ac.kntu.abusafar.dto.vehicle;

import ir.ac.kntu.abusafar.util.constants.enums.TrainRoomType;

public record TrainDetailsDTO(Short stars, TrainRoomType roomType) implements VehicleDetailsDTO {}