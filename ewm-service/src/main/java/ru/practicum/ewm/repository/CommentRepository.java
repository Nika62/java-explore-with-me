package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> getCommentsByUserId(long id, PageRequest pageRequest);

    List<Comment> getCommentsByUserIdAndEventId(long userId, long eventId);
}
