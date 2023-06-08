package com.hansung.capstone.user;

import com.hansung.capstone.response.CommonResponse;
import com.hansung.capstone.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.image.ColorModel;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/riding")
public class UserRidingController {

    private final ResponseService responseService;

    private final UserRidingService userRidingService;
    @PostMapping("/record")
    public ResponseEntity<CommonResponse> ridingRecord(@RequestBody UserRidingDTO.RecordDTO req){
        try{
            this.userRidingService.record(req);
            return new ResponseEntity<>(this.responseService.getSuccessCommonResponse(), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(this.responseService.getFailureCommonResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/record-history")
    public ResponseEntity<CommonResponse> getRidingHistory(
            @RequestParam Long userId,
            @RequestParam Long period
    ){
        return new ResponseEntity<>(this.responseService.getListResponse(this.userRidingService.getHistory(userId, period)), HttpStatus.OK);
    }

    @GetMapping("/rank")
    public ResponseEntity<CommonResponse> getRank(){
        return new ResponseEntity<>(this.responseService.getListResponse(this.userRidingService.getRank()), HttpStatus.OK);
    }
}
