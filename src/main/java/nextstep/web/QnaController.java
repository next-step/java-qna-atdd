package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/questions")
public class QnaController {
    private static final Logger log = LoggerFactory.getLogger(QnaController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping
    public String qnaList(@LoginUser User loginUser) {
        return "redirect:/";
    }

    @GetMapping("/form")
    public String form(@LoginUser User loginUser) {
        return "/qna/form";
    }

    @GetMapping("/{id}")
    public String getQuestion(Model model, @PathVariable long id) {
        Question question = qnaService.findById(id).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @PostMapping
    public String createQuestion(@LoginUser User loginUser, String title, String contents, Long id) throws Exception{
        if (id == null) {
            qnaService.create(loginUser, new Question(title, contents));
            return "redirect:/";
        }
        qnaService.update(loginUser, id, new Question(title, contents));
        return  "redirect:/";
    }

    @GetMapping("/{id}/form")
    public String updateQuestion(Model model,  @LoginUser User loginUser, @PathVariable Long id) {
        Question question = qnaService.findById(id).orElseThrow(NullPointerException::new);
        model.addAttribute("question", qnaService.findById(id).orElseThrow(NullPointerException::new));
        return "/qna/updateForm";
    }

    @PutMapping("/{id}")
    public String updateQuestion(@LoginUser User loginUser, @PathVariable long id, Question target) throws Exception{
        qnaService.update(loginUser, id, target);
        return "redirect:/";
    }

    @DeleteMapping("/{id}")
    public String deleteQuestion(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }

    @PostMapping("/{id}/answers")
    public String addAnswer(@LoginUser User loginUser, @PathVariable Long id, String contents) {
        qnaService.addAnswer(loginUser, id, contents);
        return "redirect:/questions/"+id;
    }
}
