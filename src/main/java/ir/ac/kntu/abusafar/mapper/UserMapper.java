package ir.ac.kntu.abusafar.mapper;

import ir.ac.kntu.abusafar.model.User;
import ir.ac.kntu.abusafar.dto.User.UserInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ir.ac.kntu.abusafar.util.constants.enums.UserType;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "userTypeToString")
    UserInfoDTO toUserInfoDTO(User user);


    @Named("userTypeToString")
    default String userTypeToString(UserType userType) {
        return userType == null ? null : userType.name();
    }

     @Mapping(source = "userType", target = "userType", qualifiedByName = "stringToUserType")
     User toUser(UserInfoDTO userInfoDTO);

     @Named("stringToUserType")
     default UserType stringToUserType(String userType) {
         return userType == null ? null : UserType.valueOf(userType.toUpperCase());
     }
}
