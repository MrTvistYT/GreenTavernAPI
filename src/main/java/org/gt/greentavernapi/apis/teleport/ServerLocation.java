package org.gt.greentavernapi.apis.teleport;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.*;

public class ServerLocation {
    final double x;
    final double y;
    final double z;
    final float yaw;
    final float pitch;
    final String world;
    final String server;

    public ServerLocation(Location location, String server){
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();

        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.world = location.getWorld().getName();
        this.server = server;
    }
    public ServerLocation(double x, double y, double z, float yaw, float pitch, String world, String server){
        this.x = x;
        this.y = y;
        this.z = z;

        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
        this.server = server;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public float getPitch() {
        return pitch;
    }
    public float getYaw() {
        return yaw;
    }

    public World getWorld() {
        if(Bukkit.getWorld(world) == null){
            Bukkit.getLogger().info("World == null");
        }
        return Bukkit.getWorld(world);
    }
    public String getServer() {
        return server;
    }

    public Location getLocation(){
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }

    public byte[] serializeToBytes(){
        ByteArrayOutputStream boss = new ByteArrayOutputStream();

        try(ObjectOutputStream out = new ObjectOutputStream(boss)){
            out.writeObject(this);
            out.flush();
            return boss.toByteArray();
        }
        catch (Exception er) {er.printStackTrace();}
        return null;
    }
    public String serializeToJSON(){
        return new Gson().toJson(this);
    }

    public static ServerLocation deserialize(byte[] bytes){
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        try (ObjectInput in = new ObjectInputStream(bis)) {
            return (ServerLocation) in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public static ServerLocation deserialize(String json){
        return new Gson().fromJson(json, ServerLocation.class);
    }

    @Override
    public String toString() {
        return "x = " + x + "; y = " + y + "; z = " + z + "; yaw = "  + yaw + "; pitch = " + pitch + "; world = " + world + "; server = " + server;
    }
}
