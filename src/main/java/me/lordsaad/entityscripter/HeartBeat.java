package me.lordsaad.entityscripter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

/**
 * Created by Saad on 1/25/2016.
 */
public class HeartBeat extends BukkitRunnable {

    public void run() {
        for (World world : Bukkit.getWorlds()) {
            world.getEntities().stream().filter(entity -> EntityScripter.mobs.containsKey(entity.getUniqueId())).forEach(entity -> {
                CodeInterpreter interpreter = new CodeInterpreter(EntityScripter.mobs.get(entity.getUniqueId()));
                EntityBuilder builder = new EntityBuilder(entity, EntityScripter.mobs.get(entity.getUniqueId()));
                interpreter.resolveModule("tick", builder);
                builder.inject(builder.getEntity());
            });
        }

        File dir = new File(EntityScripter.plugin.getDataFolder() + "/mobs/");
        for (File file : dir.listFiles())
            if (file != null) {
                CodeInterpreter code = new CodeInterpreter(file);
                if (code.hasOption("properties")) {
                    EntityBuilder builder = code.create();
                    if (builder != null) {
                        builder.spawn();
                        code.resolveModule("properties", builder);
                        builder.inject(builder.getEntity());
                    }
                }
            }
    }
}
