package org.gt.greentavernapi;

import org.bukkit.Bukkit;
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

        ShowablePlugin showablePlugin = new ShowablePlugin(this);
        showablePlugin.initializeTimer();
        Bukkit.getPluginManager().registerEvents(showablePlugin, this);

//        TabAPI.getInstance().getEventBus().register(PlayerLoadEvent.class, event -> {
//            TabPlayer tabPlayer = event.getPlayer();
//            tabPlayer
//            //do something
//        });

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
