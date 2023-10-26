package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> getCommentsByUserIdOrderById(long id, PageRequest pageRequest);

    List<Comment> getCommentsByUserIdAndEventIdOrderById(long userId, long eventId);

    Page<Comment> getCommentsByEventIdOrderById(long eventId, PageRequest pageRequest);

    Page<Comment> findAllByOrderByIdAsc(PageRequest pageRequest);
}
