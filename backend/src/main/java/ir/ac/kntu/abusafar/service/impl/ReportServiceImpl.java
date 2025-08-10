package ir.ac.kntu.abusafar.service.impl;

import ir.ac.kntu.abusafar.dto.report.ReportRequestDTO;
import ir.ac.kntu.abusafar.model.Report;
import ir.ac.kntu.abusafar.repository.ReportDAO;
import ir.ac.kntu.abusafar.service.ReportService;
import ir.ac.kntu.abusafar.util.constants.enums.ReportStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportDAO reportDAO;

    @Autowired
    public ReportServiceImpl(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    @Override
    @Transactional
    public Report createReport(ReportRequestDTO reportDTO, Long userId) {
        Report report = new Report(
                null,
                userId,
                reportDTO.getLinkType(),
                reportDTO.getLinkId(),
                reportDTO.getTopic(),
                reportDTO.getContent(),
                ReportStatus.PENDING
        );

        return reportDAO.save(report);
    }
}