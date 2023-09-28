package nl.ilovecoding.userservice.mappers;

import nl.ilovecoding.userservice.domain.User;
import nl.ilovecoding.userservice.model.UserDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userToUserDto(User user);
 User userDtoToUser(UserDto userDto);
}
