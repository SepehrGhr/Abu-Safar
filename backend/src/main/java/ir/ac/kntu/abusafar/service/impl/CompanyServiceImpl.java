package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.model.Company;
import ir.ac.kntu.abusafar.repository.CompanyDAO;
import ir.ac.kntu.abusafar.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyDAO companyDAO;

    @Autowired
    public CompanyServiceImpl(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }

    @Override
    public List<Company> getCompaniesByVehicleType(String vehicleType) {
        return companyDAO.findByVehicleType(vehicleType);
    }
}
