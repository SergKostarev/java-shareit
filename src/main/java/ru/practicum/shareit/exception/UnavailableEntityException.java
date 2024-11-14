package ru.practicum.shareit.exception;

import lombok.Getter;

public class UnavailableEntityException extends RuntimeException {
    @Getter
    private final String data;

    public UnavailableEntityException(final String message, String data) {
        super(message);
        this.data = data;
    }
}
