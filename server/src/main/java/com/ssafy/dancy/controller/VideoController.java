package com.ssafy.dancy.controller;

import com.ssafy.dancy.entity.User;
import com.ssafy.dancy.message.request.video.ConvertVideoRequest;
import com.ssafy.dancy.message.request.video.PracticeVideoUploadRequest;
import com.ssafy.dancy.message.request.video.ReferenceVideoUploadRequest;
import com.ssafy.dancy.message.response.video.ConvertVideoResponse;
import com.ssafy.dancy.message.response.video.UploadVideoResponse;
import com.ssafy.dancy.service.video.CreateVideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/video")
public class VideoController {

    private final CreateVideoService videoService;
    private final WebClient webClient;

    @PostMapping(value = "/upload/reference", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadVideoResponse uploadReferenceVideo(@AuthenticationPrincipal User user,
                                                    @Valid @ModelAttribute ReferenceVideoUploadRequest request){

        return videoService.uploadReferenceVideo(user, request.videoFile());
    }

    @PostMapping(value = "/upload/practice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadVideoResponse uploadDanceVideo(@AuthenticationPrincipal User user,
                                                @Valid @ModelAttribute PracticeVideoUploadRequest request){

        return videoService.uploadPracticeVideo(user, request.videoFile(), request.referenceVideoId());
    }

    @PostMapping("/analyze")
    public ConvertVideoResponse convertTwoVideo(@Valid @RequestBody ConvertVideoRequest request){

        return videoService.requestConvertToFlask(request);
    }

    @GetMapping("/status")


//    @GetMapping("/check")
//    public List<>

    @PostMapping("")
    public String requestAccuracyData(@RequestBody String accuracyData){

        try{
            String apiUrl = "http://i10d210.p.ssafy.io:5000/sendData";

            return webClient.post()
                    .uri(apiUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("Spring에서 저장한 url")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }
        catch(Exception e){
            return "ERROR";
        }

    }

}