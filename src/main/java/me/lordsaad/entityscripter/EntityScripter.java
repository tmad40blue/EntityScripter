package me.lordsaad.entityscripter;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Saad on 1/23/2016.
 */
public class EntityScripter extends JavaPlugin {

    public static HashMap<UUID, File> mobs = new HashMap<>();
    public static HashMap<UUID, UUID> lastDamage = new HashMap<>();
    public static EntityScripter plugin;

    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        getCommand("spawnmob").setExecutor(new CommandSpawn());
        getServer().getPluginManager().registerEvents(new MobEvents(), this);
        new HeartBeat().runTaskTimer(this, 1, 1);

        File f = new File(getDataFolder() + "tmp.yml");
        if (f.exists()) {
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
            for (String string : yml.getStringList("mobs"))
                mobs.put(UUID.fromString(string.split(";")[0]), new File(getDataFolder() + "/mobs/" + string.split(";")[1]));
        }
    }

    public void onDisable() {
        try {
            File f = new File(getDataFolder() + "tmp.yml");
            f.createNewFile();

            YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
            List<String> list = yml.getStringList("mobs");
            list.addAll(mobs.keySet().stream().map(uuid -> uuid + ";" + mobs.get(uuid).getName()).collect(Collectors.toList()));

            yml.set("mobs", list);
            yml.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
