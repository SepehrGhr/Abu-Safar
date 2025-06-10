package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.exception.LocationNotFoundException;
import ir.ac.kntu.abusafar.service.LocationService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(Routes.API_KEY + "/locations")
public class LocationController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/cities")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<?>> getCities(@RequestParam(required = false) String name) {
        if (name == null || name.trim().isEmpty()) {
            try {
                return ResponseEntity.ok(BaseResponse.success(locationService.getAllCities()));
            } catch (LocationNotFoundException e) {
                return ResponseEntity.ok(BaseResponse.success(Collections.emptyList(), "No cities found in the system.", HttpStatus.OK.value()));
            }
        } else {
            List<LocationResponseDTO> foundCities = locationService.findLocationsByCityName(name);
            if (foundCities.isEmpty()) {
                return ResponseEntity.ok(BaseResponse.success(Collections.emptyList(), "City '" + name + "' not found.", HttpStatus.OK.value()));
            } else {
                return ResponseEntity.ok(BaseResponse.success(foundCities));
            }
        }
    }
}