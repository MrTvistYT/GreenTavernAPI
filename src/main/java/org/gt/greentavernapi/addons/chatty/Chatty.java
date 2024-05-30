package org.gt.greentavernapi.addons.chatty;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.luckperms.api.LuckPermsProvider;

import net.luckperms.api.model.user.User;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gt.greentavernapi.GreenTavernAPI;
import org.gt.greentavernapi.apis.ComponentAPI;
import org.redisson.api.RedissonClient;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;

public class Chatty implements Listener {
    final RedissonClient redisson;
    final GreenTavernAPI gtAPI;
    final FileConfiguration config;
    final HashMap<String, String> chatsBySymbol = new HashMap<>();

    public Chatty(RedissonClient redisson, FileConfiguration config, GreenTavernAPI gtAPI){
        this.redisson = redisson;
        this.config = config;
        this.gtAPI = gtAPI;

        chatsBySymbol.put("!", "global");
        chatsBySymbol.put("$", "trade");
        chatsBySymbol.put("?", "support");
        chatsBySymbol.put("@", "donate");
        chatsBySymbol.put("^", "admins");
    }

    @EventHandler
    public void message(AsyncChatEvent event){
        sendMessage(event.getPlayer(), event.message());
        event.setCancelled(true);
    }

    public void sendMessage(Player player, Component message){
        if("/".equals(ComponentAPI.componentToString(message).substring(0,1))) return;
        String chatType = getType(ComponentAPI.componentToString(message));

        Component result = ComponentAPI.miniMessage(
                config.getString("chatty." + chatType)
                        .replace("{prefix}", getPrefix(player))
                        .replace("{player}", player.getName())
                        .replace("{message}", ComponentAPI.componentToString(message).substring("local".equals(chatType) ? 0 : 1)));

        if("local".equals(chatType)) {
            Collection<Player> entities = player.getWorld().getNearbyPlayers(player.getLocation(), config.getInt("chatty.local_distance"));

            if(entities.size() == 1){
                player.sendMessage(result);
                player.sendMessage(Component.text("[!] Никого нет рядом!"));
                return;
            }
            for(Player players : entities){
                players.sendMessage(result);
            }
            return;
        }

        redisson.getTopic("chat").publish(result);
    }

    private String getPrefix(Player player){
        User user = LuckPermsProvider.get().getUserManager().getUser(player.getName());
        return user.getCachedData().getMetaData().getPrefix();
    }

    private String getType(String text){
        return chatsBySymbol.getOrDefault(text.substring(0,1), "local");
    }
}
