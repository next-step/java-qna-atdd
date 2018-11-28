package nextstep.service;

import nextstep.UnAuthenticationException;
import nextstep.UnAuthorizedException;
import nextstep.domain.User;
import nextstep.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service("userService")
public class UserService {
    @Resource(name = "userRepository")
    private UserRepository userRepository;

    public User add(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User update(User loginUser, long id, User updatedUser) {
        User original = findById(loginUser, id);
        original.update(loginUser, updatedUser);
        return original;
    }
    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    public User findById(User loginUser, long id) {
        return userRepository.findById(id)
                .filter(user -> user.equals(loginUser))
                .orElseThrow(UnAuthorizedException::new);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User login(String userId, String password) throws UnAuthenticationException {
        return userRepository.findByUserId(userId).filter(x->x.matchPassword(password)).orElseThrow(UnAuthenticationException::new);
    }
}
