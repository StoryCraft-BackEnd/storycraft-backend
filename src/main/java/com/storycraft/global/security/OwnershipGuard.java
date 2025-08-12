package com.storycraft.global.security;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class OwnershipGuard {

    private final ChildProfileRepository childProfileRepository;

    public ChildProfile getOwnedChildOrThrow(Long childId, Long userId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 자녀입니다."));
        if (!child.getUser().getId().equals(userId)) {
            try {
                throw new AccessDeniedException("귀하의 자녀 프로필이 아닙니다.");
            } catch (AccessDeniedException e) {
                throw new RuntimeException(e);
            }
        }
        return child;
    }


}
