package ir.ac.kntu.abusafar.controller;

import ir.ac.kntu.abusafar.dto.report.ReportRequestDTO;
import ir.ac.kntu.abusafar.dto.response.BaseResponse;
import ir.ac.kntu.abusafar.model.Report;
import ir.ac.kntu.abusafar.service.ReportService;
import ir.ac.kntu.abusafar.util.constants.Routes;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.API_KEY + "/reports")
public class SubmitReportController {

    private final ReportService reportService;

    @Autowired
    public SubmitReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Report>> submitReport(@Valid @RequestBody ReportRequestDTO reportRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        Report createdReport = reportService.createReport(reportRequestDTO, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(createdReport, "Report submitted successfully.", HttpStatus.CREATED.value()));
    }
}