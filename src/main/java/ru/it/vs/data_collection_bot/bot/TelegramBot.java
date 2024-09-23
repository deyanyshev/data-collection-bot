package ru.it.vs.data_collection_bot.bot;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.it.vs.data_collection_bot.config.BotConfig;
import ru.it.vs.data_collection_bot.dto.UserDto;
import ru.it.vs.data_collection_bot.enums.BotState;
import ru.it.vs.data_collection_bot.service.UserService;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.it.vs.data_collection_bot.bot.KeyboardButtons.*;
import static ru.it.vs.data_collection_bot.enums.BotState.*;
import static ru.it.vs.data_collection_bot.utils.constants.BotConstants.CHAT_STATES_MAP;
import static ru.it.vs.data_collection_bot.utils.constants.BotConstants.START_COMMAND_TEXT;
import static ru.it.vs.data_collection_bot.utils.constants.ButtonConstants.*;
import static ru.it.vs.data_collection_bot.utils.constants.TextConstants.*;

@Component
public class TelegramBot extends AbilityBot {

    public final Map<Long, BotState> chatStates;
    private final Map<Long, UserDto> users;

    @Autowired
    private UserService userService;

    private TelegramBot(BotConfig config) {
        super(config.getToken(), config.getName());

        chatStates = db.getMap(CHAT_STATES_MAP);
        users = new HashMap<>();
    }

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;

        Message message = update.getMessage();
        Long chatId = message.getChatId();

        try {
            if (userService.isUserExistByChatId(chatId)) {
                UserDto user = userService.getUserByChatId(chatId);

                if (!user.isAdmin()) {
                    sendMessage(chatId, USER_EXISTENT_TEXT);
                    return;
                }

                handleAdmin(chatId, message.getText());
            } else {
                handleUser(chatId, message);
            }
        } catch (Exception e) {
            Logger logger = Logger.getAnonymousLogger();
            logger.log(Level.SEVERE, "Error", e);
            sendMessage(chatId, ERROR_TEXT);
        }
    }

    public void start(Long chatId) {
        if (userService.getUsers().isEmpty()) {
            userService.addUser(UserDto.builder()
                    .chatId(chatId)
                    .isAdmin(true)
                    .build()
            );

            sendMessage(chatId, ADMIN_TEXT, keyboardMarkup(List.of(USER_LIST_BUTTON, SEND_MESSAGE_BUTTON)));
            return;
        }

        sendMessage(chatId, START_TEXT);
        sendMessage(chatId, CONTACT_TEXT, keyboardMarkup(List.of(CONTACT_BUTTON)));
        chatStates.put(chatId, CONTACT);
    }

    public void handleUser(Long chatId, Message message) {
        if (Objects.equals(message.getText(), START_COMMAND_TEXT)) {
            start(chatId);
            return;
        }

        switch (chatStates.get(chatId)) {
            case CONTACT -> {
                Contact contact = message.getContact();

                users.put(chatId, UserDto.builder()
                        .chatId(chatId)
                        .phone(contact.getPhoneNumber())
                        .isAdmin(false)
                        .build()
                );

                chatStates.put(chatId, NAME);
                sendMessage(chatId, NAME_TEXT);
            }
            case NAME -> {
                UserDto user = users.get(chatId);
                user.setName(message.getText());
                users.put(chatId, user);

                chatStates.put(chatId, POSITION);
                sendMessage(chatId, POSITION_TEXT);
            }
            case POSITION -> {
                UserDto user = users.get(chatId);
                user.setPosition(message.getText());
                users.put(chatId, user);

                chatStates.put(chatId, EMPLOYEES_NUMBER);
                sendMessage(chatId, EMPLOYEES_NUMBER_TEXT);
            }
            case EMPLOYEES_NUMBER -> {
                UserDto user = users.get(chatId);
                user.setEmployeesNumber(Long.valueOf(message.getText()));
                users.put(chatId, user);

                chatStates.put(chatId, SEATS_NUMBER);
                sendMessage(chatId, SEATS_NUMBER_TEXT);
            }
            case SEATS_NUMBER -> {
                UserDto user = users.get(chatId);
                user.setSeatsNumber(Long.valueOf(message.getText()));
                users.put(chatId, user);

                chatStates.put(chatId, INN);
                sendMessage(chatId, INN_TEXT, keyboardMarkup(List.of(SKIP_BUTTON)));
            }
            case INN -> {
                if (!Objects.equals(message.getText(), SKIP_BUTTON_TEXT)) {
                    UserDto user = users.get(chatId);
                    user.setInn(message.getText());
                    users.put(chatId, user);
                }

                userService.addUser(users.get(chatId));
                chatStates.remove(chatId);
                sendMessage(chatId, FINISHED_TEXT);
            }
        }
    }

    public void handleAdmin(Long chatId, String text) {
        switch (text) {
            case USER_LIST_BUTTON_TEXT -> {
                StringBuilder userListText = new StringBuilder();

                for (UserDto user : userService.getUsers()) {
                    if (user.isAdmin()) continue;
                    userListText.append(String.format(USER_LINE_TEXT, user.getName(), user.getPhone()));
                }

                sendMessage(chatId, userListText.toString(), keyboardMarkup(List.of(USER_LIST_BUTTON, SEND_MESSAGE_BUTTON)));
            }
            case SEND_MESSAGE_BUTTON_TEXT -> {
                sendMessage(chatId, SEND_MESSAGE_TEXT);
                chatStates.put(chatId, MESSAGE);
            }
            default -> {
                if (chatStates.get(chatId) == MESSAGE) {
                    for (UserDto user : userService.getUsers()) {
                        if (user.isAdmin()) continue;
                        sendMessage(user.getChatId(), text);
                    }

                    sendMessage(chatId, SUCCESS_TEXT, keyboardMarkup(List.of(USER_LIST_BUTTON, SEND_MESSAGE_BUTTON)));
                    chatStates.remove(chatId);
                } else {
                    sendMessage(chatId, ERROR_TEXT, keyboardMarkup(List.of(USER_LIST_BUTTON, SEND_MESSAGE_BUTTON)));
                }
            }
        }
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text) {
        execute(SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build()
        );
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String text, ReplyKeyboardMarkup markup) {
        execute(SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(markup)
                .build()
        );
    }

    @Override
    public long creatorId() {
        return 0;
    }
}
