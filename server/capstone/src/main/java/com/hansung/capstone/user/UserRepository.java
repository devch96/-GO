package com.hansung.capstone.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<UserEmailInterface> findByUsernameAndBirthday(String username, String birthday);

    Optional<User> findByNickname(String nickname);

    Optional<User> findById(Long id);
}
