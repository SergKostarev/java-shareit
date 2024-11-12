package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToOne
    @JoinColumn(name = "author_id")
    private User author;

    private LocalDate created = LocalDate.now();
}
