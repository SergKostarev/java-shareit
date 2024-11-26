package ru.practicum.shareit.booking;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState getState(String state) {
        return switch (state) {
            case ("current") -> CURRENT;
            case ("past") -> PAST;
            case ("future") -> FUTURE;
            case ("waiting") -> WAITING;
            case ("rejected") -> REJECTED;
            default -> ALL;
        };
    }
}