package ktplusplus.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.Filter;
import com.puppycrawl.tools.checkstyle.api.FilterSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrderedFilterSet extends FilterSet {
    private final List<Filter> filters = new ArrayList<>();

    @Override
    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    @Override
    public void removeFilter(Filter filter) {
        filters.remove(filter);
    }

    @Override
    public void clear() {
        filters.clear();
    }

    @Override
    public Set<Filter> getFilters() {
        return new HashSet<>(filters);
    }

    @Override
    public boolean accept(AuditEvent event) {
        for (Filter filter : filters) {
            if (!filter.accept(event)) {
                return false;
            }
        }
        return true;
    }
}
