package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
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
}
