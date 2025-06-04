package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.vehicle.*;
import ir.ac.kntu.abusafar.repository.AdditionalServiceDAO;
import ir.ac.kntu.abusafar.repository.BusDAO;
import ir.ac.kntu.abusafar.repository.FlightDAO;
import ir.ac.kntu.abusafar.repository.TrainDAO;
import ir.ac.kntu.abusafar.service.TripService;
import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TripServiceImpl implements TripService {

    private final AdditionalServiceDAO additionalServiceDAO;
    private final TrainDAO trainDAO;
    private final BusDAO busDAO;
    private final FlightDAO flightDAO;

    @Autowired
    public TripServiceImpl(
            AdditionalServiceDAO additionalServiceDAO,
            TrainDAO trainDAO,
            BusDAO busDAO,
            FlightDAO flightDAO) {
        this.additionalServiceDAO = additionalServiceDAO;
        this.trainDAO = trainDAO;
        this.busDAO = busDAO;
        this.flightDAO = flightDAO;
    }

    @Override
    public List<ServiceType> getServicesForTrip(Long tripId) {
        if (tripId == null) {
            return Collections.emptyList();
        }
        return additionalServiceDAO.findServiceTypesByTripId(tripId);
    }

    @Override
    public Optional<TrainDetailsDTO> getTrainDetails(Long tripId) {
        if (tripId == null) {
            return Optional.empty();
        }
        return trainDAO.findTrainDetailsByTripId(tripId);
    }

    @Override
    public Optional<BusDetailsDTO> getBusDetails(Long tripId) {
        if (tripId == null) {
            return Optional.empty();
        }
        return busDAO.findBusDetailsByTripId(tripId);
    }

    @Override
    public Optional<FlightDetailsDTO> getFlightDetails(Long tripId) {
        if (tripId == null) {
            return Optional.empty();
        }
        return flightDAO.findFlightDetailsByTripId(tripId);
    }
}