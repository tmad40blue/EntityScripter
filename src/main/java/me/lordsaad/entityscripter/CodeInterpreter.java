package me.lordsaad.entityscripter;

import com.darkblade12.particleeffect.ParticleEffect;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (!yml.contains("properties")) return null;

        for (String property : yml.getConfigurationSection("properties").getKeys(false))
            matcher("properties.", property, builder);

        return builder;
    }

    public void interpretOptions(String path, EntityBuilder builder) {
        path = path + ".";
        if (builder.getEntity() == null) return;
        if (!yml.contains(path)) return;

        for (String property : yml.getConfigurationSection(path).getKeys(false)) {
            matcher(path, property, builder);
            secondaryMatcher(property, path + property, builder.getEntity());
            tertiaryMatcher(path, property, builder.getEntity());
            builder.inject(builder.getEntity());
        }
    }

    private void matcher(String prefixPath, String key, EntityBuilder builder) {
        if (file != builder.getFile())
            builder.setFile(file);

        if (key.equalsIgnoreCase("set_entity_type"))
            builder.setEntityType(EntityType.valueOf(yml.getString(prefixPath + "set_entity_type").toUpperCase()));

        if (key.equalsIgnoreCase("set_custom_name"))
            builder.setCustomName(ChatColor.translateAlternateColorCodes('&', yml.getString(prefixPath + "set_custom_name")));

        if (key.equalsIgnoreCase("set_custom_name_visible"))
            builder.setCustomNameVisible((yml.getBoolean(prefixPath + "set_custom_name_visible")));

        if (key.equalsIgnoreCase("set_no_ai"))
            builder.setNoAI(yml.getBoolean(prefixPath + "set_no_ai"));

        if (key.equalsIgnoreCase("set_silent"))
            builder.setSilent(yml.getBoolean(prefixPath + "set_silent"));

        if (key.equalsIgnoreCase("set_location"))
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

        if (key.equalsIgnoreCase("potion_effects"))
            for (String effectType : yml.getConfigurationSection(prefixPath + "potion_effects").getKeys(false)) {
                PotionEffectType potionEffectType = PotionEffectType.getByName(effectType);

                for (String parameter : yml.getConfigurationSection(prefixPath + "potion_effects." + effectType).getKeys(false)) {

                    int duration = 9999, amplifier = 1;
                    boolean ambient = false, particles = true;

                    if (parameter.equalsIgnoreCase("duration"))
                        duration = yml.getInt(prefixPath + "potion_effects." + effectType + ".duration");
                    if (parameter.equalsIgnoreCase("amplifier"))
                        amplifier = yml.getInt(prefixPath + "potion_effects." + effectType + ".amplifier");
                    if (parameter.equalsIgnoreCase("ambient"))
                        ambient = yml.getBoolean(prefixPath + "potion_effects." + effectType + ".ambient");
                    if (parameter.equalsIgnoreCase("particles"))
                        particles = yml.getBoolean(prefixPath + "potion_effects." + effectType + ".particles");

                    builder.addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier, ambient, particles));
                }
            }

        if (key.equalsIgnoreCase("custom_nbt"))
            for (String nbt : yml.getConfigurationSection(prefixPath + "custom_nbt").getKeys(false))
                if (StringUtils.isNumeric(yml.getString(prefixPath + "custom_nbt" + nbt)))
                    builder.addCustomNBT(nbt, Integer.parseInt(yml.getString(prefixPath + "custom_nbt" + nbt)));
                else builder.addCustomNBT(nbt, yml.getString(prefixPath + "custom_nbt" + nbt));


        if (key.equalsIgnoreCase("send_message"))
            for (String ink : yml.getConfigurationSection(prefixPath + "send_message").getKeys(false)) {
                String to = "@a";
                String msg = null;
                if (ink.equalsIgnoreCase("to")) to = yml.getString(prefixPath + "send_message.to");
                if (ink.equalsIgnoreCase("msg")) msg = yml.getString(prefixPath + "send_message.msg");
                List<UUID> entities = Utils.targetUUIDResolver(to, builder.getEntity());
                for (World world : Bukkit.getWorlds())
                    for (Entity entity : world.getEntities())
                        if (entities.contains(entity.getUniqueId()))
                            if (msg != null)
                                entity.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
    }

    private void tertiaryMatcher(String prefixPath, String key, Entity entity) {
        if (entity == null) return;

        if (key.equalsIgnoreCase("particles"))
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

        if (key.equalsIgnoreCase("run_command"))
            for (String ink : yml.getConfigurationSection(prefixPath + "run_command").getKeys(false)) {
                List<UUID> senders = new ArrayList<>();
                String cmd = null;

                if (ink.contains("sender"))
                    if (yml.getString(prefixPath + "run_command.sender").split("=").length > 0)
                        senders = Utils.targetUUIDResolver(yml.getString(prefixPath + "run_command.sender").split("=")[1], entity);

                if (ink.equalsIgnoreCase("cmd")) cmd = yml.getString(prefixPath + "run_command.cmd");

                if (!senders.isEmpty())
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        if (senders.contains(players.getUniqueId()))
                            Bukkit.dispatchCommand(players, cmd);
                    }
                else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
    }

    public void secondaryMatcher(String property, String path, Entity entity) {
        if (entity == null) return;

        if (entity.getType() == EntityType.ZOMBIE)
            if (property.equalsIgnoreCase("set_villager")) ((Zombie) entity).setBaby(yml.getBoolean(path));

        if (entity instanceof Ageable) {
            Ageable ageable = (Ageable) entity;
            if (property.equalsIgnoreCase("set_age")) ageable.setAge(yml.getInt(path));
            if (property.equalsIgnoreCase("set_age_lock")) ageable.setAgeLock(yml.getBoolean(path));
            if (property.equalsIgnoreCase("set_breed")) ageable.setBreed(yml.getBoolean(path));
            if (property.equalsIgnoreCase("set_baby")) if (yml.getBoolean(path)) ageable.setBaby();
            else ageable.setAdult();
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (property.equalsIgnoreCase("set_health")) livingEntity.setHealth(yml.getDouble(path));
            if (property.equalsIgnoreCase("set_despawnable")) livingEntity.setRemoveWhenFarAway(yml.getBoolean(path));
            if (property.equalsIgnoreCase("set_passenger_of")) {
                File f = new File(EntityScripter.plugin.getDataFolder(), "/mobs/" + yml.getString(path) + ".txt");
                if (f.exists()) {
                    CodeInterpreter interpreter = new CodeInterpreter(f);
                    EntityBuilder builder2 = interpreter.interpretProperties();
                    builder2.spawn();
                    builder2.getEntity().setPassenger(livingEntity);
                }
            }
            if (property.equalsIgnoreCase("set_equipment")) {
                Random random = new Random();
                for (String stuff : yml.getConfigurationSection(path).getKeys(false)) {
                    ItemStack item = new ItemStack(Material.AIR);
                    ItemMeta meta = item.getItemMeta();
                    int r = 100;
                    for (String properties : yml.getConfigurationSection(path + "." + stuff).getKeys(false)) {
                        if (properties.equalsIgnoreCase("material")) {
                            Bukkit.broadcastMessage(yml.getString(path + "." + stuff + "." + properties));
                            item.setType(Material.valueOf(yml.getString(path + "." + stuff + "." + properties).toUpperCase()));
                        }
                        if (properties.equalsIgnoreCase("durability"))
                            item.setDurability((short) yml.getInt(path + "." + stuff + "." + properties));
                        if (properties.equalsIgnoreCase("name"))
                            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', yml.getString(path + "." + stuff + "." + properties)));
                        if (properties.equalsIgnoreCase("lore"))
                            meta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', yml.getString(path + "." + stuff + "." + properties)).split("\n")));
                        if (properties.equalsIgnoreCase("chance_of_dropping"))
                            if (stuff.equalsIgnoreCase("boots"))
                                livingEntity.getEquipment().setBootsDropChance(yml.getInt(path + "." + stuff + "." + properties) / 100);
                            else if (stuff.equalsIgnoreCase("leggings"))
                                livingEntity.getEquipment().setLeggingsDropChance(yml.getInt(path + "." + stuff + "." + properties) / 100);
                            else if (stuff.equalsIgnoreCase("chestplate"))
                                livingEntity.getEquipment().setChestplateDropChance(yml.getInt(path + "." + stuff + "." + properties) / 100);
                            else if (stuff.equalsIgnoreCase("helmet"))
                                livingEntity.getEquipment().setHelmetDropChance(yml.getInt(path + "." + stuff + "." + properties) / 100);
                            else if (stuff.equalsIgnoreCase("hand"))
                                livingEntity.getEquipment().setItemInHandDropChance(yml.getInt(path + "." + stuff + "." + properties) / 100);
                        if (properties.equalsIgnoreCase("chance_of_appearing"))
                            r = yml.getInt(path + "." + stuff + "." + properties);
                    }

                    if (random.nextInt(100) <= r) {
                        item.setItemMeta(meta);
                        if (stuff.equalsIgnoreCase("boots"))
                            livingEntity.getEquipment().setBoots(item);
                        else if (stuff.equalsIgnoreCase("leggings"))
                            livingEntity.getEquipment().setLeggings(item);
                        else if (stuff.equalsIgnoreCase("chestplate"))
                            livingEntity.getEquipment().setChestplate(item);
                        else if (stuff.equalsIgnoreCase("helmet"))
                            livingEntity.getEquipment().setHelmet(item);
                        else if (stuff.equalsIgnoreCase("hand"))
                            livingEntity.getEquipment().setItemInHand(item);
                    }
                }
            }
        }
    }

    public Object injectMatches(Object obj) {
        if (obj instanceof String) {
            String string = (String) obj;
            if (string.contains("%randomize%")) {
                Random r = new Random();
                List<Integer> ranges = new ArrayList<>();
                List<String> matches = new ArrayList<>();
                Pattern pattern = Pattern.compile("[(.*?)]");
                Matcher matcher = pattern.matcher(string);
                while (matcher.find()) {
                    matches.add(matcher.group(1));
                }
                if (!matches.isEmpty()) {
                    String[] ranges1 = matches.get(0).split(",");
                    for (String range : ranges1) {
                        if (StringUtils.isNumeric(range)) {
                            ranges.add(Integer.parseInt(range));
                        }
                    }
                }
                if (ranges.size() > 2) {
                    return string.replace("%randomize", r.nextInt((ranges.get(0) - ranges.get(1)) + 1) + ranges.get(1) + "");
                } else return obj;
            } else return obj;
        } else return obj;
    }

    public Object injectMatches(Object obj, EntityBuilder builder) {
        if (obj instanceof String) {
            String string = (String) obj;

            if (string.contains("%location%"))
                return string.replace("%location%", LocationHandler.toString(builder.getLocation()));

            else if (string.contains("%location_x%"))
                return string.replace("%location_x%", builder.getLocation().getBlockX() + "");

            else if (string.contains("%location_y%"))
                return string.replace("%location_y%", builder.getLocation().getBlockY() + "");

            else if (string.contains("%location_z%"))
                return string.replace("%location_z%", builder.getLocation().getBlockZ() + "");

            else if (string.contains("%location_world%"))
                return string.replace("%location_world%", builder.getLocation().getWorld().getName() + "");

            else if (string.contains("%entity_type%"))
                return string.replace("%entity_type%", builder.getEntityType().name());

            else if (string.contains("%name%"))
                return string.replace("%name%", builder.getCustomName());

            else if (string.contains("%entity_type%"))
                return string.replace("%entity_type%", builder.getEntityType().name());
        }
        return obj;
    }
}