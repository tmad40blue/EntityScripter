package me.lordsaad.entityscripter;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

/**
 * Created by Saad on 1/24/2016.
 */
public class NBTUtils {

    public static void addEntityTag(Entity entity, String tag, int value) {
        net.minecraft.server.v1_8_R3.Entity nmsEnt = ((CraftEntity) entity).getHandle();
        NBTTagCompound xtag = nmsEnt.getNBTTag();

        if (xtag == null) {
            xtag = new NBTTagCompound();
        }

        nmsEnt.c(xtag);
        xtag.setInt(tag, value);
        nmsEnt.f(xtag);
    }

    public static void addEntityTag(Entity entity, String tag, String value) {
        net.minecraft.server.v1_8_R3.Entity nmsEnt = ((CraftEntity) entity).getHandle();
        NBTTagCompound xtag = nmsEnt.getNBTTag();

        if (xtag == null) {
            xtag = new NBTTagCompound();
        }

        nmsEnt.c(xtag);
        xtag.setString(tag, value);
        nmsEnt.f(xtag);
    }
}
