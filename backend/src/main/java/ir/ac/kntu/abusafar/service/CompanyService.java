package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.model.Company;
import java.util.List;

public interface CompanyService {
    List<Company> getCompaniesByVehicleType(String vehicleType);
}