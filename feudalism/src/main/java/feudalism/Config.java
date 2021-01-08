package feudalism;

import java.util.HashMap;
import java.util.Map;

import org.luaj.vm2.Globals;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class Config {
    private static LuaValue configTable;
    private static Map<String, Object> configSchema;
    private static final String ENTRY_BASE = "%s%s = %s;\n";

    public static Map<String, Object> getConfigSchema() {
        if (configSchema == null) {
            configSchema = new HashMap<>();
            // realm config
            {
                Map<String, Object> map = new HashMap<>();
                map.put("coord_grid_size", 8);
                configSchema.put("realm", map);
            }
        }
        return configSchema;
    }

    private static String repeatString(String str, int count) {
        return new String(new char[count]).replace("\0", str);
    }

    private static String mapToLua(Map<String, Object> map, int tabLevel) {
        String luaString = "{\n";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            String luaValue;
            if (val.getClass() == HashMap.class) {
                luaValue = mapToLua((Map<String, Object>) val, tabLevel + 1); // lack of type safety go brr
            } else {
                luaValue = val.toString();
            }
            String luaEntry = String.format(ENTRY_BASE, repeatString("\t", tabLevel), key, luaValue);
            luaString += luaEntry;
        }
        luaString += repeatString("\t", tabLevel - 1) + "}";
        return luaString;
    }

    public static String generate() {
        return "return " + mapToLua(getConfigSchema(), 1) + ";";
    }

    private static boolean isValid(LuaValue table, Map<String, Object> map) {
        if (table.type() != 5) {
            return false;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            LuaValue luaVal = table.get(key);
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
            } else {
                if (val.getClass() == Boolean.class) {
                    if (luaVal.type() != 1) {
                        return false;
                    }
                } else if (val.getClass() == Integer.class || val.getClass() == Float.class || val.getClass() == Double.class) {
                    if (luaVal.type() != 3) {
                        return false;
                    }
                } else if (val.getClass() == String.class) {
                    if (luaVal.type() != 4) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void load(String luaCode) throws FeudalismException {
        Globals globals = JsePlatform.standardGlobals();
        LuaValue table = globals.load(luaCode).call();
        if (!isValid(table, getConfigSchema())) {
            throw new FeudalismException("Unable to load lua config, invalid formatting.");
        }
        configTable = table;
    }

    private static Object get(String path, int type) throws FeudalismException {
        String[] split = path.split("\\.");
        LuaValue table = configTable;
        for (String pathDown : split) {
            LuaValue val = table.get(pathDown);
            if (val.type() == 0) {
                throw new FeudalismException(String.format("No config value with path %s", path));
            }
            if (val.type() == 5) {
                table = val;
            } else {
                if (val.type() != type) {
                    throw new FeudalismException("Config value type does not match expected type");
                }
                if (val.type() == 1) {
                    return val.checkboolean();
                } else if (val.type() == 3) {
                    return val.checkint();
                } else if (val.type() == 4) {
                    return val.checkjstring();
                }
            }
        }
        throw new FeudalismException(String.format("No config value with path %s", path));
    }

    public static boolean getBoolean(String path) throws FeudalismException {
        return (boolean) get(path, 1);
    }

    public static int getInt(String path) throws FeudalismException {
        return (int) get(path, 3);
    }

    public static String getString(String path) throws FeudalismException {
        return (String) get(path, 4);
    }
}
