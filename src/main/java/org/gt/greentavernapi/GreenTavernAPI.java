package org.gt.greentavernapi;

import org.bukkit.plugin.java.JavaPlugin;
import org.gt.greentavernapi.apis.ComponentAPI;

import java.io.File;

public final class GreenTavernAPI extends JavaPlugin {

    @Override
    public void onEnable() {
        saveResource("offset.yml", false);
        saveResource("translateForItems.json", false);

        ComponentAPI componentAPI = new ComponentAPI(
                new File(getDataFolder(), "translateForItems.json"),
                new File(getDataFolder(), "offset.yml")
        );
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
