package ir.ac.kntu.abusafar.service;

import ir.ac.kntu.abusafar.dto.report.ReportRequestDTO;
import ir.ac.kntu.abusafar.model.Report;

public interface ReportService {
    Report createReport(ReportRequestDTO reportDTO, Long userId);
}