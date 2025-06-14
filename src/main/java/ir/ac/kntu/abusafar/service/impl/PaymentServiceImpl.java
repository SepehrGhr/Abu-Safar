package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.payment.PaymentRecordDTO;
import ir.ac.kntu.abusafar.dto.payment.PaymentRequestDTO;
import ir.ac.kntu.abusafar.exception.PaymentFailedException;
import ir.ac.kntu.abusafar.exception.ReservationNotFoundException;
import ir.ac.kntu.abusafar.mapper.PaymentMapper;
import ir.ac.kntu.abusafar.model.Payment;
import ir.ac.kntu.abusafar.model.Reservation;
import ir.ac.kntu.abusafar.repository.PaymentDAO;
import ir.ac.kntu.abusafar.repository.ReservationDAO;
import ir.ac.kntu.abusafar.service.PaymentService;
import ir.ac.kntu.abusafar.service.RedisReserveService;
import ir.ac.kntu.abusafar.service.UserService;
import ir.ac.kntu.abusafar.service.NotificationService;
import ir.ac.kntu.abusafar.util.constants.enums.PaymentMeans;
import ir.ac.kntu.abusafar.util.constants.enums.PaymentStatus;
import ir.ac.kntu.abusafar.util.constants.enums.ReserveStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final ReservationDAO reservationDAO;
    private final PaymentDAO paymentDAO;
    private final RedisReserveService redisService;
    private final UserService userService;
    private final NotificationService notificationService;

    private static final String REDIS_RESERVATION_EXPIRE_PREFIX = "reservation:expire:";
    private static final String REDIS_RESERVATION_REMIND_PREFIX = "reservation:remind:";

    @Autowired
    public PaymentServiceImpl(ReservationDAO reservationDAO, PaymentDAO paymentDAO, RedisReserveService redisService, UserService userService, NotificationService notificationService) {
        this.reservationDAO = reservationDAO;
        this.paymentDAO = paymentDAO;
        this.redisService = redisService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public PaymentRecordDTO processPayment(Long userId, PaymentRequestDTO paymentRequest) {
        Long reservationId = paymentRequest.getReservationId();

        Reservation reservation = reservationDAO.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation with ID " + reservationId + " not found."));

        if (!reservation.getUserId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to pay for this reservation.");
        }

        if (reservation.getReserveStatus() != ReserveStatus.RESERVED) {
            throw new PaymentFailedException("Payment cannot be processed. Reservation status is: " + reservation.getReserveStatus());
        }
        if (reservation.getExpirationDatetime().isBefore(OffsetDateTime.now())) {
            throw new PaymentFailedException("Payment cannot be processed. The reservation hold has expired.");
        }

        Payment pendingPayment = paymentDAO.findPendingPayment(reservationId)
                .orElseThrow(() -> new PaymentFailedException("No pending payment found for this reservation."));

        if (paymentRequest.getPaymentMeans() == PaymentMeans.WALLET) {
            userService.debitFromWallet(userId, pendingPayment.getPrice());
        } else {
            System.out.println("Processing " + paymentRequest.getPaymentMeans() + " payment.");
        }

        paymentDAO.updatePaymentStatus(pendingPayment.getPaymentId(), PaymentStatus.SUCCESSFUL);

        notificationService.sendBookingConfirmationEmail(reservation.getReservationId());

        redisService.deleteKey(REDIS_RESERVATION_EXPIRE_PREFIX + reservationId);
        redisService.deleteKey(REDIS_RESERVATION_REMIND_PREFIX + reservationId);

        Payment successfulPayment = paymentDAO.findById(pendingPayment.getPaymentId())
                .orElseThrow(() -> new PaymentFailedException("Failed to retrieve payment details after processing."));

        return PaymentMapper.INSTANCE.toDTO(successfulPayment);
    }
}