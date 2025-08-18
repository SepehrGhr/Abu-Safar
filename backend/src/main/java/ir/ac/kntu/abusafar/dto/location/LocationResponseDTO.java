package ir.ac.kntu.abusafar.dto.location;

public record LocationResponseDTO(
        Long locationId,
        String country,
        String province,
        String city
) {
}
