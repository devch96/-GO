package com.hansung.capstone.user;

import com.hansung.capstone.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRidingServiceImpl implements UserRidingService{

    private final UserRepository userRepository;

    private final UserRidingRepository userRidingRepository;

    private final RedisService redisService;

    @Override
    public void record(UserRidingDTO.RecordDTO req) {
        User user = this.userRepository.findById(req.getUserId()).orElseThrow(
                () -> new RuntimeException("유저가 존재하지 않습니다.")
        );
        UserRiding userRiding = UserRiding.builder()
                .ridingTime(req.getRidingTime())
                .ridingDistance(req.getRidingDistance())
                .calorie(req.getCalorie())
                .user(user).build();

        this.userRidingRepository.save(userRiding);
    }

    @Override
    public List<UserRidingDTO.HistoryResponseDTO> getHistory(Long userId, Long period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowMinusPeriod = now.minusDays(period).with(LocalTime.of(0, 0, 0, 0));
        List<Object[]> db = this.userRidingRepository.findAllByPeriod(userId, now, nowMinusPeriod);
        List<UserRidingDTO.HistoryResponseDTO> res = new ArrayList<>();
        for(Object[] inDB : db){
            BigDecimal decimalValue = new BigDecimal(inDB[3].toString());
            String dateValue = inDB[0].toString();
            String temp = String.valueOf(inDB[1]);
            String[] parts = temp.split("\\.");
            UserRidingDTO.HistoryResponseDTO dto = UserRidingDTO.HistoryResponseDTO.builder()
                    .createdDate(dateValue)
                    .ridingTime(decimalValue.longValue())
                    .ridingDistance(Float.parseFloat(inDB[2].toString()))
                    .calorie(Integer.parseInt(parts[0])).build();
            res.add(dto);
        }
        return res;
    }

    @Override
    public List<UserRidingDTO.RankResponseDTO> getRank() {
        List<UserRidingDTO.RankResponseDTO> rankerList = new ArrayList<>();
        Long profileImageId;
        for(int i = 1; i<=3 ; i++){
            if(this.redisService.getValues("Rank_No"+String.valueOf(i)) != null) {
                String value = this.redisService.getValues("Rank_No" + String.valueOf(i));
                String[] parts = value.split(":");
                User user = this.userRepository.findById(Long.parseLong(parts[0])).orElseThrow(
                        () -> new RuntimeException("유저가 존재하지 않습니다.")
                );
                if (user.getProfileImage() == null){
                    profileImageId = -1L;
                } else{
                    profileImageId = user.getProfileImage().getId();
                }
                UserRidingDTO.RankResponseDTO ranker = UserRidingDTO.RankResponseDTO.builder()
                        .userNickname(user.getNickname())
                        .profileImageId(profileImageId)
                        .distanceRank(i)
                        .totalDistance(Float.parseFloat(parts[1])).build();
                rankerList.add(ranker);
            }
        }
        return rankerList;
    }

    @Scheduled(cron = "0 50 14 * * *")
    private void rank(){
        List<Object[]> ranker = this.userRidingRepository.getRank();
        for(Object[] row : ranker){
            String key = "Rank_No"+String.valueOf(row[2]);
            String value = row[0].toString() + ":" + row[1].toString();
            this.redisService.setValues(key, value);
        }
    }


}
