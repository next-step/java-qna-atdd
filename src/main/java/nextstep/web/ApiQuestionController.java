package nextstep.web;

import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/{id}")
    public Question get(@PathVariable long id) {
        return qnaService.findById(id);
    }

    @PostMapping("")
    public ResponseEntity create(@LoginUser User loginUser,
                                 @Valid @RequestBody Question question) {
        Question created = qnaService.create(loginUser, question);

        return ResponseEntity.created(URI.create("/api" + created.generateUrl())).build();
    }
}
