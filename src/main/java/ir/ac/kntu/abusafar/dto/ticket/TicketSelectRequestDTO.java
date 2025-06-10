package ir.ac.kntu.abusafar.dto.ticket;

import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TicketSelectRequestDTO {
    @NotNull(message = "Trip_id cannot be null.")
    private Long trip_id;
    @NotNull(message = "Age cannot be null.")
    private AgeRange ageCategory;
}
