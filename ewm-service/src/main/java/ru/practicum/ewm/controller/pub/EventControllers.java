package ru.practicum.ewm.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.CommentFullDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.model.enums.EventSortParameter;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventControllers {

    private final EventService eventService;

    public final CommentService commentService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) Optional<String> text,
                                        @RequestParam(required = false) Optional<Long[]> categories,
                                        @RequestParam(required = false) Optional<Boolean> paid,
                                        @RequestParam(required = false) Optional<String> rangeStart,
                                        @RequestParam(required = false) Optional<String> rangeEnd,
                                        @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                        @RequestParam(required = false) Optional<EventSortParameter> sort,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                        @RequestParam(defaultValue = "10") @Positive int size,
                                        HttpServletRequest request) {

        return eventService.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable long id, HttpServletRequest request) {
        return eventService.getEventById(id, request);
    }

    @GetMapping("{eventId}/comments")
    public List<CommentDto> getCommentsEvent(@PathVariable long eventId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        return commentService.getCommentsEvent(eventId, from, size);
    }

    @GetMapping("{eventId}/comments/{commentId}")
    public CommentFullDto getCommentByIdAndByEventId(@PathVariable long eventId, @PathVariable long commentId) {
        return commentService.getCommentByIdAndByEventId(eventId, commentId);
    }
}
