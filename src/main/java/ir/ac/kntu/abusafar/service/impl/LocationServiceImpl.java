package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.exception.LocationNotFoundException;
import ir.ac.kntu.abusafar.mapper.LocationMapper;
import ir.ac.kntu.abusafar.model.Location;
import ir.ac.kntu.abusafar.repository.LocationDAO;
import ir.ac.kntu.abusafar.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {
    private final LocationResponseDTO locationResponseDTO;

    private final LocationDAO locationDAO;

    @Autowired
    public LocationServiceImpl(LocationResponseDTO locationResponseDTO, LocationDAO locationDAO) {
        this.locationResponseDTO = locationResponseDTO;
        this.locationDAO = locationDAO;
    }

    @Override
    public List<String> getCitiesByProvince(String provinceName) {
        List<String> cities = locationDAO.findCitiesByProvince(provinceName);
        if (cities.isEmpty()) {
            throw new LocationNotFoundException("No cities found for province: " + provinceName);
        }
        return cities;
    }

    @Override
    public List<String> getProvincesByCountry(String countryName) {
        List<String> provinces = locationDAO.findProvincesByCountry(countryName);
        if (provinces.isEmpty()) {
            throw new LocationNotFoundException("No provinces found for country: " + countryName);
        }
        return provinces;
    }

    @Override
    public List<LocationResponseDTO> getLocationsByCity(String cityName) {
        List<Location> locations = locationDAO.findByCity(cityName);
        if (locations.isEmpty()) {
            throw new LocationNotFoundException("No locations found for city: " + cityName);
        }
        return locations.stream()
                .map(LocationMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationResponseDTO> getLocationsByProvince(String provinceName) {
        List<Location> locations = locationDAO.findByProvince(provinceName);
        if (locations.isEmpty()) {
            throw new LocationNotFoundException("No locations found for province: " + provinceName);
        }
        return locations.stream()
                .map(LocationMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocationResponseDTO> getLocationsByCountry(String countryName) {
        List<Location> locations = locationDAO.findByCountry(countryName);
        if (locations.isEmpty()) {
            throw new LocationNotFoundException("No locations found for country: " + countryName);
        }
        return locations.stream()
                .map(LocationMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllCountries() {
        List<String> countries = locationDAO.findAllCountries();
        if (countries.isEmpty()) {
            throw new LocationNotFoundException("No countries found in the system.");
        }
        return countries;
    }

    @Override
    public List<String> getAllProvinces() {
        List<String> provinces = locationDAO.findAllProvinces();
        if (provinces.isEmpty()) {
            throw new LocationNotFoundException("No provinces found in the system.");
        }
        return provinces;
    }

    @Override
    public List<String> getAllCities() {
        List<String> cities = locationDAO.findAllCities();
        if (cities.isEmpty()) {
            throw new LocationNotFoundException("No cities found in the system.");
        }
        return cities;
    }
}
