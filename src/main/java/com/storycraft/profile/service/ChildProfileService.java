package com.storycraft.profile.service;

import com.storycraft.global.exception.CustomException;
import com.storycraft.global.exception.ErrorCode;
import com.storycraft.profile.dto.ChildProfileCreateRequestDto;
import com.storycraft.profile.dto.ChildProfileIdResponseDto;
import com.storycraft.profile.dto.ChildProfileResponseDto;
import com.storycraft.profile.dto.ChildProfileUpdateRequestDto;
import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.user.entity.User;
import com.storycraft.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChildProfileService {

    private final ChildProfileRepository childProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChildProfileIdResponseDto createChildProfile(String email, ChildProfileCreateRequestDto request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChildProfile.LearningLevel level;
        try {
            level = ChildProfile.LearningLevel.valueOf(request.getLearningLevel());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_LEARNING_LEVEL);
        }

        ChildProfile child = ChildProfile.builder()
                .name(request.getName())
                .age(request.getAge())
                .learningLevel(level)
                .user(user)
                .build();

        childProfileRepository.save(child);
        return new ChildProfileIdResponseDto(child.getId());
    }

    @Transactional(readOnly = true)
    public List<ChildProfileResponseDto> getChildProfiles(String email) {
        List<ChildProfile> children = childProfileRepository.findByUserEmail(email);
        return children.stream()
                .map(child -> new ChildProfileResponseDto(
                        child.getId(),
                        child.getName(),
                        child.getAge(),
                        child.getLearningLevel().name(),
                        child.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChildProfileResponseDto getChildProfile(String email, Long childId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_PROFILE_NOT_FOUND));

        if (!child.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.CHILD_PROFILE_ACCESS_DENIED);
        }

        return new ChildProfileResponseDto(
                child.getId(),
                child.getName(),
                child.getAge(),
                child.getLearningLevel().name(),
                child.getCreatedAt()
        );
    }

    @Transactional
    public ChildProfileIdResponseDto updateChildProfile(String email, Long childId, ChildProfileUpdateRequestDto request) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_PROFILE_NOT_FOUND));

        if (!child.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.CHILD_PROFILE_ACCESS_DENIED);
        }

        ChildProfile.LearningLevel level;
        try {
            level = ChildProfile.LearningLevel.valueOf(request.getLearningLevel());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_LEARNING_LEVEL);
        }

        child.setName(request.getName());
        child.setAge(request.getAge());
        child.setLearningLevel(level);

        childProfileRepository.save(child);

        return new ChildProfileIdResponseDto(child.getId());
    }

    @Transactional
    public ChildProfileIdResponseDto deleteChildProfile(String email, Long childId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_PROFILE_NOT_FOUND));

        if (!child.getUser().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.CHILD_PROFILE_ACCESS_DENIED);
        }

        childProfileRepository.delete(child);
        return new ChildProfileIdResponseDto(childId);
    }
}
