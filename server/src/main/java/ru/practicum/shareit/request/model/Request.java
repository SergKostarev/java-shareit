package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "description", nullable = false)
    private String description;

    @Setter
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;

    private LocalDateTime created = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        if (id == null || request.id == null) {
            return false;
        }
        return id.equals(request.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
