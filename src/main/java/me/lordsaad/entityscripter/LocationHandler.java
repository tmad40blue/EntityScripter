package me.lordsaad.entityscripter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Saad on 28/9/2015.
 */
public class LocationHandler {

    public static String serialize(Location loc) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float pitch = loc.getPitch();
        float yaw = loc.getYaw();
        String world = loc.getWorld().getName();
        return x + ";" + y + ";" + z + ";" + world + ";" + pitch + ";" + yaw;
    }

    public static Location deserialize(String serialized) {
        String[] deserialize = serialized.split(";");
        double x = Double.parseDouble(deserialize[0]);
        double y = Double.parseDouble(deserialize[1]);
        double z = Double.parseDouble(deserialize[2]);
        World world = Bukkit.getWorld(deserialize[3]);
        float pitch = Float.parseFloat(deserialize[4]);
        float yaw = Float.parseFloat(deserialize[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String toString(Location loc) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        String world = loc.getWorld().getName();

        return x + ", " + y + ", " + z;
    }
}
