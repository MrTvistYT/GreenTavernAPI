package org.gt.greentavernapi.addons;

import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gt.greentavernapi.apis.ComponentAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnvilReplacement implements Listener {
    List<String> symbolsToRemove = new ArrayList<>();
    public AnvilReplacement(File file){
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            String line = config.getString(key);
            if (line != null) {
                symbolsToRemove.add(line.trim());
            }
        }
    }
    @EventHandler
    public void anvil(PrepareAnvilEvent event){
        ItemStack item = event.getResult();
        if(item == null) return;

        ItemMeta itemMeta = item.getItemMeta();
        String name = ComponentAPI.componentToString(item.displayName());

        for(String ch : symbolsToRemove){
            name = name.replace(ch, "");
        }

        name = name.substring(1, name.length()-1);
        itemMeta.displayName(Component.text(name));
        item.setItemMeta(itemMeta);

        event.setResult(item);
    }
}
