package nextstep.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeleteHistoryRepository extends JpaRepository<DeleteHistory, Long> {
    Optional<DeleteHistory> findByContentId(Long contentId);
}
