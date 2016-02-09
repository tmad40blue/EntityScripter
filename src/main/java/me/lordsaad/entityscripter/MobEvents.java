package me.lordsaad.entityscripter;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Created by Saad on 1/27/2016.
 */
public class MobEvents implements Listener {

    @EventHandler
    public void mobDeath(EntityDeathEvent event) {
        if (EntityScripter.mobs.containsKey(event.getEntity().getUniqueId())) {
            CodeInterpreter interpreter = new CodeInterpreter(EntityScripter.mobs.get(event.getEntity().getUniqueId()));
            EntityBuilder builder = new EntityBuilder(event.getEntity(), EntityScripter.mobs.get(event.getEntity().getUniqueId()));
            interpreter.resolveModule("on_death", builder);
            builder.inject(event.getEntity());
        }

        if (EntityScripter.lastDamage.containsKey(event.getEntity().getUniqueId()))
            EntityScripter.lastDamage.remove(event.getEntity().getUniqueId());
        if (EntityScripter.mobs.containsKey(event.getEntity().getUniqueId()))
            EntityScripter.mobs.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (EntityScripter.mobs.containsKey(event.getEntity().getUniqueId())) {
            CodeInterpreter interpreter = new CodeInterpreter(EntityScripter.mobs.get(event.getEntity().getUniqueId()));
            EntityBuilder builder = new EntityBuilder(event.getEntity(), EntityScripter.mobs.get(event.getEntity().getUniqueId()));
            interpreter.resolveModule("receive_damage", builder);
            builder.inject(event.getEntity());

            File f = new File(EntityScripter.plugin.getDataFolder(), "entities.yml");
            if (f.exists()) {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
                List<String> list = yml.getStringList("entities");
                for (int i = 0; i <= list.size() - 1; i++)
                    if (UUID.fromString(list.get(i).split(";")[0]).equals(event.getEntity().getUniqueId()))
                        list.remove(i);
                yml.set("entities", list);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player)
            EntityScripter.lastDamage.put(event.getEntity().getUniqueId(), event.getDamager().getUniqueId());
    }
}
