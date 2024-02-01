package com.ssafy.dancy.auth;

import com.ssafy.dancy.ApiTest;
import com.ssafy.dancy.CommonDocument;
import com.ssafy.dancy.entity.User;
import com.ssafy.dancy.message.request.auth.LoginUserRequest;
import com.ssafy.dancy.message.request.user.SignUpRequest;
import com.ssafy.dancy.repository.UserRepository;
import com.ssafy.dancy.service.user.UserService;
import com.ssafy.dancy.type.Role;
import com.ssafy.dancy.user.UserDocument;
import io.restassured.matcher.RestAssuredMatchers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;

public class AuthApiTest extends ApiTest {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthSteps authSteps;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    private SignUpRequest signUpRequest;

    @BeforeEach
    void settings(){
        signUpRequest = authSteps.회원가입정보_생성();
        userService.signup(signUpRequest, Set.of(Role.USER));
    }

    @Test
    void 로그인성공_200(){
        LoginUserRequest request = AuthSteps.로그인요청생성();

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "로그인을 진행하는 API 입니다." +
                                "<br>로그인에 성공한다면, 200 OK 와 함께 Access Token 과 Refresh Token, 인증타입, 토큰타입 정보가 반환됩니다." +
                                "<br>아이디나 비밀번호가 일치하지 않는다면, 404 Not Found 가 반환됩니다.",
                        "로그인",
                        AuthDocument.LoginUserRequestField, AuthDocument.JwtTokenResponseField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .cookie("refreshToken", notNullValue())
                .cookie("refreshToken", RestAssuredMatchers.detailedCookie().httpOnly(true))
                .body("accessToken", notNullValue())
                .log().all().extract();

        Mockito.verify(mockValueOp, times(1)).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void 로그아웃_refreshToken_cookie_제거_200(){
        String token = authSteps.로그인액세스토큰정보(AuthSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "로그아웃을 진행하는 API 입니다." +
                                "<br>로그아웃에 성공한다면, 200 OK 와 함께 Refresh Token 을 가지고 있는 Cookie 가 삭제됩니다." +
                                "<br>프론트앤드 영역에서 Access Token 을 직접 삭제시켜 주어야 합니다." +
                                "<br>AUTH-TOKEN 을 입력하지 않았을 때, 401 Unauthorized 가 반환됩니다.",
                        "로그아웃",
                        CommonDocument.AccessTokenHeader))
                .header("AUTH-TOKEN", token)
                .when()
                .post("/auth/logout")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .cookie("refreshToken", RestAssuredMatchers.detailedCookie().maxAge(0))
                .log().all().extract();

        Mockito.verify(redisTemplate, times(1)).delete(anyString());
        Mockito.verify(mockValueOp, times(2)).set(anyString(), anyString(), anyLong(), any());
        // 로그인 할 때 한번 저장, 로그아웃할 때 블랙리스트 한번 저장
    }


    @Test
    void 비밀번호변경_성공_200(){
        String token = authSteps.로그인액세스토큰정보(AuthSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH, "비밀번호를 변경하는 API 입니다." +
                                "<br>비밀번호 변경에 성공한다면, 200 OK 와 함께 로그아웃 처리됩니다." +
                                "<br>프론트앤드 영역에서 Access Token 을 직접 삭제시켜 주어야 합니다." +
                                "<br>바꾸고자 하는 비밀번호가 정규표현식을 만족하지 못하는 경우, 400 Bad Request 와 함께 에러 메세지가 반환됩니다." +
                                "<br>AUTH_TOKEN 이 유효하지 않거나, 값이 없을 경우 401 Unauthorized 가 반환됩니다." +
                                "<br>A기존 패스워드와 일치하지 않을 경우, 403 Forbidden 과 함께 패스워드가 일치하지 않는다는 메세지가 반환됩니다.",
                        "비밀번호 변경",
                        CommonDocument.AccessTokenHeader, UserDocument.changePasswordRequestField))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("AUTH-TOKEN", token)
                .body(authSteps.비밀번호_변경_요청생성())
                .when()
                .put("/auth/change")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .cookie("refreshToken", RestAssuredMatchers.detailedCookie().maxAge(0))
                .log().all().extract();

        User user = userRepository.findByEmail(AuthSteps.email).get();
        assertThat(passwordEncoder.matches(AuthSteps.newPassword, user.getPassword())).isTrue();
    }

    @Test
    void 비밀번호변경_정규표현식_불만족_400(){
        String token = authSteps.로그인액세스토큰정보(AuthSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.changePasswordRequestField, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("AUTH-TOKEN", token)
                .body(authSteps.비밀번호_변경_조건불만족())
                .when()
                .put("/auth/change")
                .then()
                .assertThat()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all().extract();
    }

    @Test
    void 비밀번호변경_토큰없음_401(){
        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(authSteps.비밀번호_변경_요청생성())
                .when()
                .put("/auth/change")
                .then()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all().extract();
    }

    @Test
    void 비밀번호변경_비밀번호불일치_403(){

        String token = authSteps.로그인액세스토큰정보(AuthSteps.로그인요청생성());

        given(this.spec)
                .filter(document(DEFAULT_RESTDOC_PATH,
                        UserDocument.changePasswordRequestField, CommonDocument.ErrorResponseFields))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("AUTH-TOKEN", token)
                .body(authSteps.비밀번호_변경_비밀번호틀림())
                .when()
                .put("/auth/change")
                .then()
                .assertThat()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all().extract();
    }
}
