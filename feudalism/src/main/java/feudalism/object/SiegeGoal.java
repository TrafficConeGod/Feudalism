package feudalism.object;

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import feudalism.Config;
import feudalism.FeudalismException;

public class SiegeGoal {
    private int index;
    private String name;
    private String displayName;
    private List<String> props = new ArrayList<>();
    private LuaValue onPeace;

    public SiegeGoal(int index) {
        index++;
        this.index = index;
        name = Config.getString(getPath("name"));
        displayName = Config.getString(getPath("display_name"));
        onPeace = Config.getFunction(getPath("on_peace"));
        int size = Config.getInt(String.format("#siege.goals[%s].props", index));
        for (int i = 1; i <= size; i++) {
            String prop = Config.getString(String.format(getPath("props") + "[%s]", i));
            props.add(prop);
        }
    }

    private String getPath(String path) {
        return String.format("siege.goals[%s].%s", index, path);
    }

    public void execute(Realm victor, Realm loser, List<String> propValues) throws FeudalismException {
        if (props.size() != propValues.size()) {
            throw new FeudalismException("Prop sizes don't match.");
        }
        LuaTable luaProps = new LuaTable();
        for (int i = 0; i < props.size(); i++) {
            String key = props.get(i);
            String val = propValues.get(i);
            luaProps.set(key, val);
        }
        LuaValue luaVictor = CoerceJavaToLua.coerce(victor);
        LuaValue luaLoser = CoerceJavaToLua.coerce(loser);
        onPeace.call(luaVictor, luaLoser, luaProps);
    }
}
