package me.lordsaad.entityscripter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saad on 1/23/2016.
 */
public class CodeInterpreter {

    private File file;
    private YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
    private static BiMap<String, List<String>> modules = HashBiMap.create();
    private List<String> lines = new ArrayList<>();
    private String text;

    public CodeInterpreter(File file) {
        this.file = file;
    }

    public void interpretCode() {
        EntityBuilder builder = new EntityBuilder();
        if (yml.contains("properties")) {
            for (String property : yml.getConfigurationSection("properties").getKeys(false)) {
                matcher("properties.", property, builder);
            }
        }
        builder.spawn();
    }

    public void matcher(String prefixPath, String key, EntityBuilder builder) {

        if (key.equalsIgnoreCase("set_entity_type"))
            builder.setEntityType(EntityType.valueOf(yml.getString(prefixPath + "set_entity_type").toUpperCase()));

        if (key.equalsIgnoreCase("set_custom_name"))
            builder.setCustomName(yml.getString(prefixPath + "set_custom_name"));

        if (key.equalsIgnoreCase("set_entity_name_visible"))
            builder.setCustomNameVisible((yml.getBoolean(prefixPath + "set_custom_name_visible")));

        if (key.equalsIgnoreCase("set_no_ai"))
            builder.setNoAI(yml.getBoolean(prefixPath + "set_no_ai"));

        if (key.equalsIgnoreCase("silent")) {
            builder.setSilent(yml.getBoolean(prefixPath + "silent"));
        }

        if (key.equalsIgnoreCase("set_location")) {
            for (String parameter : yml.getConfigurationSection(prefixPath + "set_location").getKeys(false)) {

                double x = 0, y = 0, z = 0;
                World world = null;
                float pitch = 0, yaw = 0;

                if (parameter.equalsIgnoreCase("x")) x = yml.getDouble(prefixPath + "set_location.x");
                if (parameter.equalsIgnoreCase("y")) y = yml.getDouble(prefixPath + "set_location.y");
                if (parameter.equalsIgnoreCase("z")) z = yml.getDouble(prefixPath + "set_location.z");
                if (parameter.equalsIgnoreCase("world")) world = Bukkit.getWorld(yml.getString(prefixPath + "set_location.world"));
                if (parameter.equalsIgnoreCase("pitch")) pitch = (float) yml.getDouble(prefixPath + "set_location.pitch");
                if (parameter.equalsIgnoreCase("yaw")) yaw = (float) yml.getDouble(prefixPath + "set_location.yaw");

                if (world != null) builder.setLocation(new Location(world, x, y, z, pitch, yaw));
            }
        }

        if (key.equalsIgnoreCase("add_potion_effect")) {
            for (String effectType : yml.getConfigurationSection(prefixPath + "add_potion_effect").getKeys(false)) {
                PotionEffectType potionEffectType = PotionEffectType.getByName(effectType);

                for (String parameter : yml.getConfigurationSection(prefixPath + "add_potion_effect." + potionEffectType).getKeys(false)) {

                    int duration = 9999, amplifier = 1;
                    boolean ambient = false, particles = true;

                    if (parameter.equalsIgnoreCase("duration"))
                        duration = yml.getInt(prefixPath + "add_potion_effect." + potionEffectType + ".duration");

                    if (parameter.equalsIgnoreCase("amplifier"))
                        amplifier = yml.getInt(prefixPath + "add_potion_effect." + potionEffectType + ".amplifier");

                    if (parameter.equalsIgnoreCase("ambient"))
                        ambient = yml.getBoolean(prefixPath + "add_potion_effect." + potionEffectType + ".ambient");

                    if (parameter.equalsIgnoreCase("particles"))
                        particles = yml.getBoolean(prefixPath + "add_potion_effect." + potionEffectType + ".particles");

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
}
