package de.damien.funnyplaces.comments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Set<Comment> findByPlaceId(Long id);

}
