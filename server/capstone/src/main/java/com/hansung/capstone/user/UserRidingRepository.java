package com.hansung.capstone.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRidingRepository extends JpaRepository<UserRiding, Long> {

    @Query(
            value = "SELECT DATE(created_date) AS date, SUM(calorie) AS total_calorie, SUM(riding_distance) AS total_distance, SUM(riding_time) AS total_time\n" +
                    "FROM user_riding\n" +
                    "WHERE user_id = :userId and created_date <= :now and created_date >= :period\n"+
                    "GROUP BY user_id, DATE(created_date)",
            nativeQuery = true
    )
    List<Object[]> findAllByPeriod(@Param("userId") Long userId, @Param("now") LocalDateTime now, @Param("period") LocalDateTime period);

    @Query(
            value = "SELECT user_id, ROUND(SUM(riding_distance), 2) AS total_distance, RANK() OVER (ORDER BY SUM(riding_distance) DESC) AS distance_rank\n" +
                    "FROM user_riding\n" +
                    "GROUP BY user_id\n" +
                    "LIMIT 3",
            nativeQuery = true
    )
    List<Object[]> getRank();
}
