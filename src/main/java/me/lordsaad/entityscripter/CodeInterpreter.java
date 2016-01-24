package me.lordsaad.entityscripter;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public void interpretCode(Location loc) {
        EntityBuilder builder = new EntityBuilder(loc);
        if (yml.contains("properties")) {
            for (String property : yml.getConfigurationSection("properties").getKeys(false)) {

                if (property.equalsIgnoreCase("set_entity_type")) {
                    builder.setEntityType(EntityType.valueOf(yml.getString("properties.set_entity_type").toUpperCase()));
                }

                if (property.equalsIgnoreCase("set_custom_name")) {
                    builder.setEntityType(EntityType.valueOf(yml.getString("properties.set_custom_name").toUpperCase()));
                }

                if (property.equalsIgnoreCase("set_entity_type")) {
                    builder.setEntityType(EntityType.valueOf(yml.getString("properties.set_entity_type").toUpperCase()));
                }

                if (property.equalsIgnoreCase("set_entity_type")) {
                    builder.setEntityType(EntityType.valueOf(yml.getString("properties.set_entity_type").toUpperCase()));
                }

            }
        }
        builder.build();
    }

    public void matcher(String key, Entity entity) {
        if (key.equalsIgnoreCase("set_entity_type")) {
            builder.setEntityType(EntityType.valueOf(yml.getString("properties.set_entity_type").toUpperCase()));
        }

        if (key.equalsIgnoreCase("set_custom_name")) {
            builder.setEntityType(EntityType.valueOf(yml.getString("properties.set_custom_name").toUpperCase()));
        }

        if (key.equalsIgnoreCase("set_entity_type")) {
            builder.setEntityType(EntityType.valueOf(yml.getString("properties.set_entity_type").toUpperCase()));
        }

        if (key.equalsIgnoreCase("set_entity_type")) {
            builder.setEntityType(EntityType.valueOf(yml.getString("properties.set_entity_type").toUpperCase()));
        }
    }
}
