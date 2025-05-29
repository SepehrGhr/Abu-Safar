package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.model.Ticket;
import ir.ac.kntu.abusafar.repository.params.TicketSearchParameters;

import java.util.List;

public interface TicketDAO {
    List<Ticket> findTicketsByCriteria(TicketSearchParameters params);
}
