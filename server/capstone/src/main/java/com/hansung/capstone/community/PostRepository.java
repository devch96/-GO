package com.hansung.capstone.community;

import com.hansung.capstone.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findAllByPostCategory(Pageable pageable, PostCategory postCategory);

    Page<Post> findAllByAuthor(User user, Pageable pageable);

    @Query(
            value = "SELECT p FROM Post p Where p.title LIKE %:titleOrContent% OR p.content LIKE %:titleOrContent%"
    )
    Page<Post> findAllSearch(@Param("titleOrContent")String titleOrContent, Pageable pageable);

    @Query(
            value = "SELECT * FROM post WHERE post_id IN (SELECT post_post_id FROM post_scraper WHERE scraper_user_id = :userId)",
            nativeQuery = true
    )
    Page<Post> findAllScrap(@Param("userId")Long userId, Pageable pageable);

    void deleteById(Long id);
}
