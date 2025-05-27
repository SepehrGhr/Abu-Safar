package ir.ac.kntu.abusafar.model;
import ir.ac.kntu.abusafar.util.constants.enums.ReportLink;
import ir.ac.kntu.abusafar.util.constants.enums.ReportStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
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