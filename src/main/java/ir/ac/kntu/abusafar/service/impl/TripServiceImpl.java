package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.repository.AdditionalServiceDAO;
import ir.ac.kntu.abusafar.service.TripService;
import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TripServiceImpl implements TripService {
    private final AdditionalServiceDAO additionalServiceDAO;

    @Autowired
    public TripServiceImpl(AdditionalServiceDAO additionalServiceDAO) {
        this.additionalServiceDAO = additionalServiceDAO;
    }


    public List<ServiceType> getServicesForTrip(Long tripId) {
        return additionalServiceDAO.findServiceTypesByTripId(tripId);
    }
}
