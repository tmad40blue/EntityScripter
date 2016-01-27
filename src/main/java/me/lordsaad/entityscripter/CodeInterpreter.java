package me.lordsaad.entityscripter;

import com.darkblade12.particleeffect.ParticleEffect;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;

/**
 * Created by Saad on 1/23/2016.
 */
public class CodeInterpreter {

    private YamlConfiguration yml;
    private File file;

    public CodeInterpreter(File file) {
        this.file = file;
        yml = YamlConfiguration.loadConfiguration(file);
    }

    public EntityBuilder interpretProperties() {
        EntityBuilder builder = new EntityBuilder();
        if (yml.contains("properties")) {
            for (String property : yml.getConfigurationSection("properties").getKeys(false)) {
                matcher("properties.", property, builder);
            }
        }
        return builder;
    }

    public void interpretTicks(EntityBuilder builder) {
        if (builder.getEntity() != null) {
            if (yml.contains("tick")) {
                for (String property : yml.getConfigurationSection("tick").getKeys(false)) {
                    matcher("tick.", property, builder);
                    builder.inject(builder.getEntity());
                    secondaryMatcher("tick.", property, builder.getEntity());
                }
            }
        }
    }

    private void matcher(String prefixPath, String key, EntityBuilder builder) {
        builder.setFile(file);

        if (key.equalsIgnoreCase("set_entity_type"))
            builder.setEntityType(EntityType.valueOf(yml.getString(prefixPath + "set_entity_type").toUpperCase()));

        if (key.equalsIgnoreCase("set_custom_name"))
            builder.setCustomName(yml.getString(prefixPath + "set_custom_name"));

        if (key.equalsIgnoreCase("set_custom_name_visible"))
            builder.setCustomNameVisible((yml.getBoolean(prefixPath + "set_custom_name_visible")));

        if (key.equalsIgnoreCase("set_no_ai"))
            builder.setNoAI(yml.getBoolean(prefixPath + "set_no_ai"));

        if (key.equalsIgnoreCase("set_silent")) {
            builder.setSilent(yml.getBoolean(prefixPath + "set_silent"));
        }

        if (key.equalsIgnoreCase("set_location")) {
            for (String parameter : yml.getConfigurationSection(prefixPath + "set_location").getKeys(false)) {

                double x = 0, y = 0, z = 0;
                World world = null;
                float pitch = 0, yaw = 0;

                if (parameter.equalsIgnoreCase("x")) x = yml.getDouble(prefixPath + "set_location.x");
                if (parameter.equalsIgnoreCase("y")) y = yml.getDouble(prefixPath + "set_location.y");
                if (parameter.equalsIgnoreCase("z")) z = yml.getDouble(prefixPath + "set_location.z");
                if (parameter.equalsIgnoreCase("world"))
                    world = Bukkit.getWorld(yml.getString(prefixPath + "set_location.world"));
                if (parameter.equalsIgnoreCase("pitch"))
                    pitch = (float) yml.getDouble(prefixPath + "set_location.pitch");
                if (parameter.equalsIgnoreCase("yaw")) yaw = (float) yml.getDouble(prefixPath + "set_location.yaw");

                if (world != null) builder.setLocation(new Location(world, x, y, z, pitch, yaw));
            }
        }

        if (key.equalsIgnoreCase("potion_effects")) {
            for (String effectType : yml.getConfigurationSection(prefixPath + "potion_effects").getKeys(false)) {
                PotionEffectType potionEffectType = PotionEffectType.getByName(effectType);

                for (String parameter : yml.getConfigurationSection(prefixPath + "potion_effects." + effectType).getKeys(false)) {

                    int duration = 9999, amplifier = 1;
                    boolean ambient = false, particles = true;

                    if (parameter.equalsIgnoreCase("duration"))
                        duration = yml.getInt(prefixPath + "potion_effects." + potionEffectType + ".duration");

                    if (parameter.equalsIgnoreCase("amplifier"))
                        amplifier = yml.getInt(prefixPath + "potion_effects." + potionEffectType + ".amplifier");

                    if (parameter.equalsIgnoreCase("ambient"))
                        ambient = yml.getBoolean(prefixPath + "potion_effects." + potionEffectType + ".ambient");

                    if (parameter.equalsIgnoreCase("particles"))
                        particles = yml.getBoolean(prefixPath + "potion_effects." + potionEffectType + ".particles");

                    builder.addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier, ambient, particles));
                }
            }
        }

        if (key.equalsIgnoreCase("custom_nbt")) {
            for (String nbt : yml.getConfigurationSection(prefixPath + "custom_nbt").getKeys(false)) {
                if (StringUtils.isNumeric(yml.getString(prefixPath + "custom_nbt" + nbt))) {
                    builder.addCustomNBT(nbt, Integer.parseInt(yml.getString(prefixPath + "custom_nbt" + nbt)));
                } else {
                    builder.addCustomNBT(nbt, yml.getString(prefixPath + "custom_nbt" + nbt));
                }
            }
        }
    }

    private void secondaryMatcher(String prefixPath, String key, Entity entity) {
        if (key.equalsIgnoreCase("particles")) {
            for (String particles : yml.getConfigurationSection(prefixPath + "particles").getKeys(false)) {
                ParticleEffect particleEffect = ParticleEffect.fromName(particles);

                String path = prefixPath + "particles." + particles + ".";
                double x = 0, y = 0, z = 0;
                int count = 10;
                float xd = 0, yd = 0, zd = 0, speed = 0;
                if (yml.contains(path + "x")) x = yml.getDouble(path + "x");
                if (yml.contains(path + "y")) y = yml.getDouble(path + "y");
                if (yml.contains(path + "z")) z = yml.getDouble(path + "z");
                if (yml.contains(path + "xd")) xd = (float) yml.getDouble(path + "xd");
                if (yml.contains(path + "yd")) yd = (float) yml.getDouble(path + "yd");
                if (yml.contains(path + "zd")) zd = (float) yml.getDouble(path + "zd");
                if (yml.contains(path + "speed")) speed = (float) yml.getDouble(path + "speed");
                if (yml.contains(path + "count")) count = yml.getInt(path + "count");
                Location location = new Location(entity.getLocation().getWorld()
                        , entity.getLocation().getX() + x
                        , entity.getLocation().getY() + y
                        , entity.getLocation().getZ() + z);
                particleEffect.display(xd, yd, zd, speed, count, location, 100);
            }
        }
    }
}