package nextstep.web;


import javax.annotation.Resource;
import nextstep.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/qnas")
public class QnaController {

  private static final Logger log = LoggerFactory.getLogger(QnaController.class);

  @Resource(name = "qnaService")
  private QnaService qnaService;

  @GetMapping("/form")
  public String form() {
    return "/qna/form";
  }
}
