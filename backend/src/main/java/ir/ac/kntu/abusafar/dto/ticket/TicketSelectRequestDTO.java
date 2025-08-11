package ir.ac.kntu.abusafar.dto.ticket;

import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class TicketSelectRequestDTO {
    @NotNull(message = "TripId cannot be null.")
    private Long tripId;
    @NotNull(message = "Age cannot be null.")
    private AgeRange ageCategory;
}
