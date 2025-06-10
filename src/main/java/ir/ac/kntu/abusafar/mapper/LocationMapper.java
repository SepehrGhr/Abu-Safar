package ir.ac.kntu.abusafar.mapper;

import ir.ac.kntu.abusafar.dto.location.LocationResponseDTO;
import ir.ac.kntu.abusafar.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);
    Location toEntity(LocationResponseDTO dto);
    LocationResponseDTO toDTO(Location location);

}
