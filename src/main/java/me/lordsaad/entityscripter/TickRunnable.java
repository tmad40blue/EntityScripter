package me.lordsaad.entityscripter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Saad on 1/25/2016.
 */
public class TickRunnable extends BukkitRunnable {

    public void run() {
        for (World world : Bukkit.getWorlds()) {
            world.getEntities().stream().filter(entity -> EntityScripter.mobs.containsKey(entity.getUniqueId())).forEach(entity -> {
                CodeInterpreter interpreter = new CodeInterpreter(EntityScripter.mobs.get(entity.getUniqueId()));
                EntityBuilder builder = new EntityBuilder(entity);
                interpreter.interpretOptions("tick", builder);
            });
        }
    }
}
