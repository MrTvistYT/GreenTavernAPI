package org.gt.greentavernapi.apis;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplaysOnPlayer {
    private static Map<Player, List<Entity>> entitiesOnPlayer = new HashMap<>();
    public void addPlayer(Player player){
        entitiesOnPlayer.put(player, new ArrayList<>());
    }


    public void addEntity(Player player, Entity entity){
        if(entitiesOnPlayer.get(player).isEmpty()){
            player.addPassenger(entity);
            entitiesOnPlayer.get(player).add(entity);
            return;
        }


    }
}
