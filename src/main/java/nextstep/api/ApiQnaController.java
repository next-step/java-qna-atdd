package nextstep.api;

import lombok.RequiredArgsConstructor;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class ApiQnaController {
    private final QnaService qnaService;
     //"redirect:" + updateQuestion.generateUrl();
    //@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody User updatedUser
    @PostMapping
     public ResponseEntity<Void> createQuestion(@LoginUser User loginUser, @RequestBody Question question) {

         Question savedQuestion = qnaService.create(loginUser, question);

         HttpHeaders headers = new HttpHeaders();
         headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));
         return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
     }
}
