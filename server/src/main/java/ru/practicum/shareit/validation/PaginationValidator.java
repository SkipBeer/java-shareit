package ru.practicum.shareit.validation;

import ru.practicum.shareit.exceptions.exceptions.IncorrectRequestParamException;

public class PaginationValidator {

    public static void validatePagination(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new IncorrectRequestParamException("Некорректные параметры постраничного отображения");
        } else if ((from == 0 && size == 0)) {
            throw new IncorrectRequestParamException("Некорректные параметры постраничного отображения");
        }
    }
}
