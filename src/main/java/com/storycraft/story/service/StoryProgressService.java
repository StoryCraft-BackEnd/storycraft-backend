package com.storycraft.story.service;

import com.storycraft.profile.entity.ChildProfile;
import com.storycraft.profile.repository.ChildProfileRepository;
import com.storycraft.story.entity.Story;
import com.storycraft.story.entity.StoryProgress;
import com.storycraft.story.repository.StoryProgressRepository;
import com.storycraft.story.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoryProgressService {

    private final StoryRepository storyRepository;
    private final StoryProgressRepository storyProgressRepository;
    private final ChildProfileRepository childProfileRepository;

    //동화 읽음표시 & 읽은 시간 저장
    @Transactional
    public void markAsRead(Long childId, Long storyId) {
        ChildProfile child = getChildOrThrow(childId);
        Story story = getStoryOrThrow(storyId);

        StoryProgress storyProgress = storyProgressRepository.findByChildAndStory(child, story)
                .orElse(StoryProgress.builder()
                        .child(child)
                        .story(story)
                        .build());
        storyProgress.setRead(true);
        storyProgress.setReadAt(LocalDateTime.now());

        storyProgressRepository.save(storyProgress);
    }

    //학습 시간 업데이트 메소드
    @Transactional
    public void updateLearnedTime(Long childId, Long storyId, int learnedTimeInSecond) {
        ChildProfile child = getChildOrThrow(childId);
        Story story = getStoryOrThrow(storyId);

        StoryProgress storyProgress = storyProgressRepository.findByChildAndStory(child, story)
                .orElse(StoryProgress.builder()
                        .child(child)
                        .story(story)
                        .build());

        int totalSeconds = (storyProgress.getLearnedMinutes() * 60 + storyProgress.getLearnedSeconds()) + learnedTimeInSecond;
        storyProgress.setLearnedMinutes(totalSeconds / 60);
        storyProgress.setLearnedSeconds(totalSeconds % 60);

        storyProgressRepository.save(storyProgress);
    }

    public Optional<StoryProgress> findByStoryIdAndChildId(Long storyId, Long childId) {
        return storyProgressRepository.findByStory_IdAndChild_Id(storyId, childId);
    }

    //자녀 정보 가져오는 메소드
    private ChildProfile getChildOrThrow(Long childId) {
        return childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보를 찾을 수 없습니다."));
    }

    private Story getStoryOrThrow(Long storyId) {
        return storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("동화 정보를 찾을 수 없습니다."));
    }
}
