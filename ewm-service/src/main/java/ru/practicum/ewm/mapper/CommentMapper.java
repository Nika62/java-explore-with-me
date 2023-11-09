package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.comment.CommentDto;
import ru.practicum.ewm.dto.comment.CommentFullDto;
import ru.practicum.ewm.dto.comment.CommentUserDto;
import ru.practicum.ewm.model.Comment;

import static ru.practicum.ewm.mapper.DateTimeMapper.convertToString;

@Component
@RequiredArgsConstructor
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

    public CommentUserDto convertCommentToCommentUserDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentUserDto commentUserDto = new CommentUserDto();
        commentUserDto.setId(comment.getId());
        commentUserDto.setText(comment.getText());
        commentUserDto.setCreatedOn(convertToString(comment.getCreatedOn()));
        return commentUserDto;
    }

    public CommentFullDto convertCommentToCommentFullDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentFullDto commentFullDto = new CommentFullDto();
        commentFullDto.setId(comment.getId());
        commentFullDto.setUserName(comment.getUser().getName());
        commentFullDto.setText(comment.getText());
        commentFullDto.setCreatedOn(convertToString(comment.getCreatedOn()));
        return commentFullDto;
    }
}
