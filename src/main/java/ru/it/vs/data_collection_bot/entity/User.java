package ru.it.vs.data_collection_bot.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@Entity
@Table(name = User.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Builder
public class User {

    public static final String TABLE_NAME = "users";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Fields.id, nullable = false)
    private Long id;

    private String phone;
    private Long chatId;
    private String name;
    private String position;
    private Long employeesNumber;
    private Long seatsNumber;
    private String inn;
    private boolean isAdmin;
}
