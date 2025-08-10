package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.util.constants.enums.ServiceType;

import java.util.List;

public interface AdditionalServiceDAO {
    List<ServiceType> findServiceTypesByTripId(Long tripId);
}
