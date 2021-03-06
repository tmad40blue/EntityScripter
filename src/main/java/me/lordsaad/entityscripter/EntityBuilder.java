package me.lordsaad.entityscripter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
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

    public EntityBuilder(Entity entity, File file) {
        if (entity != null) {
            this.entity = entity;
            this.location = entity.getLocation();
            this.customName = entity.getCustomName();
            this.customNameVisible = entity.isCustomNameVisible();
            this.entityType = entity.getType();
            this.file = file;
            if (entity instanceof LivingEntity) {
                this.potionEffects = (List<PotionEffect>) ((LivingEntity) entity).getActivePotionEffects();
            }

            try {
                File f = new File(EntityScripter.plugin.getDataFolder(), "entities.yml");
                if (!f.exists()) f.createNewFile();
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
                List<String> list = yml.getStringList("entities");
                list.add(entity.getUniqueId() + ";" + file.getName());
                yml.set("entities", list);
                yml.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void spawn() {
        if (location != null && file != null && entityType != null) {
            Entity entity = location.getWorld().spawnEntity(location, entityType);
            this.entity = entity;
            inject(entity);
            EntityScripter.mobs.put(entity.getUniqueId(), file);
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).getEquipment().clear();
                ((LivingEntity) entity).getEquipment().setItemInHand(null);
                ((LivingEntity) entity).getEquipment().setChestplate(null);
                ((LivingEntity) entity).getEquipment().setHelmet(null);
                ((LivingEntity) entity).getEquipment().setLeggings(null);
                ((LivingEntity) entity).getEquipment().setBoots(null);
            }
        }
    }

    public void inject(Entity entity) {
        if (entity == null) return;

        entity.setCustomNameVisible(getCustomNameVisible());

        if (getCustomName() != null) entity.setCustomName(getCustomName());
        if (getNoAI()) NBTUtils.addEntityTag(entity, "NoAI", 1);
        if (isSilent()) NBTUtils.addEntityTag(entity, "Silent", 1);
        if (location != null) if (!entity.getLocation().equals(location)) entity.teleport(location);
        if (entity instanceof LivingEntity) getPotionEffects().forEach(((LivingEntity) entity)::addPotionEffect);

        for (String nbt : customIntNBT.keySet()) NBTUtils.addEntityTag(entity, nbt, customIntNBT.get(nbt));
        for (String nbt : customStringNBT.keySet()) NBTUtils.addEntityTag(entity, nbt, customStringNBT.get(nbt));
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
