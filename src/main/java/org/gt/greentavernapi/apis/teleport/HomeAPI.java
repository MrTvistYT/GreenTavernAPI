package org.gt.greentavernapi.apis.teleport;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.gt.greentavernapi.GreenTavernAPI;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

public class HomeAPI {
    public HomeAPI(RedissonClient redisson, GreenTavernAPI greenTavernAPI){
        RTopic velocityHomes = redisson.getTopic("velocity_homes");
        RTopic serverHomes = redisson.getTopic(GreenTavernAPI.SERVER_NAME + "_homes");
        serverHomes.addListener(String[].class, (channel, message) -> {
            if(!channel.toString().equals(GreenTavernAPI.SERVER_NAME + "_homes")) return;
            Player player = Bukkit.getPlayer(message[0]);

            velocityHomes.publish(new String[] {
                    message[1], message[2], new ServerLocation(player.getLocation(), GreenTavernAPI.SERVER_NAME).serializeToJSON()
            });
        });
    }
}
