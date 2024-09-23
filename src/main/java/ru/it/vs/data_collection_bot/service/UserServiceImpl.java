package ru.it.vs.data_collection_bot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.it.vs.data_collection_bot.dto.UserDto;
import ru.it.vs.data_collection_bot.mapper.UserMapper;
import ru.it.vs.data_collection_bot.repository.UserRepo;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserMapper mapper;

    @Override
    @Transactional
    public void addUser(UserDto user) {
        userRepo.save(mapper.toUser(user));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserExistByChatId(Long chatId) {
        return userRepo.existsByChatId(chatId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByChatId(Long chatId) {
        return mapper.toUserDto(userRepo.findUserByChatId(chatId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers() {
        return userRepo.findAll().stream()
                .map(mapper::toUserDto)
                .toList();
    }
}
