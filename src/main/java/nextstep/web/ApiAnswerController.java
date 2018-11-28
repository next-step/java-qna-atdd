package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.CannotFoundException;
import nextstep.domain.Answer;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/answers")
public class ApiAnswerController {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("/{id}/answer")
    public ResponseEntity<Void> createAnswer(@LoginUser User user, @PathVariable("id") long id, @RequestBody Answer answer) throws CannotFoundException {
        qnaService.addAnswer(user, id, answer.getContents());
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/answer/{answerId}")
    public Question deleteAnswer(@LoginUser User user, @PathVariable("id") long id , @PathVariable("answerId") long answerId) throws CannotFoundException, CannotDeleteException {
        qnaService.deleteAnswer(user, id, answerId);
        return qnaService.findByIdAndDeletedFalse(user ,id);
    }



}
