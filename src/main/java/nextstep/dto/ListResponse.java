package nextstep.dto;

import java.util.List;

public class ListResponse<T> {
    private List<T> list;
    private Integer count;

    public ListResponse(List<T> result) {
        list = result;
        count = result.size();
    }

    public List<T> getList() {
        return list;
    }

    public Integer getCount() {
        return count;
    }
}
