package ir.ac.kntu.abusafar.mapper;

import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.dto.User.UserInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ir.ac.kntu.abusafar.util.constants.enums.UserType;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserInfoDTO toDTO(User user);

    User toEntity(UserInfoDTO dto);

     @Named("stringToUserType")
     default UserType stringToUserType(String userType) {
         return userType == null ? null : UserType.valueOf(userType.toUpperCase());
     }
}
