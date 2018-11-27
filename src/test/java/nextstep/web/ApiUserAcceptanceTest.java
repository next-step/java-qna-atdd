package nextstep.web;

import nextstep.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static nextstep.domain.UserTest.newUser;

@SuppressWarnings({"NonAsciiCharacters", "SpellCheckingInspection"})
public class ApiUserAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiUserAcceptanceTest.class);

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 사용자_생성() {
        // testuser1 이라는 아이디를 가지는 사용자 객체 생성
        final User newUser = newUser("testuser1");
        // 사용자 생성 API 호출
        final ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        // 샹태 코드 201 검사
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        // 응답에 있는 Location 헤더
        final String location = response.getHeaders().getLocation().getPath();
        // 응답에 있는 Location 헤더가 있는지 검사
        softly.assertThat(location).isNotNull();
        // 응답에 있는 Location 헤더를 이용해서 생성한 사용자 데이터 조회
        final User dbUser = basicAuthTemplate(findByUserId(newUser.getUserId())).getForObject(location, User.class);
        // 데이터가 존재하면 성공
        softly.assertThat(dbUser).isNotNull();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 다른_사람의_정보_조회() {
        // testuser2 이라는 아이디를 가지는 사용자 객체 생성
        final User newUser = newUser("testuser2");
        // 사용자 생성 API 호출
        ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        // 샹태 코드 201 검사
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        // 응답에 있는 Location 헤더
        final String location = response.getHeaders().getLocation().getPath();
        // 응답에 있는 Location 헤더가 있는지 검사
        softly.assertThat(location).isNotNull();
        // 다른 사람이 헤더의 Location 정보를 이용해서 데이터 조회
        response = basicAuthTemplate(defaultUser()).getForEntity(location, Void.class);
        // Forbidden 상태가 정상
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 수정() {
        // testuser3 이라는 아이디를 가지는 사용자 객체 생성
        final User newUser = newUser("testuser3");
        // 사용자 생성 API 호출
        final ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        // 응답에 있는 Location 헤더
        final String location = response.getHeaders().getLocation().getPath();
        // 응답에 있는 Location 헤더가 있는지 검사
        softly.assertThat(location).isNotNull();
        // 샹태 코드 201 검사
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        // 자신의 정보를 조회
        final User original = basicAuthTemplate(newUser).getForObject(location, User.class);
        // 자신의 이름/이메일 수정
        final User updateUser
                = new User(original.getId(), original.getUserId(), original.getPassword(), "javajigi2", "javajigi2@slipp.net");
        // 수정 API 호출
        final ResponseEntity<User> responseEntity
                = basicAuthTemplate(newUser)
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), User.class);
        // 상태 코드 200 검사
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 이름/이메일이 수정되었는지 검사
        softly.assertThat(updateUser.equalsNameAndEmail(responseEntity.getBody())).isTrue();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 로그인하지_않은_상태에서_사용자_정보_수정() {
        // testuser4 이라는 아이디를 가지는 사용자 객체 생성
        final User newUser = newUser("testuser4");
        // 사용자 생성 API 호출
        final ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        // 응답에 있는 Location 헤더
        final String location = response.getHeaders().getLocation().getPath();
        // 응답에 있는 Location 헤더가 있는지 검사
        softly.assertThat(location).isNotNull();
        // 샹태 코드 201 검사
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        // 자신의 정보를 조회
        final User original = basicAuthTemplate(newUser).getForObject(location, User.class);
        // 자신의 이름/이메일 수정
        final User updateUser
                = new User(original.getId(), original.getUserId(), original.getPassword(), "javajigi2", "javajigi2@slipp.net");
        // 로그인하지 않은 상태에서 수정 시도
        final ResponseEntity<String> responseEntity
                = template()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), String.class);
        // 상태코드가 401 이면 정상
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        // 응답 바디 출력
        log.debug("error message : {}", responseEntity.getBody());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void 다른_사람_정보_수정() {
        // testuser5 이라는 아이디를 가지는 사용자 객체 생성
        final User newUser = newUser("testuser5");
        // 사용자 생성 API 호출
        final ResponseEntity<Void> response = template().postForEntity("/api/users", newUser, Void.class);
        // 응답에 있는 Location 헤더
        final String location = response.getHeaders().getLocation().getPath();
        // 응답에 있는 Location 헤더가 있는지 검사
        softly.assertThat(location).isNotNull();
        // 샹태 코드 201 검사
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        // 자신의 이름/이메일 수정
        final User updateUser
                = new User(newUser.getUserId(), newUser.getPassword(), "name2", "javajigi@slipp.net2");
        // 로그인한 상태에서 다른 사람의 정보 수정 시도
        final ResponseEntity<Void> responseEntity
                = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateUser), Void.class);
        // 상태 코드가 403 이면 정상
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private HttpEntity<Object> createHttpEntity(final Object body) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

}
