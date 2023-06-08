package com.hansung.capstone.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {

    @Query(
            value = "SELECT uc.*\n" +
                    "FROM user_course uc\n" +
                    "JOIN (\n" +
                    "    SELECT pv.post_post_id, COUNT(*) AS vote_count\n" +
                    "    FROM post_voter pv\n" +
                    "    GROUP BY pv.post_post_id\n" +
                    "    HAVING COUNT(*) >= 1\n" +
                    ") AS subquery\n" +
                    "ON uc.post_id = subquery.post_post_id\n" +
                    "WHERE uc.region = :region\n" +
                    "ORDER BY subquery.vote_count DESC",
            countQuery = "SELECT COUNT(*)\n" +
                    "FROM user_course uc\n" +
                    "JOIN (\n" +
                    "    SELECT pv.post_post_id, COUNT(*) AS vote_count\n" +
                    "    FROM post_voter pv\n" +
                    "    GROUP BY pv.post_post_id\n" +
                    "    HAVING COUNT(*) >= 1\n" +
                    ") AS subquery\n" +
                    "ON uc.post_id = subquery.post_post_id\n" +
                    "WHERE uc.region = :region",
            nativeQuery = true
    )
    Page<UserCourse> findAllByRegion(Pageable pageable, @Param("region") String region);

    @Query(
            value = "SELECT * FROM user_course where id in (select user_course_id from user_course_scraper where scraper_user_id = :userId)",
            nativeQuery = true
    )
    Page<UserCourse> findAllScrap(@Param("userId") Long userId, Pageable pageable);

    UserCourse findByPostId(Long postId);

}
