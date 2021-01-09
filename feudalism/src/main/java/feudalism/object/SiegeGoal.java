package feudalism.object;

import feudalism.Config;

public class SiegeGoal {
    private int index;
    private String name;

    public SiegeGoal() {

    }

    public SiegeGoal(String name) {
        int size = Config.getInt("#siege.goals");
        for (int i = 0; i < size; i++) {
            String checkName = Config.getString("siege.goals[" + i + "]");
            if (name.equals(checkName)) {
                index = i;
                break;
            }
        }
        System.out.println(size);
        System.out.println(index);
    }
}
