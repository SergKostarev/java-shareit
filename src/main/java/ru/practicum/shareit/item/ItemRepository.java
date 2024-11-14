package ru.practicum.shareit.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {

    Collection<Item> findByOwnerId(Long ownerId);

    default Iterable<Item> searchByAvailableAndNameOrDescription (String text) {
        BooleanExpression byName = QItem.item.name.containsIgnoreCase(text);
        BooleanExpression byDescription = QItem.item.description.containsIgnoreCase(text);
        BooleanExpression byAvailable = QItem.item.available.isTrue();
        return findAll(byAvailable.and(byName.or(byDescription)));
    }

    default Item getItemById(Long itemId) {
        return findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена.", itemId));
    }
}
