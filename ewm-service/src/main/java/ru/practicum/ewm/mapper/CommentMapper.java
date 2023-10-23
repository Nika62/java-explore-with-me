package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.model.Comment;

import static ru.practicum.ewm.mapper.DateTimeMapper.convertToString;

@Component
public class CommentMapper {

    public CommentDto convertCommentToCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setUserName(comment.getUser().getName());
        commentDto.setText(comment.getText());
        commentDto.setCreatedOn(convertToString(comment.getCreatedOn()));
        return commentDto;
    }
}
