package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    Collection<Item> findByOwnerId(Long ownerId);

    Collection<Item> findByRequestIdIn(Set<Long> requestId);

    List<Item> findByRequestId(Long requestId);
}
