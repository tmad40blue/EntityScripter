package me.lordsaad.entityscripter;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Saad on 1/23/2016.
 */
public class EntityScripter extends JavaPlugin {

    public static EntityScripter plugin;

    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        getCommand("spawnmob").setExecutor(new CommandSpawn());
    }

    public void onDisable() {

    }
}
