package org.gt.greentavernapi.apis.teleport;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.gt.greentavernapi.GreenTavernAPI;
import org.gt.greentavernapi.apis.actionbar.ActionBarAPI;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TeleportAPI implements Listener {
    public HashMap<String, ServerLocation> playerLocations = new HashMap<>();
    public List<Player> cantMove = new ArrayList<>();
    private static final HashMap<Player, Integer> teleportCD = new HashMap<>();

    public TeleportAPI(RedissonClient redisson, GreenTavernAPI greenTavernAPI){
        RTopic velocity = redisson.getTopic("velocity_teleports");
        RTopic server = redisson.getTopic(GreenTavernAPI.SERVER_NAME + "_teleports");
        server.addListener(String[].class, (channel, message) -> {
            if(!channel.toString().equals(GreenTavernAPI.SERVER_NAME + "_teleports")) return;
            Bukkit.getLogger().info(Arrays.toString(message));

            ServerLocation sv;
            Player player;
            switch (message.length){
                case 2:
                    sv = ServerLocation.deserialize(message[1]);

                    if(sv == null) {
                        Bukkit.getLogger().info("sv null");
                        player = Bukkit.getPlayer(message[1]);
                        if(player == null) return;

                        ServerLocation location = new ServerLocation(player.getLocation(), GreenTavernAPI.SERVER_NAME);
                        velocity.publish(new String[] {message[0], location.serializeToJSON()});
                        return;
                    }

                    // teleport player to location
                    player = Bukkit.getPlayer(message[0]);
                    if(player == null){
                        playerLocations.put(message[0], sv);
                        return;
                    }
                    Bukkit.getScheduler().runTaskLater(greenTavernAPI, () -> teleport(player, sv), 0L);
                    break;

                case 3:
                    if(!"start_cooldown".equals(message[0])) return;
                    player = Bukkit.getPlayer(message[1]);
                    cantMove.add(player);

                    teleportCD.put(player, 4);
                    ActionBarAPI.addActionBarType(player, "TELEPORT");

                    Bukkit.getScheduler().runTaskTimer(greenTavernAPI, task -> {
                        if(!cantMove.contains(player)){
                            if(getCooldown(player) > -100){
                                teleportCD.put(player, -100);
                            }

                            if(teleportCD.get(player) == -101){
                                ActionBarAPI.removeActionBarType(player, "TELEPORT");
                                task.cancel();
                            }
                        }

                        if(teleportCD.get(player) == 1){
                            velocity.publish(new String[] {message[1], message[2]});
                            ActionBarAPI.removeActionBarType(player, "TELEPORT");
                            task.cancel();
                        }

                        teleportCD.replace(player, teleportCD.get(player) - 1);
                    }, 0L,20L);

                    break;
            }
        });
    }

    @EventHandler
    public void spawn(PlayerSpawnLocationEvent event){
        if(!playerLocations.containsKey(event.getPlayer().getName())) return;
        event.setSpawnLocation(playerLocations.get(event.getPlayer().getName()).getLocation());
        playerLocations.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void move(PlayerMoveEvent event){
        if(!cantMove.contains(event.getPlayer())){
            return;
        }
        if(event.getFrom().distance(event.getTo()) > 0){
            cantMove.remove(event.getPlayer());
        }
    }

    private static List<Entity> removePassengers(Player player){
        List<Entity> result = new ArrayList<>(player.getPassengers());
        for(Entity entity : player.getPassengers()){
            player.removePassenger(entity);
        }
        return result;
    }
    private static void addPassengers(Player player, List<Entity> entities){
        for(Entity entity : entities){
            player.addPassenger(entity);
        }
    }

    private static void teleport(Player player, ServerLocation serverLocation){
        List<Entity> entities = removePassengers(player);
        player.teleport(serverLocation.getLocation());
        addPassengers(player, entities);
    }
    public static int getCooldown(Player player){
        return teleportCD.get(player);
    }
}
