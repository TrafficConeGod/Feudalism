package feudalism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import feudalism.object.ConfigFunction;

public class Config {
    private static LuaValue configTable;
    private static Map<String, Object> configSchema;
    private static final String ENTRY_BASE = "%s%s = %s;\n";
    private static final String MEMBER_BASE = "%s%s;\n";
    private static Globals configGlobals;
    private static Globals getGlobals;

    public static Map<String, Object> getConfigSchema() {
        if (configSchema == null) {
            configSchema = new HashMap<>();
            // realm config
            {
                Map<String, Object> map = new HashMap<>();
                map.put("world", "world");
                configSchema.put("realm", map);
            }
            // grid_coord config
            {
                Map<String, Object> map = new HashMap<>();
                map.put("size", 8);
                configSchema.put("grid_coord", map);
            }
            // siege config
            {
                Map<String, Object> map = new HashMap<>();
                List<Object> list = new ArrayList<>();
                {
                    Map<String, Object> submap = new HashMap<>();
                    submap.put("name", "ruler_change");
                    submap.put("display_name", "Demand Ruler Change");
                    {
                        List<Object> sublist = new ArrayList<>();
                        sublist.add("new_ruler");
                        submap.put("props", sublist);
                    }
                    submap.put("on_peace", new ConfigFunction("victor, loser, props", "local new_ruler = Util:getPlayerUuidByName(props.new_ruler);\nloser:setOwner(new_ruler);"));
                    list.add(submap);
                }
                {
                    Map<String, Object> submap = new HashMap<>();
                    submap.put("name", "subjugate");
                    submap.put("display_name", "Subjugate");
                    submap.put("props", new ArrayList<>());
                    submap.put("on_peace", new ConfigFunction("victor, loser", "victor:addSubject(loser);"));
                    list.add(submap);
                }
                {
                    Map<String, Object> submap = new HashMap<>();
                    submap.put("name", "personal_union");
                    submap.put("display_name", "Demand Personal Union");
                    submap.put("props", new ArrayList<>());
                    submap.put("on_peace", new ConfigFunction("victor, loser", "loser:setOwner(victor:getOwner());"));
                    list.add(submap);
                }
                map.put("goals", list);
                configSchema.put("siege", map);
            }
        }
        return configSchema;
    }

    private static String getLuaValue(Object val, int tabLevel) {
        String luaValue = "nil";
        if (val.getClass() == HashMap.class) {
            luaValue = mapToLua((Map<String, Object>) val, tabLevel + 1); // lack of type safety go brr
        } else if (val.getClass() == ArrayList.class) {
            luaValue = listToLua((List<Object>) val, tabLevel + 1);
        } else if (val.getClass() == ConfigFunction.class) {
            ConfigFunction fn = (ConfigFunction) val;
            luaValue = fn.getCode(tabLevel + 1);
        } else if (val.getClass() == String.class) {
            luaValue = "\"" + val.toString() + "\"";
        } else {
            luaValue = val.toString();
        }
        return luaValue;
    }

    private static String listToLua(List<Object> list, int tabLevel) {
        String luaString = "{\n";
        for (Object val : list) {
            String luaValue = getLuaValue(val, tabLevel);
            String luaEntry = String.format(MEMBER_BASE, Util.repeatString("\t", tabLevel), luaValue);
            luaString += luaEntry;
        }
        luaString += Util.repeatString("\t", tabLevel - 1) + "}";
        return luaString;
    }

