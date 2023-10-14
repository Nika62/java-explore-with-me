package ru.practicum.ewm.dto.request;

import lombok.Data;

@Data
public class RequestDto {
    private long id;

    private String created;

    private long event;

    private long requester;

    private String status;
}
