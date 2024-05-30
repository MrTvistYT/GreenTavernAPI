package org.gt.greentavernapi.apis.actionbar;


import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import me.clip.placeholderapi.PlaceholderAPI;
import me.yic.xconomy.api.XConomyAPI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.gt.greentavernapi.GreenTavernAPI;
import org.gt.greentavernapi.apis.ComponentAPI;
import org.gt.greentavernapi.apis.teleport.TeleportAPI;

import java.util.*;

public class ActionBarAPI implements Listener {
    private static final List<ActionBarModule> initialized = new LinkedList<>();
    private static final List<ActionBarModule> notInitialized = new ArrayList<>();
    private static final Map<Player, List<ActionBarModule>> playerActionBar = new HashMap<>();
    GreenTavernAPI gtAPI;

    public ActionBarAPI(GreenTavernAPI gtAPI, FileConfiguration cfg){
        this.gtAPI = gtAPI;
        loadFromConfig(cfg);

        for(ActionBarModule module : notInitialized){
            Bukkit.getLogger().info(module.getName());
        }

        initializeModule("TELEPORT", p -> {
            int cd = TeleportAPI.getCooldown(p);
            Component r = Component.empty().append(Component.text(" << ").color(TextColor.color(84, 84, 84)));

            if (cd >= 0 && cd <= 3)
                r = r.append(Component.text("До телепорта осталось ").color(TextColor.color(255, 255, 255)))
                    .append(Component.text(cd).color(TextColor.color(44, 136, 0)));
            else
                r = r.append(Component.text("Телепорт отменён").color(TextColor.color(255, 255, 255)));

            return r.append(Component.text(" >> ").color(TextColor.color(84, 84, 84)));
        });
        initializeModule("MONEY", player -> {
            String money = PlaceholderAPI.setPlaceholders(player, "%xconomy_balance_formatted%")
                                        .replace("dollars","");
            money = money.substring(0, money.length() - 1);

            String icon = PlaceholderAPI.setPlaceholders(player, "%img_coin_icon%");
            int length = ComponentAPI.getOffset(money + icon);

            return ComponentAPI.space(62 - length)
                    .append(Component.text(money).font(Key.key("horizontal_offset:line8")))
                    .append(Component.text(icon).font(Key.key("minecraft:default")))
                    .append(ComponentAPI.space(-(62*2)));
        });
    }

    @EventHandler
    public void join(PlayerJoinEvent event){
        playerActionBar.put(event.getPlayer(), new ArrayList<>(){{
            add(getByType("MONEY"));
        }});
        showForPlayer(gtAPI, event.getPlayer());
    }

    public static ActionBarModule getByType(String type){
        for(ActionBarModule module : notInitialized){
            if(module.getName().equals(type)) return module;
        }
        for(ActionBarModule module : initialized){
            if(module.getName().equals(type)) return module;
        }
        return null;
    }
    public static ActionBarModule getByPriority(int priority){
        for(ActionBarModule module : initialized){
            Bukkit.getLogger().info(priority + "");
            Bukkit.getLogger().info(module.getPriority() + "");
            if(module.getPriority() == priority) return module;
        }
        return null;
    }
    public static ActionBarModule getLatest(Player player){
        return playerActionBar.get(player)
                .stream()
                .max(Comparator.comparing(ActionBarModule::getPriority))
                .get();
    }


    public static void showForPlayer(GreenTavernAPI gtAPI, Player player){
        Bukkit.getScheduler().runTaskTimer(gtAPI, task -> {
            if(player.isOnline()){
                player.sendActionBar(getLatest(player).getInfo(player));
                return;
            }
            task.cancel();
        }, 2,2);
    }


    public static void initializeModule(String name, ActionBarActions actions){
        ActionBarModule module = getByType(name);
        if(module == null){
            Bukkit.getLogger().warning("Такого модуля не существует!");
            return;
        }
        module.initialize(actions);

        initialized.add(module);
        notInitialized.remove(module);
    }
    public static boolean addActionBarType(Player player, String type) {
        ActionBarModule module = getByType(type);
        if(module.isInitalized()) {
            playerActionBar.get(player).add(module);
        }
        return module.isInitalized();
    }
    public static boolean removeActionBarType(Player player, String type) {
        ActionBarModule module = getByType(type);
        if(module.isInitalized()) {
            playerActionBar.get(player).remove(module);
        }
        return module.isInitalized();
    }



    public void loadFromConfig(FileConfiguration cfg) {
        for (Map<?, ?> module : cfg.getMapList("priorities")) {
            String type = (String) module.get("type");
            int priority = (int) module.get("priority");

            notInitialized.add(new ActionBarModule(type, priority));
        }
    }
}
