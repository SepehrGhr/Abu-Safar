package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.repository.params.TicketSearchParameters;
import ir.ac.kntu.abusafar.util.constants.enums.AgeRange;

import java.util.List;
import java.util.Optional;

public interface TicketDAO {
    List<Ticket> findTicketsByCriteria(TicketSearchParameters params);
    Optional<Ticket> findById(Long tripId, AgeRange age);
}
