package feudalism.object;

import feudalism.Util;

public class ConfigFunction {
    private static final String FUNCTION_BASE = "function(%s)%s\n%send";
    private String argCode;
    private String luaCode;

    public ConfigFunction(String argCode, String luaCode) {
        this.argCode = argCode;
        this.luaCode = "\n" + luaCode;
    }

    public String getCode(int tabLevel) {
        return String.format(FUNCTION_BASE, argCode, luaCode.replaceAll("\n", "\n" + Util.repeatString("\t", tabLevel)), Util.repeatString("\t", tabLevel - 1));
    }
}
