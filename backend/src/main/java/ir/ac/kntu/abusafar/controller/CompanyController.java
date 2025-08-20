package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.model.Company;
import ir.ac.kntu.abusafar.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/{vehicleType}")
    public ResponseEntity<BaseResponse<List<Company>>> getCompaniesByVehicleType(@PathVariable String vehicleType) {
        List<Company> companies = companyService.getCompaniesByVehicleType(vehicleType.toUpperCase());
        return ResponseEntity.ok(BaseResponse.success(companies));
    }
}