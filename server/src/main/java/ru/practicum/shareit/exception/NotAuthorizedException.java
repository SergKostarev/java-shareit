package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NotAuthorizedException extends RuntimeException {

    private final Long entityId;
    private final Long userId;

    public NotAuthorizedException(String message, Long entityId, Long userId) {
        super(message);
        this.entityId = entityId;
        this.userId = userId;
    }
}
