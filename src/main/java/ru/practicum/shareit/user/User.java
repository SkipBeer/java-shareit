package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class User {
    private Long id;
    private String name;

    @NonNull
    @Email
    private String email;
}
