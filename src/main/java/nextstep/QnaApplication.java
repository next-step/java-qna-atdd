package nextstep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SuppressWarnings("SpellCheckingInspection")
@SpringBootApplication
@ComponentScan({"nextstep", "support"})
@EnableJpaAuditing
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class QnaApplication {
    public static void main(String[] args) {
        SpringApplication.run(QnaApplication.class, args);
    }
}
