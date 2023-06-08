package com.hansung.capstone;

import com.hansung.capstone.response.CommonResponse;
import com.hansung.capstone.response.ResponseService;
import com.hansung.capstone.response.SingleResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ErrorHandler {

    private final ResponseService responseService;

    public ErrorHandler(ResponseService responseService) {
        this.responseService = responseService;
    }

    public ResponseEntity<CommonResponse> bindingResultErrorCode(BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            List<FieldError> errors = bindingResult.getFieldErrors();
            List<String> errorMessages = new ArrayList<>();
            for (FieldError error : errors ) {
                errorMessages.add(error.getField() + " - " + error.getDefaultMessage());
            }
            return new ResponseEntity<>(this.responseService.getFailureSingleResponse(errorMessages), HttpStatus.BAD_REQUEST);
        }
        else{
            return null;
        }
    }
}
