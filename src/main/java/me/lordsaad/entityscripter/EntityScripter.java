package me.lordsaad.entityscripter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

/**
 * Created by Saad on 1/23/2016.
 */
public class EntityScripter extends JavaPlugin {

    public static BiMap<UUID, File> mobs = HashBiMap.create();
    public static EntityScripter plugin;

    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        getCommand("spawnmob").setExecutor(new CommandSpawn());
    }

    public void onDisable() {

    }
}
