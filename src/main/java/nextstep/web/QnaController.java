package nextstep.web;

import nextstep.web.exception.ForbiddenException;
import nextstep.web.exception.NotFoundException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/qna")
public class QnaController {
    private final QnaService qnaService;

    public QnaController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<Question> optionalQuestion = qnaService.findById(id);

        if(!optionalQuestion.isPresent()) {
            throw new NotFoundException();
        }

        model.addAttribute("question", optionalQuestion.get());
        return "qna/show";
    }

    @GetMapping("/form")
    public String form(@LoginUser User user) {
        return "qna/form";
    }

    @PostMapping("")
    public String create(@LoginUser User user, Question question, Model model) {
        Question result = qnaService.create(user, question);

        model.addAttribute("question", result);
        return "qna/show";
    }
}
