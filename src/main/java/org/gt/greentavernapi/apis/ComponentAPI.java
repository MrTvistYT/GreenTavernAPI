package org.gt.greentavernapi.apis;

import com.google.gson.Gson;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public class ComponentAPI {
    private static final int BORDER_OFFSET = 8;
    private static final Map<Character, Integer> offsetMap = new HashMap<>();
    private static File translateFile;

    enum Align{
        LEFT,
        CENTER,
        RIGHT
    }


    public ComponentAPI(File translateFile, File offsetFile){
        ComponentAPI.translateFile = translateFile;
        new FontImageWrapper("").getWidth();
        try {
            Yaml yaml = new Yaml();
            FileInputStream inputStream = new FileInputStream(offsetFile);
            Map<String, List<Map<String, Object>>> data = yaml.load(inputStream);

            if(data == null || !data.containsKey("offset")){
                Bukkit.getLogger().warning("WRONG FORMAT OF YAML");
                return;
            }

            List<Map<String, Object>> offsetList = data.get("offset");

            for (Map<String, Object> entry : offsetList) {
                if (entry.containsKey("char") && entry.containsKey("offset")) {
                    char character = ((String) entry.get("char")).charAt(0);
                    int offsetInt = (int) entry.get("offset");
                    offsetMap.put(character, offsetInt);
                }
            }

        } catch (IOException er){
            er.printStackTrace();
        }
    }


    public static Component title(Align align, String imageIA, String title){
        FontImageWrapper image = new FontImageWrapper(imageIA);

        return space(-BORDER_OFFSET)
                .append(image(image))
                .append(space(-getAlignSpace(align, image, title)))
                .append(text(title, TextColor.color(0,0,0)));
    }


    public static Component text(String text){
        return Component.text(text).color(TextColor.color(0,0,0)).font(Key.key("minecraft:default"));
    }
    public static Component text(String text, TextColor color){
        return Component.text(text).color(color).font(Key.key("minecraft:default"));
    }
    public static Component image(FontImageWrapper image){
        return Component.text(image.getString()).color(TextColor.color(255,255,255)).font(Key.key("minecraft:default"));
    }
    public static Component space(int space){
        return Component.translatable("space." + space).font(Key.key("space:default"));
    }


    private static int getOffset(String string){
        int result = 0;
        for(char ch : string.toCharArray()){
            result += offsetMap.getOrDefault(ch, 9);
        }

        return result;
    }










    public static String translateToRu(Material material){
        return getTranslatedMaterialMap(translateFile).get(material);
    }
    public static String translateToRu(EntityType entityType){
        return getTranslatedEntityTypeMap(translateFile).get(entityType);
    }





    private static int getAlignSpace(Align align, FontImageWrapper image, String title){
        int text_size = getOffset(title);
        int space = 0;

        switch (align){
            case LEFT:
                space = (image.getWidth() + 1) - 8;
                break;
            case CENTER:
                space = (((image.getWidth() + 1) - text_size) / 2) + text_size;
                break;
            case RIGHT:
                space = BORDER_OFFSET + text_size;
                break;
        }
        return space;
    }




    public static String componentToString(Component component){
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

















    private static Map<Material, String> getTranslatedMaterialMap(final File minecraftTranslationFile) {
        final Gson gson = new Gson();
        final Map<Material, String> map = new HashMap<>();
        for (final Material mat : Material.values()) {
            map.put(mat, getNiceMaterialName(mat));
        }
        try {
            final Map<?, ?> tmpMap = gson.fromJson(new FileReader(minecraftTranslationFile), Map.class);
            for (final Material mat : Material.values()) {
                map.put(mat, (String) tmpMap.get(getMinecraftNamespacedName(mat)));
            }
        } catch (final FileNotFoundException ignored) {
            //ignored.printStackTrace();
        }
        return map;
    }
    private static Map<EntityType, String> getTranslatedEntityTypeMap(final File minecraftTranslationFile) {
        final Gson gson = new Gson();
        final Map<EntityType, String> map = new HashMap<>();
        for (final EntityType type : EntityType.values()) {
            map.put(type, getNiceEntityName(type));
        }
        try {
            final Map<?, ?> tmpMap = gson.fromJson(new FileReader(minecraftTranslationFile), Map.class);
            for (final EntityType type : EntityType.values()) {
                map.put(type, (String) tmpMap.get(getMinecraftNamespacedName(type)));
            }
        } catch (final FileNotFoundException ignored) {
            //ignored.printStackTrace();
        }
        return map;
    }

    private static String getNiceMaterialName(final Material mat) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<String> iterator = Arrays.stream(mat.name().split("_")).iterator();
        while (iterator.hasNext()) {
            builder.append(upperCaseFirstLetterOnly(iterator.next()));
            if (iterator.hasNext()) builder.append(" ");
        }
        return builder.toString();
    }
    private static String getNiceEntityName(final EntityType type) {
        final StringBuilder builder = new StringBuilder();
        final Iterator<String> iterator = Arrays.stream(type.name().split("_")).iterator();
        while (iterator.hasNext()) {
            builder.append(upperCaseFirstLetterOnly(iterator.next()));
            if (iterator.hasNext()) builder.append(" ");
        }
        return builder.toString();
    }
    private static String getMinecraftNamespacedName(final Material mat) {
        if (mat.isBlock()) {
            return "block.minecraft." + mat.name().toLowerCase(Locale.ROOT);
        }
        else {
            return "item.minecraft." + mat.name().toLowerCase(Locale.ROOT);
        }
    }
    private static String getMinecraftNamespacedName(final EntityType entityType) {
        return "entity.minecraft." + entityType.name().toLowerCase(Locale.ROOT);
    }
    private static String upperCaseFirstLetterOnly(final String word) {
        return upperCaseFirstLetter(word.toLowerCase(Locale.ROOT));
    }

    private static String upperCaseFirstLetter(final String word) {
        if (word.isEmpty()) return word;
        if (word.length() == 1) return word.toUpperCase(Locale.ROOT);
        return word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1);
    }
}
