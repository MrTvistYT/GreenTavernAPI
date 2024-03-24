package org.gt.greentavernapi;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import io.papermc.paper.event.player.ChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Transformation;
import org.gt.greentavernapi.GreenTavernAPI;
import org.gt.greentavernapi.apis.ComponentAPI;
import org.joml.Vector3f;

import java.util.*;

public class ShowablePlugin implements Listener {
    GreenTavernAPI gtAPI;

    Map<Player, TextDisplay> ownerHeart = new HashMap<>();
    Map<Player, List<TextDisplay>> ownerMessage = new HashMap<>();
    Map<Player, Integer> ownerMessageTime = new HashMap<>();

    Map<Player, Player> playerClickOwner = new HashMap<>();
    Map<Player, Long> timeToRemove = new HashMap<>();

    public ShowablePlugin(GreenTavernAPI gtAPI){
        this.gtAPI = gtAPI;
    }

    public void initializeTimer(){
        Bukkit.getScheduler().runTaskTimer(gtAPI, () -> {
            for(Player player : timeToRemove.keySet()){
                if(timeToRemove.get(player) == 0) {
                    player.hideEntity(gtAPI, ownerHeart.get(playerClickOwner.get(player)));
                    timeToRemove.remove(player);
                    playerClickOwner.remove(player);

                    continue;
                }
                timeToRemove.replace(player, timeToRemove.get(player) - 1);
            }

            for(Player player : ownerMessage.keySet()){
                TextDisplay textDisplay1 = ownerMessage.get(player).get(0);
                TextDisplay textDisplay2 = ownerMessage.get(player).get(1);

                TextDisplay toHide = textDisplay1;

                if(ComponentAPI.componentToString(textDisplay1.text()).isEmpty() && !ComponentAPI.componentToString(textDisplay2.text()).isEmpty()){
                    toHide = textDisplay2;
                }
                else if(!ComponentAPI.componentToString(textDisplay1.text()).isEmpty() && ComponentAPI.componentToString(textDisplay2.text()).isEmpty()){
                    toHide = textDisplay1;
                }

                if(ownerMessageTime.get(player) == 0){
                    hideMessage(toHide);
                }
                if(ownerMessageTime.get(player) > -1){
                    ownerMessageTime.replace(player, ownerMessageTime.get(player) - 1);
                }
            }
        }, 20L, 20L);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        TextDisplay hearts = player.getWorld().spawn(player.getLocation(), TextDisplay.class, d -> {
            player.addPassenger(d);

            d.setBackgroundColor(Color.fromARGB(0));
            d.setTransformation(new Transformation(new Vector3f(0f, -0.05f, 0f),
                    d.getTransformation().getLeftRotation(),
                    new Vector3f(0.5f, 0.5f, 0.5f),
                    d.getTransformation().getRightRotation()));

            d.setVisibleByDefault(false);
            d.setBillboard(Display.Billboard.CENTER);
            d.setAlignment(TextDisplay.TextAlignment.LEFT);
            d.text(getHearts(player));
        });

        generateMessages(player);
        ownerHeart.put(player, hearts);
    }
    @EventHandler
    public void playerDie(PlayerDeathEvent event){
        Player player = event.getPlayer();

        ownerHeart.get(player).text(Component.empty());
        ownerMessage.get(player).get(0).text(Component.empty());
        ownerMessage.get(player).get(1).text(Component.empty());
    }

    @EventHandler
    public void playerRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();

