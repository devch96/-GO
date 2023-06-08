package com.hansung.capstone.user;

import java.util.List;

public interface UserRidingService {

    void record(UserRidingDTO.RecordDTO req);

    List<UserRidingDTO.HistoryResponseDTO> getHistory(Long userId, Long period);

    List<UserRidingDTO.RankResponseDTO> getRank();
}
