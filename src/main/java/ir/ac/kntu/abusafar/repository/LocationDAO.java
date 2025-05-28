package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.model.Location;

import java.util.List;
import java.util.Optional;

public interface LocationDAO {
    Optional<Location> findById(Long locationId);

    List<String> findCitiesByProvince(String provinceName);

    List<String> findProvincesByCountry(String countryName);

    List<Location> findByCity(String cityName);

    List<Location> findByProvince(String provinceName);

    List<Location> findByCountry(String countryName);

    List<String> findAllCountries();

    List<String> findAllProvinces();

    List<String> findAllCities();

   // List<Location> findAll();

   // List<Location> findAll(int page, int size);
}