        player.addPassenger(ownerHeart.get(player));
        player.addPassenger(ownerMessage.get(player).get(0));
        player.addPassenger(ownerMessage.get(player).get(1));
        ownerHeart.get(player).text(getHearts(player));
    }

    @EventHandler
    public void leave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        ownerHeart.get(player).remove();

        ownerMessage.get(player).get(0).remove();
        ownerMessage.get(player).get(1).remove();

        ownerHeart.remove(player);
        ownerMessage.remove(player);
        ownerMessageTime.remove(player);
    }

    @EventHandler
    public void chat(ChatEvent event){
        createMessage(event.getPlayer(), event.message());
    }
    @EventHandler
    public void interactWithPlayer(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();

        Entity entity = event.getRightClicked();

        if(!(entity instanceof Player)){
            return;
        }

        Player clicked = (Player) entity;

        playerClickOwner.put(player, clicked);
        timeToRemove.put(player, 3L);

        player.showEntity(gtAPI, ownerHeart.get(clicked));
    }

    @EventHandler
    public void damage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player player)){
            return;
        }
        ownerHeart.get(player).text(getHearts(player, event.getDamage()));
    }

    @EventHandler
    public void regen(EntityRegainHealthEvent event){
        if(!(event.getEntity() instanceof Player player)){
            return;
        }
        ownerHeart.get(player).text(getHearts(player, -event.getAmount()));
    }








    private void hideMessage(TextDisplay textDisplay){
        textDisplay.setInterpolationDelay(0);
        textDisplay.setInterpolationDuration(3);

        textDisplay.setTransformation(new Transformation(new Vector3f(0,0,0),
                textDisplay.getTransformation().getLeftRotation(),
                new Vector3f(0,0,0),
                textDisplay.getTransformation().getRightRotation()));
    }
    public String createHealthBar(double currentHp, double maxHp, String fullHeart, String halfHeart, String emptyHeart) {
        int fullHearts = Math.max(0, ((int) (currentHp + 0.5)) / 2);
        int halfHearts = Math.max(0, ((int) (currentHp + 0.5)) % 2);
        int emptyHearts = Math.max(0, ((int) ((maxHp + 0.5) - (int) (currentHp + 0.5))) / 2);
        return fullHeart.repeat(fullHearts) + halfHeart.repeat(halfHearts) + emptyHeart.repeat(emptyHearts);
    }

    public Component createMultiLineHealthBar(double currentHp, double maxHp, int heartsPerLine, String fullHeart, String halfHeart, String emptyHeart) {
        int numberOfLines = (int) Math.ceil(maxHp / (heartsPerLine * 2D));

        String healthBar = createHealthBar(currentHp, maxHp, fullHeart, halfHeart, emptyHeart);
        String[] splitLines = new String[numberOfLines];

        for (int i = 0; i < numberOfLines; i++) {
            int start = i * heartsPerLine;
            int end = Math.min(healthBar.length(), start + heartsPerLine);
            splitLines[i] = healthBar.substring(start, end);
        }

        List<Object> lines = Arrays.asList(splitLines);
        Collections.reverse(lines);

        splitLines = lines.toArray(new String[0]);

        Component multiLineComponent = Component.text("");
        for (String line : splitLines) {
            Component lineComponent = Component.text(line);
            multiLineComponent = multiLineComponent.append(lineComponent).appendNewline();
        }

        return multiLineComponent;
    }
    private Component getHearts(Player owner){
        return createMultiLineHealthBar(owner.getHealth(), owner.getMaxHealth(), 10, "\uE17E", "\uE17F", "\uE180");
    }
    private Component getHearts(Player owner, double damage){
        FontImageWrapper heart = new FontImageWrapper("green_tavern:heart");
        FontImageWrapper halfHeart = new FontImageWrapper("green_tavern:half_heart");
        FontImageWrapper noneHeart = new FontImageWrapper("green_tavern:none_heart");
//        return Component.text(createHealthBar((int)owner.getHealth(), (int)owner.getMaxHealth(), heart.getString(), halfHeart.getString(), noneHeart.getString()));
        return createMultiLineHealthBar(owner.getHealth() - damage, owner.getMaxHealth(), 10, "\uE17E", "\uE17F", "\uE180");
    }


    public void createMessage(Player player, Component text){
        TextDisplay textDisplay1 = ownerMessage.get(player).get(0);
        TextDisplay textDisplay2 = ownerMessage.get(player).get(1);

        if(ComponentAPI.componentToString(textDisplay1.text()).isEmpty() && !ComponentAPI.componentToString(textDisplay2.text()).isEmpty()){
            hide(textDisplay2);
            show(textDisplay1, text);
        }
        else if(!ComponentAPI.componentToString(textDisplay1.text()).isEmpty() && ComponentAPI.componentToString(textDisplay2.text()).isEmpty()){
            hide(textDisplay1);
            show(textDisplay2, text);
        }
        else if(ComponentAPI.componentToString(textDisplay1.text()).isEmpty() && ComponentAPI.componentToString(textDisplay2.text()).isEmpty()){
            show(textDisplay1, text);
        }

        ownerMessageTime.replace(player, 5);
    }


    public void generateMessages(Player player){
        TextDisplay t1 = player.getWorld().spawn(player.getLocation(), TextDisplay.class, d -> {
            player.addPassenger(d);
            d.setTransformation(new Transformation(new Vector3f(0,0,0),
                    d.getTransformation().getLeftRotation(),
                    new Vector3f(0,0,0),
                    d.getTransformation().getRightRotation()));
            d.setBillboard(Display.Billboard.CENTER);
        });
        TextDisplay t2 = player.getWorld().spawn(player.getLocation(), TextDisplay.class, d -> {
            player.addPassenger(d);
            d.setTransformation(new Transformation(new Vector3f(0,0,0),
                    d.getTransformation().getLeftRotation(),
                    new Vector3f(0,0,0),
                    d.getTransformation().getRightRotation()));
            d.setBillboard(Display.Billboard.CENTER);
        });

        List<TextDisplay> messages = new ArrayList<>(){{add(t1);add(t2);}};
        ownerMessage.put(player, messages);
        ownerMessageTime.put(player, -1);
    }


    private void hide(TextDisplay textDisplay){
        textDisplay.text(Component.text(""));
        textDisplay.setTransformation(new Transformation(new Vector3f(0,0,0),
                textDisplay.getTransformation().getLeftRotation(),
                new Vector3f(0,0,0),
                textDisplay.getTransformation().getRightRotation()));
    }
    private void show(TextDisplay textDisplay, Component text){
        textDisplay.text(text);
        textDisplay.setInterpolationDelay(0);
        textDisplay.setInterpolationDuration(3);
        textDisplay.setTransformation(new Transformation(new Vector3f(0,0.7f,0),
                textDisplay.getTransformation().getLeftRotation(),
                new Vector3f(1,1,1),
                textDisplay.getTransformation().getRightRotation()));
    }
}
