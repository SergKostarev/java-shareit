package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private User requestor;

    private LocalDate created = LocalDateTime.now().toLocalDate();
}
