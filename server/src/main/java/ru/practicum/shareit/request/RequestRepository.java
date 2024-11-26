package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequestorIdNot(Long requestorId, Sort sort);

    List<Request> findByRequestorId(Long requestorId, Sort sort);

}
