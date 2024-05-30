package org.gt.greentavernapi.addons.teleports;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gt.greentavernapi.GreenTavernAPI;
import org.gt.greentavernapi.apis.teleport.ServerLocation;
import org.gt.greentavernapi.apis.teleport.TeleportAPI;
import org.jetbrains.annotations.NotNull;

public class TeleportCommand implements CommandExecutor{
    GreenTavernAPI gtAPI;
    public TeleportCommand(GreenTavernAPI gtAPI){
        this.gtAPI = gtAPI;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = Bukkit.getPlayer(args[0]);
        if(player == null) return false;

        Bukkit.getLogger().info(player.isDead() + " - isDead");
        Bukkit.getLogger().info(player.isInsideVehicle() + " - isInVehicle");

        player.teleport(new Location(
                Bukkit.getWorld(args[1]),
                Double.parseDouble(args[2]),
                Double.parseDouble(args[3]),
                Double.parseDouble(args[4]),
                Float.parseFloat(args[5]),
                Float.parseFloat(args[6])));
        return true;
    }
}
