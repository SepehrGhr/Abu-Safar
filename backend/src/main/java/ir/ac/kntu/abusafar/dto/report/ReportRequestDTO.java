package ir.ac.kntu.abusafar.dto.report;

import ir.ac.kntu.abusafar.util.constants.enums.ReportLink;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestDTO {

    @NotNull(message = "Link type cannot be null")
    private ReportLink linkType;

    @NotNull(message = "Link ID cannot be null")
    private Long linkId;

    @NotBlank(message = "Topic cannot be blank")
    @Size(max = 100, message = "Topic cannot exceed 100 characters")
    private String topic;

    @NotBlank(message = "Content cannot be blank")
    private String content;
}