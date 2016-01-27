package me.lordsaad.entityscripter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Created by Saad on 1/27/2016.
 */
public class MobEvents implements Listener {

    @EventHandler
    public void mobDeath(EntityDeathEvent event) {
        if (EntityScripter.mobs.containsKey(event.getEntity().getUniqueId())) {
            EntityScripter.mobs.remove(event.getEntity().getUniqueId());
        }
    }
}
