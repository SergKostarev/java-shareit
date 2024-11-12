package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class UnavailableEntityException extends RuntimeException {
    private final String data;

    public UnavailableEntityException(final String message, String data) {
        super(message);
        this.data = data;
    }
}
