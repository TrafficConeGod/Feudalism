package feudalism.object;

import java.util.ArrayList;
import java.util.List;

import feudalism.Config;

public class PermType {
    private int index;
    private String name;
    private String displayName;
    private boolean defaultStatus;
    private List<String> events = new ArrayList<>();

    public PermType(int index) {
        this.index = index;
        name = Config.getString(getPath("name"));
        displayName = Config.getString(getPath("display_name"));
        defaultStatus = Config.getBoolean(getPath("default"));
        int size = Config.getInt(String.format("#realm.perms[%s].events", index));
        for (int i = 1; i <= size; i++) {
            String eventName = Config.getString(String.format(getPath("events") + "[%s]", i));
            events.add(eventName);
        }
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean getDefaultStatus() {
        return defaultStatus;
    }

    public boolean hasEvent(String eventName) {
        return events.contains(eventName);
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    private String getPath(String path) {
        return String.format("realm.perms[%s].%s", index, path);
    }
}
