package com.arits.expense_trancker.handler;

import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Resolver {


private final UserRepo userRepo;
    private final EntityProvider entityProvider;


    public User getTargetUser(User user, Long userId, Long subuserId) {
        User targetUser;
        boolean isHeAdmin = user != null && user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // FIX: Only attempt to get user.getId() if user is not null
        Long parentId = (user == null) ? null : (isHeAdmin ? userId : user.getId());

        if (subuserId != null) {
            targetUser = userRepo.findByParentIdAndUserId(parentId, subuserId)
                    .orElseThrow(() -> new RuntimeException("Subuser does not exist"));
        } else {
            targetUser = isHeAdmin ? userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User does not exist")) : user;
        }

        return targetUser;
    }

    public Long getTargetUserId(User user, Long userId, Long subuserId) {
        Long targetUserId;

        // FIX: Added 'user != null' safeguard here as well
        boolean isHeAdmin = user != null && user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // FIX: Only attempt to get user.getId() if user is not null
        Long parentId = (user == null) ? null : (isHeAdmin ? userId : user.getId());

        if (subuserId != null) {
            targetUserId = userRepo.findUserIdByParentIdAndUserId(parentId, subuserId)
                    .orElseThrow(() -> new RuntimeException("subuser does not exist"));

        } else {
            // FIX: Ensure we don't call user.getId() if user is null
            targetUserId = isHeAdmin ? userId : (user != null ? user.getId() : null);
        }

        return targetUserId;
    }
}
