package org.gt.greentavernapi.apis.actionbar;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class ActionBarModule {
    private String name;
    private int priority;
    ActionBarActions action;

    public ActionBarModule(String name, int priority, ActionBarActions action) {
        this.name = name;
        this.priority = priority;
        this.action = action;
    }
    public ActionBarModule(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public void initialize(ActionBarActions action){
        this.action = action;
    }
    public boolean isInitalized(){
        return action != null;
    }

    public Component getInfo(Player player){
        return action.information(player);
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }
}
