package nextstep.web;


import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import nextstep.UnAuthorizedException;
import nextstep.domain.Question;
import nextstep.domain.User;
import nextstep.security.LoginUser;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questions")
public class QuestionController {

  private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

  @Resource(name = "qnaService")
  private QnaService qnaService;

  @GetMapping("/form")
  public String form() {
    return "/qna/form";
  }

  @PostMapping("")
  public String create(@LoginUser User loginUser, Question question) {
    qnaService.create(loginUser, question);
    return "redirect:/";
  }

  @GetMapping("/{id}")
  public String show(Model model, @PathVariable long id) {
    Question question = qnaService.findById(id)
        .orElseThrow(EntityNotFoundException::new);
    model.addAttribute("question", question);
    return "/qna/show";
  }

  @GetMapping("/{id}/form")
  public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {

    Optional<Question> optionalQuestion = qnaService.findById(id);
    if(!optionalQuestion.isPresent()) {
      throw new EntityNotFoundException();
    }
    model.addAttribute("question", optionalQuestion
        .filter(question -> question.isOwner(loginUser))
        .orElseThrow(UnAuthorizedException::new));
    return "/qna/updateForm";
  }
}
