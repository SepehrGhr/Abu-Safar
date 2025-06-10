package ir.ac.kntu.abusafar.mapper;

import ir.ac.kntu.abusafar.dto.report.ReportResponseDTO;
import ir.ac.kntu.abusafar.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReportMapper {

    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    ReportResponseDTO toDTO(Report report);

    Report toEntity(ReportResponseDTO dto);
}