package ir.ac.kntu.abusafar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.API_KEY + "/reports")
@Tag(name = "User Reports", description = "APIs for submitting user feedback and reports")
@SecurityRequirement(name = "bearerAuth")
public class SubmitReportController {

    private final ReportService reportService;

    @Autowired
    public SubmitReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(
            summary = "Submit a New Report",
            description = "Allows an authenticated user to submit a report related to a reservation or ticket. The report is then reviewable by an admin."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Report submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid report data provided")
    })
    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Report>> submitReport(Authentication authentication,
                                                             @Valid @RequestBody ReportRequestDTO reportRequestDTO) {
        Long userId = Long.parseLong(authentication.getName());
        Report createdReport = reportService.createReport(reportRequestDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(createdReport, "Report submitted successfully.", HttpStatus.CREATED.value()));
    }
}