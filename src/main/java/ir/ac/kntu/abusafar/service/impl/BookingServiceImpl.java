package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.reservation.InitialBookResultDTO;
import ir.ac.kntu.abusafar.dto.ticket.TicketSelectRequestDTO;
import ir.ac.kntu.abusafar.repository.ReservationDAO;
import ir.ac.kntu.abusafar.repository.TicketDAO;
import ir.ac.kntu.abusafar.service.BookingService;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl implements BookingService {

//    private final TicketDAO ticketDAO;
//    private final ReservationDAO reservationDAO;
//    private final RedisService redisService;

    // Constants for Redis
    private static final String REDIS_RESERVATION_EXPIRE_PREFIX = "reservation:expire:";
    private static final String REDIS_RESERVATION_REMIND_PREFIX = "reservation:remind:";
    private static final long TEN_MINUTES_IN_SECONDS = 10 * 60;
    private static final long FIVE_MINUTES_IN_SECONDS = 5 * 60;
    private static final String REDIS_KEY_VALUE = "active";
    public InitialBookResultDTO createReservation(TicketSelectRequestDTO ticket){

    }
}
