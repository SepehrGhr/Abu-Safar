package ir.ac.kntu.abusafar.model;

import ir.ac.kntu.abusafar.util.constants.enums.TrainRoomType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Train {

    private Long tripId;

    private Short stars;

    private TrainRoomType roomType;
}