package ru.it.vs.data_collection_bot.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

import static ru.it.vs.data_collection_bot.utils.constants.ButtonConstants.*;

public class KeyboardButtons {

    public static final KeyboardButton CONTACT_BUTTON = KeyboardButton.builder()
            .text(CONTACT_BUTTON_TEXT)
            .requestContact(true)
            .build();

    public static final KeyboardButton SKIP_BUTTON = KeyboardButton.builder()
            .text(SKIP_BUTTON_TEXT)
            .build();

    public static final KeyboardButton USER_LIST_BUTTON = KeyboardButton.builder()
            .text(USER_LIST_BUTTON_TEXT)
            .build();

    public static final KeyboardButton SEND_MESSAGE_BUTTON = KeyboardButton.builder()
            .text(SEND_MESSAGE_BUTTON_TEXT)
            .build();

    public static ReplyKeyboardMarkup keyboardMarkup(List<KeyboardButton> buttons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(
                buttons.stream().map(button -> {
                    KeyboardRow row = new KeyboardRow();
                    row.add(button);
                    return row;
                }).toList()
        );

        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        return replyKeyboardMarkup;
    }
}
