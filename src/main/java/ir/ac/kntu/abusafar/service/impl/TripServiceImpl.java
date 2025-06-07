package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.vehicle.*;
import ir.ac.kntu.abusafar.model.Company;
import ir.ac.kntu.abusafar.repository.*;
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
    private final CompanyDAO companyDAO;

    @Autowired
    public TripServiceImpl(
            AdditionalServiceDAO additionalServiceDAO,
            TrainDAO trainDAO,
            BusDAO busDAO,
            FlightDAO flightDAO, CompanyDAO companyDAO) {
        this.additionalServiceDAO = additionalServiceDAO;
        this.trainDAO = trainDAO;
        this.busDAO = busDAO;
        this.flightDAO = flightDAO;
        this.companyDAO = companyDAO;
    }

    @Override
    public List<ServiceType> getServicesForTrip(Long tripId) {
        if (tripId == null) {
            return Collections.emptyList();
        }
        return additionalServiceDAO.findServiceTypesByTripId(tripId);
    }

    @Override
    public Optional<Company> getCompanyById(Long companyId) {
        if (companyId == null) {
            return Optional.empty();
        }
        return companyDAO.findById(companyId);
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