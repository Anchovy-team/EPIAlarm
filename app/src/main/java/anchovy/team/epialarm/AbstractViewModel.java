package anchovy.team.epialarm;

import androidx.lifecycle.ViewModel;

import java.util.List;

public abstract class AbstractViewModel<T> extends ViewModel{
    protected List<T> cachedItems;

    public abstract List<T> getCachedItems();

    public abstract void setCachedItems(List<T> cachedItems);
    public boolean hasCachedItems() {
        return cachedItems != null && !cachedItems.isEmpty();
    }
}

