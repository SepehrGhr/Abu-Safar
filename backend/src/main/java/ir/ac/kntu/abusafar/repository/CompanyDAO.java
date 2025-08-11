package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.model.Company;
import java.util.Optional;

public interface CompanyDAO {
    Optional<Company> findById(Long id);
    Optional<Company> findByName(String name);
}