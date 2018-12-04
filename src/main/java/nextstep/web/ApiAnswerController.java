package nextstep.web;

import nextstep.domain.Answer;
import nextstep.domain.AnswerRepository;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{question_Id}/answers")
public class ApiAnswerController {

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@PathVariable long question_Id, @RequestBody String contents, @LoginUser User user) {
        Answer newAnswer = qnaService.addAnswer(user,question_Id,contents);
        log.debug("checking Contents : {}",newAnswer.getContents());
        log.debug("checking URL : {}",newAnswer.generateUrl());
        HttpHeaders headers = new HttpHeaders();
        //answer의 generateURL쓰도록 mapping 주소 변경
        headers.setLocation(URI.create("/api"+newAnswer.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{answer_Id}")
    public Answer showAnswerById(@PathVariable long answer_Id ){
        return qnaService.showAnswer(answer_Id);
    }

    @PostMapping("/{answer_Id}")
    public Answer delete(@LoginUser User user,  @PathVariable long answer_Id ){
        return qnaService.deleteAnswer(user,answer_Id);
    }
}
