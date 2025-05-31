package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationService {

    List<String> getCitiesByProvince(String provinceName);

    List<String> getProvincesByCountry(String countryName);

    List<LocationResponseDTO> getLocationsByCity(String cityName);

    List<LocationResponseDTO> findLocationsByCityName(String cityName);

    List<LocationResponseDTO> getLocationsByProvince(String provinceName);

    List<LocationResponseDTO> getLocationsByCountry(String countryName);

    List<String> getAllCountries();

    List<String> getAllProvinces();

    List<String> getAllCities();

    List<Long> findLocationIdByDetails(String city, String province, String country);

    Optional<LocationResponseDTO> getLocationById(Long id);
}
