package me.lordsaad.entityscripter;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Created by Saad on 1/23/2016.
 */
public class EntityBuilder {

    private EntityType entityType;
    private String customName;
    private boolean customNameVisible;
    private boolean noAI;
    private Location loc;

    public EntityBuilder(Location loc) {
        this.loc = loc;
    }

    public Entity build() {
        Entity entity = loc.getWorld().spawnEntity(loc, getEntityType());
        if (getCustomName() != null) {
            entity.setCustomName(getCustomName());
        }
        return entity;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public boolean getCustomNameVisible() {
        return customNameVisible;
    }

    public void setCustomNameVisible(boolean customNameVisible) {
        this.customNameVisible = customNameVisible;
    }

    public boolean getNoAI() {
        return noAI;
    }

    public void setNoAI(boolean noAI) {
        this.noAI = noAI;
    }
}
