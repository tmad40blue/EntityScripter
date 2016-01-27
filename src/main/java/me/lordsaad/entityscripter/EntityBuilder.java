package me.lordsaad.entityscripter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 1/23/2016.
 */
public class EntityBuilder {

    private EntityType entityType = EntityType.ZOMBIE;
    private String customName = null;
    private boolean customNameVisible = false, noAI = false, silent = false;
    private Location location = null;
    private Entity entity;
    private List<PotionEffect> potionEffects = new ArrayList<>();
    private BiMap<String, Integer> customIntNBT = HashBiMap.create();
    private BiMap<String, String> customStringNBT = HashBiMap.create();
    private File file;


    public EntityBuilder() {
    }

    public EntityBuilder(Entity entity) {
        this.entity = entity;
    }

    public Entity spawn() {
        if (location != null && file != null) {
            Entity entity = location.getWorld().spawnEntity(location, getEntityType());
            inject(entity);
            EntityScripter.mobs.put(entity.getUniqueId(), file);
            return entity;

        } else return null;
    }

    public void inject(Entity entity) {
        if (getCustomName() != null) entity.setCustomName(getCustomName());

        entity.setCustomNameVisible(getCustomNameVisible());

        if (getNoAI()) NBTUtils.addEntityTag(entity, "NoAI", 1);

        if (location != null) if (!entity.getLocation().equals(location)) entity.teleport(location);

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            getPotionEffects().forEach(livingEntity::addPotionEffect);
        }

        for (String nbt : customIntNBT.keySet()) {
            NBTUtils.addEntityTag(entity, nbt, customIntNBT.get(nbt));
        }

        for (String nbt : customStringNBT.keySet()) {
            NBTUtils.addEntityTag(entity, nbt, customStringNBT.get(nbt));
        }
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

    public Entity getEntity() {
        return entity;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void addPotionEffect(PotionEffect potionEffect) {
        this.potionEffects.add(potionEffect);
    }

    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void addCustomNBT(String nbt, String value) {
        customStringNBT.put(nbt, value);
    }

    public void addCustomNBT(String nbt, int value) {
        customIntNBT.put(nbt, value);
    }

    public BiMap<String, String> getCustomStringNBTs() {
        return customStringNBT;
    }

    public BiMap<String, Integer> getCustomIntNBTs() {
        return customIntNBT;
    }
}
