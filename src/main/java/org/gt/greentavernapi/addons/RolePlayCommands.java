package org.gt.greentavernapi.addons;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.gt.greentavernapi.apis.ComponentAPI;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class RolePlayCommands implements CommandExecutor {
    FileConfiguration config;
    public RolePlayCommands(FileConfiguration config){
        this.config = config;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)){
            return false;
        }

        Component result = Component.empty();
        String message = String.join(" ", args);
        switch (label){
            case "me":
                result = ComponentAPI.miniMessage(config.getString("rp_commands.me")
                        .replace("{player}", player.getName())
                        .replace("{message}", message));
                break;
            case "b":
                result = ComponentAPI.miniMessage(config.getString("rp_commands.b")
                        .replace("{player}", player.getName())
                        .replace("{message}", message));
                break;
            case "do":
                result = ComponentAPI.miniMessage(config.getString("rp_commands.do")
                        .replace("{player}", player.getName())
                        .replace("{message}", message));
                break;
            case "todo":
                if(message.split(" ").length != 2){
                    player.sendMessage(Component.text("Неверный формат! Введите в формет /todo message*action"));
                    return false;
                }
                result = ComponentAPI.miniMessage(config.getString("rp_commands.todo")
                        .replace("{player}", player.getName())
                        .replace("{message}", message.split("\\*")[0])
                        .replace("{action}", message.split("\\*")[1]));
                break;

            case "try":
                result = ComponentAPI.miniMessage(config.getString("rp_commands.try.msg")
                        .replace("{player}", player.getName())
                        .replace("{message}", message)
                        + (config.getString("rp_commands.try." + new Random().nextBoolean())));
                break;

        }

        for(Player players : player.getWorld().getNearbyPlayers(player.getLocation(), 30)){
            players.sendMessage(result);
        }

        return false;
    }
}
