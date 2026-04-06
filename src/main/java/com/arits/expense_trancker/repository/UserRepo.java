package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.Role;
import com.arits.expense_trancker.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {


    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.role r " +
            "LEFT JOIN FETCH u.usersPermissions p where u.username=:username")
    Optional<User> findByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "update users set is_deleted = true, deleted_at = NOW() where id = :id", nativeQuery = true)
    void softDeleteById(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "update users set is_deleted = true, deleted_at = NOW() where parent_id = :parentId", nativeQuery = true)
    void softDeleteSubUsers(@Param("parentId") Long parentId);

    @Modifying
    @Transactional
    @Query(value = "update users set is_deleted = true, deleted_at = NOW() where id = :user_id", nativeQuery = true)
    void softDeleteSubUsersId(@Param("user_id") Long user_id);


    @Query("select u from User u where lower(u.username)=lower(:keyword) or lower(u.firstName) =lower(:keyword) or lower(u.lastName)=lower(:keyword)  or lower(u.email)=lower(:keyword) ")
    List<User> getUserBySearch(@Param("keyword") String keyword);

    @Query(value = "select u.username, u.first_name, u.last_name, r.role_name from users u inner join role r On u.role_id= r.role_id where u.parent_id=:userId", nativeQuery = true)
    List<User> getUserByParentId(@Param("userId") Long userId);


    @Query(value = "select * from users where is_deleted = true", nativeQuery = true)
    List<User> findAllDeletedUsers();


    @Query(value = "SELECT * FROM users WHERE id IN (" +
            "SELECT MIN(id) FROM users " +
            "WHERE parent_id = :parentId " +
            "GROUP BY role_id)", nativeQuery = true)
    List<User> findOneSubUserPerRole(@Param("parentId") Long parentId);

    @Query(value = "select * from users where is_deleted= true and id=:user_id", nativeQuery = true)
    Optional<User> findUserFromSoftDelete(@Param("user_id") Long user_id);

    @Query(value = "SELECT * FROM users WHERE is_deleted=true and parent_id = :parentId ", nativeQuery = true)
    List<User> findSubUserByParentId(@Param("parentId") Long parentId);


    @Query(value = "select u.user_id as userId, m.current_balance as mobileBankingBalance,b.current_balance as bankBalance, c.current_balance as cashWalletBalance , m.current_balance+b.current_balance+c.current_balance as totalBalance" +
            " from users u " +
            "left join mobile_banking m  on  u.user_id= m.user_id " +
            "left join bank b on m.user_id = b.user_id " +
            "left join cash_wallet c on b.user_id = c.user_id " +
            "Where u.user_id=:id"
            , nativeQuery = true)
    OverallBalanceDto fetchAccountsBalance(@Param("id") long id);

    @Query(value = "select username , concat(first_name,' ',last_name )as name from users where parent_id=:id", nativeQuery = true)
    List<SubuserListDto> getSubusersListByParent(@Param("id") Long id);

    @Query(value = "select " +
            "u.id as id," +
            "u.username as username," +
            " concat(u.first_name,' ',u.last_name)as name," +
            " r.name as role ," +
            " g.name as gender," +
            "u.phone as phone," +
            "u.dob as dateOfBirth" +
            " from users u " +
            "inner join role  r on u.role_id=r.id" +
            "  inner join gender as g on u.gender_id=g.id " +
            "where" +
            " u.id=:id", nativeQuery = true)
    GetUserInfoDto getUserInfo(@Param("id") long id);


    @Query(value = "select " +
            "u.id as userId, " +
            "concat(u.first_name, ' ', u.last_name) as userName, " +
            "b.bank_name as bankName, " +
            "a.name as accountType " +
            "FROM users u " +
            "inner join bank b on u.id=b.user_id " +
            "inner join account_Type a on b.account_type_id=a.id " +
            "where u.id=:userId ", nativeQuery = true)
    List<Object[]> getUserBankDetails(@Param("userId") Long userId);


    @Query(value = "select  u.id " +
            "from users u where u.role_id=:roleId", nativeQuery = true)
    List<Long> findUsersIdByRoleId(@Param("roleId") Long roleId);

    @Query(value = "select  u.id " +
            "from users u where u.role_id=:roleId and u.parent_id=:parentId", nativeQuery = true)
    List<Long> findSubUserByParentIdAndRoleId(@Param("parentId") Long parentId, @Param("roleId") Long roleId);
}
