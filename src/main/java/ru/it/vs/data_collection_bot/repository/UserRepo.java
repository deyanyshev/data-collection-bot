package ru.it.vs.data_collection_bot.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import ru.it.vs.data_collection_bot.entity.User;

import java.util.List;

public interface UserRepo extends CrudRepository<User, Long> {
    boolean existsByChatId(Long chatId);
    User findUserByChatId(Long chatId);
    @NotNull
    List<User> findAll();
}
