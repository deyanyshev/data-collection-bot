package ru.it.vs.data_collection_bot.service;

import ru.it.vs.data_collection_bot.dto.UserDto;

import java.util.List;

public interface UserService {
    void addUser(UserDto user);
    boolean isUserExistByChatId(Long chatId);
    UserDto getUserByChatId(Long chatId);
    List<UserDto> getUsers();
}
