package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class DuplicatedDataException extends RuntimeException {

    private final String data;

    public DuplicatedDataException(final String message, String data) {
        super(message);
        this.data = data;
    }
}