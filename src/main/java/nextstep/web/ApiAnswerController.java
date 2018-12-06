package nextstep.web;

import nextstep.UnAuthorizedException;
import nextstep.domain.Answer;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{id}/answers")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> add(@Valid @RequestBody User user, @PathVariable long id, String contents) {
        Answer answer = qnaService.addAnswer(user, id, contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + id + "/answers/" + answer.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{secondId}")
    public Answer show(@PathVariable long secondId) {
        return qnaService.findByAnswerId(secondId).get();
    }

    @PutMapping("{secondId}")
    public Answer update(@LoginUser User loginUser, @PathVariable long secondId, String contents) {
        if(!qnaService.findByAnswerId(secondId).get().isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        return qnaService.findByAnswerId(secondId).get().setContents(contents);
    }
}