    private static String mapToLua(Map<String, Object> map, int tabLevel) {
        String luaString = "{\n";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            String luaValue = getLuaValue(val, tabLevel);
            String luaEntry = String.format(ENTRY_BASE, Util.repeatString("\t", tabLevel), key, luaValue);
            luaString += luaEntry;
        }
        luaString += Util.repeatString("\t", tabLevel - 1) + "}";
        return luaString;
    }

    public static String generate() {
        return "return " + mapToLua(getConfigSchema(), 1) + ";";
    }

    private static boolean isValid(LuaValue luaVal, Object val) {
        if (luaVal.type() == 0) {
            return false;
        }
        if (val.getClass() == HashMap.class) {
            if (luaVal.type() != 5) {
                return false;
            }
            if (!isValid(luaVal, (Map<String, Object>) val)) {
                return false;
            }
        } else if (val.getClass() == ArrayList.class) {
            if (luaVal.type() != 5) {
                return false;
            }
            if (!isValid(luaVal, (List<Object>) val)) {
                return false;
            }
        } else {
            if (val.getClass() == Boolean.class) {
                if (luaVal.type() != 1) {
                    return false;
                }
            } else if (val.getClass() == Integer.class || val.getClass() == Float.class
                    || val.getClass() == Double.class) {
                if (luaVal.type() != 3) {
                    return false;
                }
            } else if (val.getClass() == String.class) {
                if (luaVal.type() != 4) {
                    return false;
                }
            } else if (val.getClass() == ConfigFunction.class) {
                if (luaVal.type() != 6) {
                    return false;
                }
            } else {
                System.out.println("Unsupported type " + val.getClass());
                return false;
            }
        }
        return true;
    }

    private static boolean isValid(LuaValue array, List<Object> list) {
        Object val = list.get(0);
        int size = array.len().checkint();
        for (int i = 1; i <= size; i++) {
            LuaValue luaVal = array.get(i);
            if (!isValid(luaVal, val)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isValid(LuaValue table, Map<String, Object> map) {
        if (table.type() != 5) {
            return false;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            LuaValue luaVal = table.get(key);
            if (!isValid(luaVal, val)) {
                return false;
            }
        }
        return true;
    }

    private static void checkConfigGlobals() {
        if (configGlobals == null) {
            configGlobals = JsePlatform.standardGlobals();
            configGlobals.set("Registry", CoerceJavaToLua.coerce(Registry.class));
            configGlobals.set("Util", CoerceJavaToLua.coerce(Util.class));
        }
    }

    public static void load(String luaCode) throws FeudalismException {
        checkConfigGlobals();
        LuaValue table = configGlobals.load(luaCode).call();
        if (!isValid(table, getConfigSchema())) {
            throw new FeudalismException("Unable to load lua config, invalid formatting.");
        }
        configTable = table;
    }

    public static void loadFile(String path) throws FeudalismException {
        checkConfigGlobals();
        LuaValue table = configGlobals.loadfile(path).call();
        if (!isValid(table, getConfigSchema())) {
            throw new FeudalismException("Unable to load lua config, invalid formatting.");
        }
        configTable = table;
    }

    private static Object get(String luaCode, int type) throws FeudalismException {
        if (Util.isJUnitTest() && configTable == null) {
            load(generate());
        }
        if (getGlobals == null) {
           getGlobals = JsePlatform.standardGlobals();
           getGlobals.set("Config", configTable);
        }
        String repl = "";
        if (luaCode.charAt(0) == '#') {
            repl = "#";
            luaCode = luaCode.substring(1);
        }
        LuaValue val = getGlobals.load(String.format("return %sConfig.%s", repl, luaCode)).call();
        if (val.type() != type) {
            throw new FeudalismException("Config value type does not match expected type");
        }
        if (val.type() == 1) {
            return val.checkboolean();
        } else if (val.type() == 3) {
            return val.checkint();
        } else if (val.type() == 4) {
            return val.checkjstring();
        } else if (val.type() == 6) {
            return val.checkfunction();
        }
        throw new FeudalismException(String.format("No config value with path %s", luaCode));
    }

    public static boolean getBoolean(String path) {
        try {
            return (boolean) get(path, 1);
        } catch (FeudalismException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getInt(String path) {
        try {
            return (int) get(path, 3);
        } catch (FeudalismException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getString(String path) {
        try {
            return (String) get(path, 4);
        } catch (FeudalismException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LuaFunction getFunction(String path) {
        try {
            return (LuaFunction) get(path, 6);
        } catch (FeudalismException e) {
            e.printStackTrace();
        }
        return null;
    }
}
