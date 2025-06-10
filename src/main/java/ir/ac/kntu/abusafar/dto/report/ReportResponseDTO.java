package ir.ac.kntu.abusafar.dto.report;

import ir.ac.kntu.abusafar.util.constants.enums.ReportLink;
import ir.ac.kntu.abusafar.util.constants.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDTO {

    private Long reportId;
    private Long userId;
    private ReportLink linkType;
    private Long linkId;
    private String topic;
    private String content;
    private ReportStatus reportStatus;
}