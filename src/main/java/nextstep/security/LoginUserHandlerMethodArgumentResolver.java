package nextstep.security;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        User user = HttpSessionUtils.getUserFromSession(webRequest);
        if (!user.isGuestUser()) {
            return user;
        }

        // 이 코드는 guest user를 처리하는 부분
        // LoginUser 어노테이션의 required가 false 이면 guest user로 받아서 처리하겠다는 뜻임.
        LoginUser loginUser = parameter.getParameterAnnotation(LoginUser.class);
        if (loginUser.required()) {
            throw new UnAuthenticationException("You're required Login!");
        }
        return user;
    }
}
