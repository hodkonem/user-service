package ru.itwizardry.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.itwizardry.userservice.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findAllByAge(int age);

    @Modifying
    @Query("delete from User u where u.id = :id")
    int deleteByIdReturningCount(@Param("id") long id);
}
