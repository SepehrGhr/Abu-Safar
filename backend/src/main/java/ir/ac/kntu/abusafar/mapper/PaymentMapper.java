package ir.ac.kntu.abusafar.mapper;

import ir.ac.kntu.abusafar.dto.payment.PaymentRecordDTO;
import ir.ac.kntu.abusafar.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMapper {

    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(source = "paymentStatus", target = "paymentStatus", qualifiedByName = "enumToString")
    @Mapping(source = "paymentType", target = "paymentType", qualifiedByName = "enumToString")
    PaymentRecordDTO toDTO(Payment payment);

    @Named("enumToString")
    default <T extends Enum<T>> String enumToString(T enumValue) {
        return enumValue == null ? null : enumValue.name();
    }
}