package com.hansung.capstone.community;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findAllByPostId(Long postId);

    void deleteById(Long id);
}

