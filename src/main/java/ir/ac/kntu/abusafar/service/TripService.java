package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.vehicle.BusDetailsDTO;
import ir.ac.kntu.abusafar.dto.vehicle.FlightDetailsDTO;
import ir.ac.kntu.abusafar.dto.vehicle.TrainDetailsDTO;
import ir.ac.kntu.abusafar.model.Company;
import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;

import java.util.List;
import java.util.Optional;

public interface TripService {
    List<ServiceType> getServicesForTrip(Long tripId);
    Optional<FlightDetailsDTO> getFlightDetails(Long tripId);
    Optional<BusDetailsDTO> getBusDetails(Long tripId);
    Optional<TrainDetailsDTO> getTrainDetails(Long tripId);
    Optional<Company> getCompanyById(Long companyId);
}
