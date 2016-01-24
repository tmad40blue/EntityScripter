package me.lordsaad.entityscripter;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private static BiMap<String, List<String>> modules = HashBiMap.create();
    private List<String> lines = new ArrayList<>();
    private String text;

    public CodeInterpreter(File file) {
        this.file = file;
        getLines();
        getModules();
    }

    private void getLines() {
        try {
            try (Stream<String> line = Files.lines(Paths.get(file.toURI()), Charset.defaultCharset())) {
                line.forEachOrdered(lin -> lines.add(lin));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        text = Joiner.on("\n").skipNulls().join(lines);
    }

    public BiMap<String, List<String>> getModules() {
        List<String> heads = lines.stream().filter(line -> line.startsWith("#")).collect(Collectors.toList());
        for (String head : heads) {
            lines.remove(head);
        }

        Bukkit.broadcastMessage(Arrays.toString(text.split("#")));
        for (String slice : text.split("#")) {
            if (slice != null && !slice.isEmpty() && !slice.equalsIgnoreCase("end")) {
                String[] subSlices = slice.split("\n", 2);
                if (subSlices[1] != null && !subSlices[1].isEmpty()) {

                    List<String> code = new ArrayList<>();
                    Collections.addAll(code, subSlices[1].split("\n"));

                    modules.put(subSlices[0], code);
                }
            }
        }

        return modules;
    }

    public void interpretCode(Location loc) {
        EntityBuilder builder = new EntityBuilder(loc);
        for (String heads : modules.keySet()) {
            modules.get(heads).stream().filter(line -> line != null).forEach(line -> {
                String[] option = line.split(":\\s");

                if (heads.equalsIgnoreCase("properties")) {
                    if (option[0].equalsIgnoreCase("set_entity_type")) {
                        builder.setEntityType(EntityType.valueOf(option[1].toUpperCase()));
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
            });
        }
        builder.build();
    }
}
