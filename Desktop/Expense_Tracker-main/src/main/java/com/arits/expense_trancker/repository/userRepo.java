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


    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.role r " +
            "LEFT JOIN FETCH u.usersPermissions p where u.username=:username")
    Optional<User> findByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "update users set is_deleted = true, deleted_at = NOW() where user_id = :id", nativeQuery = true)
    void softDeleteById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "update users set is_deleted = true, deleted_at = NOW() where parent_id = :parentId", nativeQuery = true)
    void softDeleteSubUsers(@Param("parentId") Long parentId);

    @Modifying
    @Transactional
    @Query(value = "update users set is_deleted = true, deleted_at = NOW() where user_id = :user_id", nativeQuery = true)
    void softDeleteSubUsersId(@Param("user_id") Long user_id);

    @Query("select u from User u where lower(u.username)=lower(:keyword) or lower(u.firstName) =lower(:keyword) or lower(u.lastName)=lower(:keyword)  or lower(u.email)=lower(:keyword) ")
    List<User> getUserBySearch(@Param("keyword") String keyword);

    @Query("select u from User u where u.parent=:user")
    List<User> getUserByParentId(User user);


    List<User> findUsersByRole(Role role);

    @Query(value = "select * from users where is_deleted = true", nativeQuery = true)
    List<User> findAllDeletedUsers();


    @Query(value = "SELECT * FROM users WHERE user_id IN (" +
            "SELECT MIN(user_id) FROM users " +
            "WHERE parent_id = :parentId " +
            "GROUP BY role_id)", nativeQuery = true)
    List<User> findOneSubUserPerRole(@Param("parentId") Long parentId);


}
