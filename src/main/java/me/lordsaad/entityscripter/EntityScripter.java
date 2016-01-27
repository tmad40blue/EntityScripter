package me.lordsaad.entityscripter;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Saad on 1/23/2016.
 */
public class EntityScripter extends JavaPlugin {

    public static HashMap<UUID, File> mobs = new HashMap<>();
    public static EntityScripter plugin;

    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        getCommand("spawnmob").setExecutor(new CommandSpawn());
        new TickRunnable().runTaskTimer(this, 1, 1);
    }

    public void onDisable() {

    }
}
