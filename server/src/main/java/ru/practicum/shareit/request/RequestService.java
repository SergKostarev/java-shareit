package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestService {

    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final Sort sort = Sort.by("created").descending();

    @Transactional
    public RequestDto create(Long userId, CreateRequestDto requestDto) {
        log.debug("Создание нового запроса.");
        User user = userService.getUserById(userId);
        Request request = RequestMapper.toRequest(requestDto, user);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    public Collection<RequestItemDto> getRequests(Long userId) {
        log.debug("Получение списка запросов пользователя с идентификатором " + userId);
        userService.getUserById(userId);
        List<Request> requests = requestRepository.findByRequestorId(userId, sort);
        Set<Long> userRequestsIds = requests
                .stream()
                .map(Request::getId)
                .collect(Collectors.toSet());
        Map<Long, List<Item>> items = itemRepository
                .findByRequestIdIn(userRequestsIds)
                .stream()
                .collect(Collectors.groupingBy(i -> i.getRequest().getId()));
        return requests
                .stream()
                .map(r -> RequestMapper.toRequestItemDto(r, items.getOrDefault(r.getId(), List.of())))
                .toList();
    }

    public Collection<RequestDto> getAllRequests(Long userId) {
        log.debug("Получение списка запросов других пользователей.");
        userService.getUserById(userId);
        return requestRepository
                .findByRequestorIdNot(userId, sort)
                .stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    public RequestItemDto getRequest(Long userId, Long requestId) {
        log.debug("Получение данных об одном запросе.");
        userService.getUserById(userId);
        Request request = getRequestById(requestId);
        List<Item> items = itemRepository.findByRequestId(requestId);
        return RequestMapper.toRequestItemDto(request, items);
    }

    public Request getRequestById(Long requestId) {
        return requestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден.", requestId));
    }
}
