package com.ssafy.dancy.controller;

import com.ssafy.dancy.config.security.JwtTokenProvider;
import com.ssafy.dancy.entity.User;
import com.ssafy.dancy.message.request.auth.ChangePasswordRequest;
import com.ssafy.dancy.message.request.auth.LoginUserRequest;
import com.ssafy.dancy.message.response.auth.JwtTokenResponse;
import com.ssafy.dancy.service.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public JwtTokenResponse login(@Valid @RequestBody LoginUserRequest request, HttpServletResponse response){
        User user = userService.login(request);
        jwtTokenProvider.setRefreshTokenForClient(response, user);

        return jwtTokenProvider.makeJwtTokenResponse(user);
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal User user, HttpServletResponse response){
        if(user != null){
            userService.logout(user);
        }
        jwtTokenProvider.removeRefreshTokenForClient(response);
    }

    @PutMapping("/change")
    public void changeUserPassword(@AuthenticationPrincipal User user,
                                   @Valid @RequestBody ChangePasswordRequest request,
                                   HttpServletResponse response){

        userService.changePassword(user, request);
        jwtTokenProvider.removeRefreshTokenForClient(response);
    }
}
