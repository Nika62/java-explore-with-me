package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.request.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(long userId, long eventId);

    List<RequestDto> getRequests(long userId);

    RequestDto cancelRequest(long userId, long requestId);
}
