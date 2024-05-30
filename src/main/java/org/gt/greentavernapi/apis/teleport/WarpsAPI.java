package org.gt.greentavernapi.apis.teleport;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.gt.greentavernapi.GreenTavernAPI;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

public class WarpsAPI {
    public WarpsAPI(RedissonClient redisson, GreenTavernAPI greenTavernAPI){
        RTopic velocityWarps = redisson.getTopic("velocity_warps");
        RTopic serverWarps = redisson.getTopic(GreenTavernAPI.SERVER_NAME + "_warps");
        serverWarps.addListener(String[].class, (channel, message) -> {
            Player player = Bukkit.getPlayer(message[0]);
            velocityWarps.publish(new String[]{
                    message[1], new ServerLocation(player.getLocation(), GreenTavernAPI.SERVER_NAME).serializeToJSON()
            });
        });
    }
}
