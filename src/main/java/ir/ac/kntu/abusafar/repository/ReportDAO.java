package ir.ac.kntu.abusafar.repository;

import ir.ac.kntu.abusafar.model.Report;

import java.util.List;
import java.util.Optional;

public interface ReportDAO {
    Report save(Report report);
    Optional<Report> findById(Long reportId);
    List<Report> findAll();
    List<Report> findAllByUserId(Long userId);
}