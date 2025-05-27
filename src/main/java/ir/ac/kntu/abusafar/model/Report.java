package ir.ac.kntu.abusafar.model;
import ir.ac.kntu.abusafar.util.constants.enums.ReportLink;
import ir.ac.kntu.abusafar.util.constants.enums.ReportStatus;

public class Report {

    private Long reportId;

    private Long userId;

    private ReportLink linkType;

    private Long linkId;

    private String topic;

    private String content;

    private ReportStatus reportStatus;

    public Report(Long reportId, Long userId, ReportLink linkType, Long linkId, String topic, String content, ReportStatus reportStatus) {
        this.reportId = reportId;
        this.userId = userId;
        this.linkType = linkType;
        this.linkId = linkId;
        this.topic = topic;
        this.content = content;
        this.reportStatus = reportStatus;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ReportLink getLinkType() {
        return linkType;
    }

    public void setLinkType(ReportLink linkType) {
        this.linkType = linkType;
    }

    public Long getLinkId() {
        return linkId;
    }

    public void setLinkId(Long linkId) {
        this.linkId = linkId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ReportStatus getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(ReportStatus reportStatus) {
        this.reportStatus = reportStatus;
    }
}