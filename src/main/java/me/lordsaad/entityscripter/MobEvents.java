package me.lordsaad.entityscripter;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Created by Saad on 1/27/2016.
 */
public class MobEvents implements Listener {

    @EventHandler
    public void mobDeath(EntityDeathEvent event) {
        if (EntityScripter.lastDamage.containsKey(event.getEntity().getUniqueId()))
            EntityScripter.lastDamage.remove(event.getEntity().getUniqueId());
        if (EntityScripter.mobs.containsKey(event.getEntity().getUniqueId()))
            EntityScripter.mobs.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (EntityScripter.mobs.containsKey(event.getEntity().getUniqueId())) {
            CodeInterpreter interpreter = new CodeInterpreter(EntityScripter.mobs.get(event.getEntity().getUniqueId()));
            EntityBuilder builder = new EntityBuilder(event.getEntity());
            interpreter.interpretOptions("receive_damage", builder);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        EntityScripter.lastDamage.put(event.getEntity().getUniqueId(), event.getDamager().getUniqueId());
    }
}
