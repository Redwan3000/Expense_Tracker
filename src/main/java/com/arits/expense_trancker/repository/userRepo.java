package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.SubuserListDto;
import com.arits.expense_trancker.entity.Role;
import com.arits.expense_trancker.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface userRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.role r " +
            "LEFT JOIN FETCH u.permissions p where u.username=:username")

    Optional<User> findByUsername(@Param("username") String username);

    @Query("select u from User u where lower(u.username)=lower(:keyword) or lower(u.firstName) =lower(:keyword) or lower(u.lastName)=lower(:keyword)  or lower(u.email)=lower(:keyword) ")
    List<User> getUserBySearch(@Param("keyword") String keyword);

    @Query("select u from User u where u.parent=:user")
    List<User> getUserByParentId(User user);


    List<User> findUsersByRole(Role role);

//    @Query(value = "SELECT * FROM users WHERE username = :username", nativeQuery = true)
//    Optional<User> findByUsernameIncludingDeleted(@Param("username") String username);
//
//    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
//    Optional<User> findByEmailIncludingDeleted(@Param("email") String email);

    @Query(value = "select * from users where is_deleted = true", nativeQuery = true)
    List<User> findAllDeletedUsers();


    @Query(value = "SELECT * FROM users WHERE user_id IN (" +
            "SELECT MIN(user_id) FROM users " +
            "WHERE parent_id = :parentId " +
            "GROUP BY role_id)", nativeQuery = true)
    List<User> findOneSubUserPerRole(@Param("parentId") Long parentId);



}
