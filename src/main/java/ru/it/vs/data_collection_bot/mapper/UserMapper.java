package ru.it.vs.data_collection_bot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.it.vs.data_collection_bot.dto.UserDto;
import ru.it.vs.data_collection_bot.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "isAdmin", source = "admin")
    UserDto toUserDto(User user);

    @Mapping(target = "isAdmin", source = "admin")
    User toUser(UserDto userDto);
}
