package org.openhds.repository.results;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ben on 8/18/15.
 */
public class PageMaker {

    public static <T> Page<T> makePage(List<T> list, Pageable pageable) {
        int totalSize = list.size();

        int startIndex = pageable.getOffset();
        if (startIndex >= totalSize) {
            // empty page past the end
            return new PageImpl<T>(new ArrayList<>(), pageable, totalSize);
        }

        int endIndex = startIndex + pageable.getPageSize();
        if (endIndex >= totalSize) {
            // short page at the end
            endIndex = totalSize;
        }

        return new PageImpl<T>(list.subList(startIndex, endIndex), pageable, totalSize);
    }
}
