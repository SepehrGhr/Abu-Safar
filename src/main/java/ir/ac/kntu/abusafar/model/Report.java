package ir.ac.kntu.abusafar.model;
import ir.ac.kntu.abusafar.util.constants.enums.ReportLink;
import ir.ac.kntu.abusafar.util.constants.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class Report {

    private Long reportId;

    private Long userId;

    private ReportLink linkType;

    private Long linkId;

    private String topic;

    private String content;

    private ReportStatus reportStatus;


}