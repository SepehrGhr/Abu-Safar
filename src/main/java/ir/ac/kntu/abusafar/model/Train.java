package ir.ac.kntu.abusafar.model;

import ir.ac.kntu.abusafar.util.constants.enums.TrainRoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Train {

    private Long tripId;

    private Short stars;

    private TrainRoomType roomType;

}