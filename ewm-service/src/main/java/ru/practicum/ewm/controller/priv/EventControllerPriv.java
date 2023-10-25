package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.CommentUserDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequestDto;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResultDto;
import ru.practicum.ewm.dto.request.RequestDto;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@RestController
@Validated
public class EventControllerPriv {

    private final EventService eventService;
    private final RequestService requestService;
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(CREATED)
    public EventFullDto createEvent(@PathVariable long userId, @RequestBody @Valid NewEventDto newEventDto) {

        return eventService.createEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventFullDto> getEventsByUserId(@PathVariable long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return eventService.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable long userId, @PathVariable long eventId) {
        return eventService.getEventByIdAndInitiator(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable long userId, @PathVariable long eventId, @RequestBody NewEventDto newEventDto) {
        return eventService.updateEventByUser(userId, eventId, newEventDto);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getUserEventRequests(@PathVariable long userId, @PathVariable long eventId) {
        return requestService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResultDto getUserEventRequests(@PathVariable long userId, @PathVariable long eventId, @RequestBody EventRequestStatusUpdateRequestDto body) {
        return requestService.reviewEventRequests(userId, eventId, body);
    }


    @PostMapping("/{eventId}/comments")
    @ResponseStatus(CREATED)
    public CommentDto addComment(@PathVariable long userId, @PathVariable long eventId,
                                 @RequestBody @NotBlank @Size(min = 10, max = 1000) String text) {
        return commentService.addComment(userId, eventId, text);
    }

    @GetMapping("/comments")
    public List<CommentUserDto> getCommentsUser(@PathVariable long userId, @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return commentService.getCommentsUser(userId, from, size);
    }

    @GetMapping("/{eventId}/comments")
    public List<CommentDto> getCommentsUserToEvent(@PathVariable long userId, @PathVariable long eventId) {
        return commentService.getCommentsUserToEvent(userId, eventId);
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    public void deleteCommentById(@PathVariable long userId, @PathVariable long eventId, @PathVariable long commentId) {
        commentService.deleteCommentById(userId, eventId, commentId);
    }

    @PatchMapping("/{eventId}/comments/{commentId}")
    public CommentUserDto updateComment(@PathVariable long userId, @PathVariable long eventId, @PathVariable long commentId,
                                        @RequestBody @NotBlank @Size(min = 10, max = 1000) String text) {
        return commentService.updateComment(userId, eventId, commentId, text);
    }

}
