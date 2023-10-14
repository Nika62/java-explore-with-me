package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.model.exception.Request;

import static ru.practicum.ewm.mapper.DateTimeMapper.convertToString;

@Component
public class RequestMapper {

    public RequestDto convertRequestToRequestDto(Request request) {
        if (request == null) {
            return null;
        }
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setCreated(convertToString(request.getCreated()));
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setStatus(request.getStatus());

        return requestDto;
    }
}
