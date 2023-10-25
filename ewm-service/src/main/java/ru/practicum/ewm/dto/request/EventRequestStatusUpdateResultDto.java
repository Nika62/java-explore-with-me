package ru.practicum.ewm.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateResultDto {

    List<RequestDto> confirmedRequests;

    List<RequestDto> rejectedRequests;
}
