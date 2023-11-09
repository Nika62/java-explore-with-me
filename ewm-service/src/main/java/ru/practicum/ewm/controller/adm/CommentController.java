package ru.practicum.ewm.controller.adm;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.comment.CommentFullDto;
import ru.practicum.ewm.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@Validated
@RestController
@RequestMapping("admin/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteCommentByIdByAdmin(@PathVariable long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }

    @GetMapping("/{commentId}")
    public CommentFullDto getCommentById(@PathVariable long commentId) {
        return commentService.getCommentById(commentId);
    }

    @GetMapping()
    List<CommentFullDto> getComments(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                     @RequestParam(defaultValue = "10") @Positive int size) {

        return commentService.getCommentsByAdmin(from, size);
    }

}
