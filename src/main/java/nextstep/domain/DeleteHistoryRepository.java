package nextstep.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DeleteHistoryRepository extends CrudRepository<DeleteHistory, Long> {

    Optional<DeleteHistory> findByContentTypeAndContentId(ContentType contentType, long contentId);

}
