package org.gt.greentavernapi;

import com.google.gson.Gson;
import me.clip.placeholderapi.PlaceholderAPI;
import me.yic.xconomy.api.XConomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.gt.greentavernapi.addons.AnvilReplacement;
import org.gt.greentavernapi.addons.RolePlayCommands;
import org.gt.greentavernapi.addons.Showers;
import org.gt.greentavernapi.addons.chatty.Chatty;
import org.gt.greentavernapi.addons.teleports.TeleportCommand;
import org.gt.greentavernapi.apis.ComponentAPI;
import org.gt.greentavernapi.apis.actionbar.ActionBarAPI;
import org.gt.greentavernapi.apis.teleport.HomeAPI;
import org.gt.greentavernapi.apis.teleport.TeleportAPI;
import org.gt.greentavernapi.apis.teleport.WarpsAPI;
import org.jetbrains.annotations.NotNull;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;

import java.io.File;
import java.nio.charset.StandardCharsets;

public final class GreenTavernAPI extends JavaPlugin implements Listener {
    public static String SERVER_NAME;
    private static GreenTavernAPI instance;
    @Override
    public void onEnable() {
        RedissonClient redisson = Redisson.create();
        instance = this;

        SERVER_NAME = getConfig().getString("plugin_server");

        saveResource("offset.yml", false);
        saveResource("translateForItems.json", false);
        saveResource("action.yml", false);
        saveResource("config.yml", false);

        new ComponentAPI(
                new File(getDataFolder(), "translateForItems.json"),
                new File(getDataFolder(), "offset.yml")
        );

        XConomyAPI xConomyAPI = new XConomyAPI();
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Bukkit.getLogger().info("PLACEHOLDER API IS NOT ENABLED");
            return;
        }

        File file = new File(Bukkit.getPluginManager().getPlugin("ItemsAdder").getDataFolder(), "storage/font_images_unicode_cache.yml");
        Bukkit.getPluginManager().registerEvents(new AnvilReplacement(file), this);

        Showers showers = new Showers(getConfig(), this);
        Bukkit.getPluginManager().registerEvents(showers, this);
        showers.initializeTimer();

        registerPluginChannel("chatty:chat", null);

        getCommand("me").setExecutor(new RolePlayCommands(getConfig()));
        getCommand("do").setExecutor(new RolePlayCommands(getConfig()));
        getCommand("try").setExecutor(new RolePlayCommands(getConfig()));
        getCommand("todo").setExecutor(new RolePlayCommands(getConfig()));
        getCommand("b").setExecutor(new RolePlayCommands(getConfig()));

        getCommand("gtvtp").setExecutor(new TeleportCommand(this));

        Bukkit.getPluginManager().registerEvents(new Chatty(redisson, getConfig(), this), this);
        Bukkit.getPluginManager().registerEvents(this, this);

        TeleportAPI teleportAPI = new TeleportAPI(redisson, this);
        WarpsAPI warpsAPI = new WarpsAPI(redisson, this);
        HomeAPI homeAPI = new HomeAPI(redisson, this);

        Bukkit.getPluginManager().registerEvents(teleportAPI, this);

        ActionBarAPI barAPI = new ActionBarAPI(this, YamlConfiguration.loadConfiguration(new File(getDataFolder(), "action.yml")));
        Bukkit.getPluginManager().registerEvents(barAPI, this);
    }


    public void registerPluginChannel(String channel, PluginMessageListener listener) {
        if(listener != null) Bukkit.getMessenger().registerIncomingPluginChannel(this, channel, listener);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, channel);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void teleport(PlayerTeleportEvent event){
        Bukkit.getLogger().info(event.isCancelled() + "");
    }

    public static GreenTavernAPI getInstance() {
        return instance;
    }
}
