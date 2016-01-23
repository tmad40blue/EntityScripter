package me.lordsaad.entityscripter;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Saad on 1/23/2016.
 */
public class CodeInterpretter {

    private File file;
    private static BiMap<String, String[]> modules = HashBiMap.create();
    private List<String> lines = new ArrayList<>();
    private String text;

    public CodeInterpretter(File file) {
        this.file = file;
        getLines();
        getModules();
    }

    private void getLines() {
        try {
            try (Stream<String> line = Files.lines(Paths.get(file.toURI()), Charset.defaultCharset())) {
                line.forEachOrdered(lin -> {
                    lines.add(lin);
                    text = text + "\n" + line;
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BiMap<String, String[]> getModules() {
        List<String> heads = lines.stream().filter(line -> line.startsWith("#")).collect(Collectors.toList());

        for (int i = 0; i < heads.size(); i = i + 2) {
            String code = Pattern.quote(heads.get(i)) + "(.*?)" + Pattern.quote(heads.get(i++));
            modules.put(heads.get(i), code.split(";"));
        }

        return modules;
    }

    public static void interpretCode(Location loc) {
        EntityBuilder builder = new EntityBuilder(loc);
        for (String heads : modules.keySet()) {
            for (String line : modules.get(heads)) {
                String[] option = line.split(": ");

                if (heads.equalsIgnoreCase("properties")) {
                    if (option[0].equalsIgnoreCase("set_entity_type")) {
                        builder.setEntityType(EntityType.valueOf(option[1]));
                    }

                    if (option[0].equalsIgnoreCase("set_custom_name")) {
                        builder.setCustomName(option[1]);
                    }

                    if (option[0].equalsIgnoreCase("set_no_ai")) {
                        builder.setNoAI(Boolean.parseBoolean(option[1]));
                    }

                    if (option[0].equalsIgnoreCase("set_custom_name_visible")) {
                        builder.setCustomNameVisible(Boolean.parseBoolean(option[1]));
                    }
                }
            }
        }
        builder.build();
    }
}
