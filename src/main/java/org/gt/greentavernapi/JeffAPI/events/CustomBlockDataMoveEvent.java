package org.gt.greentavernapi.JeffAPI.events;

import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class CustomBlockDataMoveEvent extends CustomBlockDataEvent {

    private final @NotNull Block blockTo;

    public CustomBlockDataMoveEvent(@NotNull Plugin plugin, @NotNull Block blockFrom, @NotNull Block blockTo, @NotNull Event bukkitEvent) {
        super(plugin, blockFrom, bukkitEvent);
        this.blockTo = blockTo;
    }

    public @NotNull Block getBlockTo() {
        return blockTo;
    }

}
