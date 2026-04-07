package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {


    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.role r " +
            "LEFT JOIN FETCH u.usersPermissions p where u.username=:username")
    Optional<User> findByUsername(@Param("username") String username);


    @Query(value = "select u.id as userId, " +
            "u.username as username," +
            "u.first_name as firstName, " +
            "u.last_name as lastName, " +
            "u.email as email, " +
            "u.phone as phone, " +
            "u.dob as dob," +
            "u.parent_id as parentId," +
            "g.name as gender, " +
            "r.name as role  " +
            " from users u " +
            "left join gender g on u.gender_id= g.id " +
            "left join role r on u.role_id= r.id " +
            "where lower(u.username)=lower(:keyword) or lower(u.first_name) =lower(:keyword) or lower(u.last_name)=lower(:keyword)  or lower(u.email)=lower(:keyword) or lower(r.name)=lower(:keyword)", nativeQuery = true)
    Optional<List<UserDetailResponseDto>> getUserByKeyword(@Param("keyword") String keyword);



    @Query(value = "select username , concat(first_name,' ',last_name )as name from users where parent_id=:id", nativeQuery = true)
    List<SubuserListDto> getSubusersListByParent(@Param("id") Long id);


    @Query(value = "select  u.id " +
            "from users u where u.role_id=:roleId", nativeQuery = true)
    List<Long> findUsersIdByRoleId(@Param("roleId") Long roleId);


    @Query(value = "select  * " +
            "from users u " +
            "where u.parent_id=:id and u.id=:subUserId ", nativeQuery = true)
    Optional<User> findByParentIdAndUserId(@Param("id") Long id, @Param("subUserId") Long subUserId);


    @Query(value = "select u.id as userId, " +
            "u.username as username," +
            "u.first_name as firstName, " +
            "u.last_name as lastName, " +
            "u.email as email, " +
            "u.phone as phone, " +
            "u.dob as dob," +
            "u.parent_id as parentId," +
            "g.name as gender, " +
            "r.name as role  " +
            " from users u " +
            "left join gender g on u.gender_id= g.id " +
            "left join role r on u.role_id= r.id " +
            "where u.parent_id is null ", nativeQuery = true)
    List<UserDetailResponseDto> getAllOwner();


    @Query(value = "select u.id as userId, " +
            "u.username as username," +
            "u.first_name as firstName, " +
            "u.last_name as lastName, " +
            "u.email as email, " +
            "u.phone as phone, " +
            "u.dob as dob," +
            "u.parent_id as parentId," +
            "g.name as gender, " +
            "r.name as role  " +
            " from users u " +
            "left join gender g on u.gender_id= g.id " +
            "left join role r on u.role_id= r.id " +
            "where u.parent_id = :userId ", nativeQuery = true)
    List<UserDetailResponseDto> getALlSubuserByOwnerId(@Param("userId") Long userId);


    @Query(value = "select u.id as userId, " +
            "u.username as userName, " +
            "u.deleted_at as deletedAt " +
            "from users u " +
            "where is_deleted is true ", nativeQuery = true)
    List<DeletedUsersListDto> findAllSoftDeletedUsers();

    @Query(value = "update users" +
            " set is_deleted = false ," +
            "     deleted_at = null " +
            "where id=:id and is_deleted is true " +
            "returning id as userId , username", nativeQuery = true)
    Optional<RetriveUserResponseDto> reviveUserById(@Param("id") long id);


    @Query(value = """
            select sum(b.balance)+sum(m.balance)+sum(c.balance) as totalBalance
            from bank b 
            join mobile_banking m on b.user_id= m.user_id 
             join cash_wallet c on m.user_id = c.user_id
            where b.user_id=:id
            """, nativeQuery = true)
    BigDecimal getAllAccountBanance(@Param("id") Long id);
}
