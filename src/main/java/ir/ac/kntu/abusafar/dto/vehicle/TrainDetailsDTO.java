package ir.ac.kntu.abusafar.dto.vehicle;

import ir.ac.kntu.abusafar.util.constants.enums.TrainRoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class TrainDetailsDTO {

    private Short stars;

    private TrainRoomType roomType;
}
