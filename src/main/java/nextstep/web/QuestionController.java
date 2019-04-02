package nextstep.web;

import nextstep.CannotDeleteException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Question;
import nextstep.domain.QuestionRepository;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;


    @GetMapping("/form")
    public String form() { return "/qna/form"; }

    @PostMapping("")
    public String create(Question question, @LoginUser User loginUser) {
        qnaService.create(loginUser, question);
        return "redirect:/questions";
    }

    @GetMapping("")
    public String list(Model model) {
        List<Question> questions = new ArrayList<>();
        Iterable<Question> questionsIterable = qnaService.findAll();
        questionsIterable.iterator().forEachRemaining(questions::add);
        model.addAttribute("questions", questions);
        return "home";
    }

    @GetMapping("/{id}")
    public String showDetailQuestion(@PathVariable Long id, Model model) {
        Question question = qnaService.findById(id).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("question", question);
        return "/qna/show";
    }

    @PostMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable long id, Question question) throws UnAuthorizedException{
        qnaService.update(loginUser, id, question);
        return "redirect:/questions";
    }

    @GetMapping("/{id}/form")
    public String formForUpdate(@LoginUser User loginUser, @PathVariable long id, Model model) {
        Question question = qnaService.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        model.addAttribute("question", question);
        return "/qna/updateForm";
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/questions";
    }




}
