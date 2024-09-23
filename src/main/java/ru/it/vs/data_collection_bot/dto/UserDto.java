package ru.it.vs.data_collection_bot.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
    private String phone;
    private Long chatId;
    private String name;
    private String position;
    private Long employeesNumber;
    private Long seatsNumber;
    private String inn;
    private boolean isAdmin;
}
