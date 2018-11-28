package nextstep.domain;

public interface DeletePolicy<T> {
    boolean canPermission(T target, User user);
}
