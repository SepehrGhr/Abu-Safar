package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;

import java.util.List;

public interface TripService {
    List<ServiceType> getServicesForTrip(Long tripId);
}
