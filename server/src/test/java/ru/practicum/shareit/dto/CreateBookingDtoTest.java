package ru.practicum.shareit.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.time.LocalDateTime;

@JsonTest
public class CreateBookingDtoTest {

    @Autowired
    private JacksonTester<CreateBookingDto> json;

    @Test
    void serialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 23, 58, 00);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 23, 59, 00);
        CreateBookingDto dto = new CreateBookingDto(null, start, end, 1L);
        JsonContent<CreateBookingDto> content = json.write(dto);
        Assertions.assertThat(content).hasJsonPathNumberValue("@.itemId");
        Assertions.assertThat(content)
                .extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        Assertions.assertThat(content).hasJsonPathValue("@.start");
        Assertions.assertThat(content)
                .extractingJsonPathValue("@.start").isEqualTo("2025-01-01T23:58:00");
        Assertions.assertThat(content).hasJsonPathValue("@.end");
        Assertions.assertThat(content)
                .extractingJsonPathValue("@.end").isEqualTo("2025-01-01T23:59:00");
        Assertions.assertThat(content).doesNotHaveJsonPathValue("@.id");
    }
}
