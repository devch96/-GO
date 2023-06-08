package com.hansung.capstone.course;

import com.hansung.capstone.response.CommonResponse;
import com.hansung.capstone.response.ResponseService;
import com.hansung.capstone.response.SingleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/app-course")
@RequiredArgsConstructor
public class AppCourseController {

    private final AppCourseService appCourseService;

    private final ResponseService responseService;

    @PostMapping("/create")
    public ResponseEntity<CommonResponse> createAppCourse(@RequestBody AppCourseDTO.createDTO req){
        try{
            this.appCourseService.createAppCourse(req);
            return new ResponseEntity<>(this.responseService.getSuccessCommonResponse(), HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(this.responseService.getFailureCommonResponse(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<CommonResponse> getAppCourseList(){
        try{
            return new ResponseEntity<>(this.responseService.getListResponse(this.appCourseService.getAppCourseList()), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(this.responseService.getFailureCommonResponse(), HttpStatus.BAD_REQUEST);
        }
    }
}
