package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Permission;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;


@Repository
public interface PermissionRepo extends JpaRepository<Permission, Long> {


    @Query(value = """
            select count(id)>0
            from permission 
            where name=:name
              and (is_deleted=true or is_deleted=false)
            """, nativeQuery = true)
    boolean existsByNameIncludingSofted(@Param("name") String name);


    @Query(value = """
            update permission 
            set is_deleted=false,
                deleted_at=null,
                updated_at=now()
            where name=:name 
            returning *
            """, nativeQuery = true)
    Optional<Permission> findByNameIncludingSoft(@Param("name") String name);


    @Modifying
    @Query(value = """
            update permission 
            set name=:name ,
                description=:description,
                updated_at= now()
            where id=:permissionId
            and is_deleted=false
            returning *
            """, nativeQuery = true)
    Permission modifyPermission(@Param("permissionId") Long permissionId, @Param("name") String name, @Param("description") String description);


    @Query(value = """
            update permission 
            set is_deleted=true,
                deleted_at=now(),
                updated_at=now()
            where id=:permissionId
            returning *
            """, nativeQuery = true)
    Permission deletePermission(@Param("permissionId") Long permissionId);

    @Query(value = """
            select count(id)>0 
                from permission
            where id_deleted= true  
                and id=:permissionId
            """, nativeQuery = true)
    boolean existsAfterSoftDelete(@Param("permissionId") Long permissionId);


    @Query(value = """
            select count(id)>0
            from permission 
            where id=:permissionId 
              and (is_deleted=true or is_deleted=false)
            """, nativeQuery = true)
    boolean permissionExistInBothWorld(@Param("permissionId") Long permissionId);


    @Modifying
    @Transactional
    @Query(value = """
            with 
                delete_users_permission as(
                   delete from users_permissions
                   where permission_id=:permissionId 
                ),
                delete_roles_permission as(
                    delete from roles_default_permissions
                    where permission_id=:permissionId
                )
            delete from permission 
                where id=:permissionId
            """, nativeQuery = true)
    int hardDeletePermission(@Param("permissionId") Long permissionId);

    @Modifying
    @Transactional
    @Query(value = """
            with 
                delete_users_permission as(
                   delete from users_permissions
                ),
                delete_roles_permission as(
                    delete from roles_default_permissions
                )
            delete from permission 
            """, nativeQuery = true)
    int hardDeleteAllPermissions();


    @Query(value = """
             select Distinct  p.* from permission p
                      left join roles_default_permissions rdp on p.id=rdp.permission_id
                      left join users_permissions up on p.id=up.permission_id
             where 
                  (p.is_deleted=:isDeleted)   
              and (:keyword is null 
                     or (lower(p.name)  like lower(concat('%', :keyword, '%')) 
                     or  (lower(p.description) like lower(concat('%', :keyword, '%')))
                         ))
             and (:permissionId is null or  p.id = :permissionId)
             and (:userId is null or  up.user_id = :userId)
             and (:roleId is null or  rdp.role_id = :roleId)
            
            limit :size offset :offset
            """, nativeQuery = true)
    List<Object[]> searchPermission(@Param("keyword") String keyword,
                                    @Param("userId")Long userId,
                                    @Param("roleId")Long roleId,
                                    @Param("permissionId")Long permissionId,
                                    @Param("isDeleted")boolean isDeleted,
                                    @Param("size") int size,
                                    @Param("offset")int offset);



    @Query(value = """
             select Distinct  count(p.id) from permission p
                      left join roles_default_permissions rdp on p.id=rdp.permission_id
                      left join users_permissions up on p.id=up.permission_id
             where 
                  (p.is_deleted=:isDeleted)   
              and (:keyword is null 
                     or (lower(p.name)  like lower(concat('%', :keyword, '%')) 
                     or  (lower(p.description) like lower(concat('%', :keyword, '%')))
                         ))
             and (:permissionId is null or  p.id = :permissionId)
             and (:userId is null or  up.user_id = :userId)
             and (:roleId is null or  rdp.role_id = :roleId)
            
            """, nativeQuery = true)
    Long countsearchedPermission(String keyword, Long userId, Long roleId, Long permissionId, boolean isDeleted);
}
