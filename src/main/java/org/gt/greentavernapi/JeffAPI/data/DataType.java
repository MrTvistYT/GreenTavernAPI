package org.gt.greentavernapi.JeffAPI.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public interface DataType {
    PersistentDataType<byte[], ItemMeta> ITEM_META = new ConfigurationSerializableDataType<>(ItemMeta.class);
    PersistentDataType<byte[], ItemMeta[]> ITEM_META_ARRAY = new ConfigurationSerializableArrayDataType<>(ItemMeta[].class);
    PersistentDataType<byte[], ItemStack> ITEM_STACK = new ConfigurationSerializableDataType<>(ItemStack.class);
    PersistentDataType<byte[], ItemStack[]> ITEM_STACK_ARRAY = new ConfigurationSerializableArrayDataType<>(ItemStack[].class);
    PersistentDataType<byte[], Location> LOCATION = new ConfigurationSerializableDataType<>(Location.class);
    PersistentDataType<byte[], Location[]> LOCATION_ARRAY = new ConfigurationSerializableArrayDataType<>(Location[].class);
    PersistentDataType<byte[], Player> PLAYER = new ConfigurationSerializableDataType<>(Player.class);
    PersistentDataType<byte[], Player[]> PLAYER_ARRAY = new ConfigurationSerializableArrayDataType<>(Player[].class);

    PersistentDataType<byte[], java.util.UUID> UUID = new UuidDataType();
}
