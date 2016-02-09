package me.lordsaad.entityscripter;

import com.darkblade12.particleeffect.ParticleEffect;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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

    public EntityBuilder create() {
        EntityBuilder builder = new EntityBuilder();
        if (file != builder.getFile())
            builder.setFile(file);
        if (!yml.contains("properties")) return null;

        matcher("properties.", builder);

        if (builder.getLocation() != null)
            return builder;
        else return null;
    }

    public void resolveModule(String module, EntityBuilder builder) {
        if (!yml.contains(module)) return;
        if (builder == null) return;
        if (builder.getLocation() == null) return;
        if (builder.getEntity() == null) return;

        module += ".";
        matcher(module, builder);
        secondaryMatcher(module, builder.getEntity());
        tertiaryMatcher(module, builder);
    }

    public boolean hasOption(String option) {
        return yml.contains(option);
    }

    private void matcher(String path, EntityBuilder builder) {
        if (yml.contains(path + "set_entity_type"))
            builder.setEntityType(EntityType.valueOf(yml.getString(path + "set_entity_type").toUpperCase()));

        if (yml.contains(path + "set_custom_name"))
            builder.setCustomName(ChatColor.translateAlternateColorCodes('&', yml.getString(path + "set_custom_name")));

        if (yml.contains(path + "set_custom_name_visible"))
            builder.setCustomNameVisible((yml.getBoolean(path + "set_custom_name_visible")));

        if (yml.contains(path + "set_no_ai"))
            builder.setNoAI(yml.getBoolean(path + "set_no_ai"));

        if (yml.contains(path + "set_silent"))
            builder.setSilent(yml.getBoolean(path + "set_silent"));

        if (yml.contains(path + "custom_nbt"))
            for (String nbt : yml.getConfigurationSection(path + "custom_nbt").getKeys(false))
                if (StringUtils.isNumeric(yml.getString(path + "custom_nbt." + nbt)))
                    builder.addCustomNBT(nbt, Integer.parseInt(yml.getString(path + "custom_nbt." + nbt)));
                else builder.addCustomNBT(nbt, yml.getString(path + "custom_nbt." + nbt));

        if (yml.contains(path + "set_location")) {
            double x = 0, y = 0, z = 0;
            World world = null;
            float pitch = 0, yaw = 0;

            if (yml.contains(path + "set_location.x")) x = yml.getDouble(path + "set_location.x");
            if (yml.contains(path + "set_location.y")) y = yml.getDouble(path + "set_location.y");
            if (yml.contains(path + "set_location.z")) z = yml.getDouble(path + "set_location.z");
            if (yml.contains(path + "set_location.world"))
                world = Bukkit.getWorld(yml.getString(path + "set_location.world"));
            if (yml.contains(path + "set_location.pitch" + pitch))
                pitch = (float) yml.getDouble(path + "set_location.pitch");
            if (yml.contains(path + "set_location.yaw")) yaw = (float) yml.getDouble(path + "set_location.yaw");

            if (world != null) builder.setLocation(new Location(world, x, y, z, pitch, yaw));

        } else if (yml.contains(path + "spawn_naturally")) {

            double chance = 10;
            boolean highestBlock = false;
            List<Biome> biomes = new ArrayList<>();
            List<Material> blacklist = new ArrayList<>();
            List<Material> whitelist = new ArrayList<>();
            List<String> worlds = new ArrayList<>();

            String path1 = path + "spawn_naturally.";

            if (yml.contains(path1 + "biomes"))
                biomes.addAll(yml.getStringList(path1 + "biomes").stream().filter(biome -> Biome.valueOf(biome) != null).map(Biome::valueOf).collect(Collectors.toList()));
            else if (yml.contains(path1 + "biome"))
                if (Biome.valueOf(yml.getString(path1 + "biome")) != null)
                    biomes.add(Biome.valueOf(yml.getString(path1 + "biome")));
            if (yml.contains(path1 + "set_on_highest_block"))
                highestBlock = yml.getBoolean(path1 + "set_on_highest_block");
            if (yml.contains(path1 + "chance")) chance = yml.getDouble(path1 + "chance");
            if (yml.contains(path1 + "whitelist_blocks"))
                whitelist.addAll(yml.getStringList(path1 + "whitelist_blocks").stream().filter(material -> Material.valueOf(material) != null).map(Material::valueOf).collect(Collectors.toList()));
            else if (yml.contains(path1 + "blacklist_blocks"))
                blacklist.addAll(yml.getStringList(path1 + "blacklist_blocks").stream().filter(material -> Material.valueOf(material) != null).map(Material::valueOf).collect(Collectors.toList()));
            if (yml.contains(path1 + "worlds"))
                for (String world : yml.getStringList("worlds"))
                    worlds.addAll(Bukkit.getWorlds().stream().filter(worlds1 -> worlds1.getName().equals(world)).map(worlds1 -> world).collect(Collectors.toList()));
            else if (yml.contains(path1 + "world"))
                worlds.addAll(Bukkit.getWorlds().stream().filter(worlds1 -> worlds1.getName().equals(yml.getString(path1 + "world"))).map(worlds1 -> yml.getString(path1 + "world")).collect(Collectors.toList()));

            if (!worlds.isEmpty() && Bukkit.getOnlinePlayers().size() > 0)
                for (Player p : Bukkit.getOnlinePlayers())
                    builder.setLocation(new SpawnHandler(worlds, biomes, whitelist, blacklist, chance, highestBlock).makeNewLocation(p.getLocation()));
        }

    }

    public void secondaryMatcher(String path, Entity entity) {
        if (entity == null) return;

        if (entity.getType() == EntityType.ZOMBIE) {
            if (yml.contains(path + "set_villager"))
                ((Zombie) entity).setVillager(yml.getBoolean(path + "set_villager"));
            else ((Zombie) entity).setVillager(false);
            if (yml.contains(path + "set_baby"))
                if (yml.getBoolean(path + "set_baby")) ((Zombie) entity).setBaby(true);
                else ((Zombie) entity).setBaby(false);
        }

        if (entity instanceof Ageable) {
            Ageable ageable = (Ageable) entity;
            if (yml.contains(path + "set_age")) ageable.setAge(yml.getInt(path + "set_age"));
            if (yml.contains(path + "set_age_lock")) ageable.setAgeLock(yml.getBoolean(path + "set_age_lock"));
            if (yml.contains(path + "set_breed")) ageable.setBreed(yml.getBoolean(path + "set_breed"));
            if (yml.contains(path + "set_baby")) if (yml.getBoolean(path + "set_baby")) ageable.setBaby();
            else ageable.setAdult();
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            if (yml.contains(path + "set_health")) livingEntity.setHealth(yml.getDouble(path + "set_health"));
            if (yml.contains(path + "set_despawnable"))
                livingEntity.setRemoveWhenFarAway(yml.getBoolean(path + "set_despawnable"));
            if (yml.contains(path + "set_passenger_of")) {
                File f = new File(EntityScripter.plugin.getDataFolder(), "/mobs/" + yml.getString(path + "set_passenger_of") + ".txt");
                if (f.exists()) {
                    CodeInterpreter interpreter = new CodeInterpreter(f);
                    EntityBuilder builder2 = interpreter.create();
                    builder2.spawn();
                    builder2.getEntity().setPassenger(livingEntity);
                }
            }

            if (yml.contains(path + "set_equipment")) {
                ((LivingEntity) entity).getEquipment().clear();

                for (String equipment : yml.getConfigurationSection(path + "set_equipment").getKeys(false)) {
                    Random random = new Random();
                    ItemStack item = new ItemStack(Material.AIR);
                    ItemMeta meta;
                    int appear = 100;
                    int chance = 1;
                    String path1 = path + "set_equipment." + equipment + ".";
                    if (yml.contains(path1 + "material"))
                        item.setType(Material.valueOf(yml.getString(path1 + "material").toUpperCase()));
                    if (yml.contains(path1 + "durability"))
                        item.setDurability((short) yml.getInt(path1 + "durability"));
                    if (yml.contains(path1 + "chance_of_dropping"))
                        chance = yml.getInt(path1 + "chance_of_dropping") / 100;
                    if (yml.contains(path1 + "chance_of_appearing"))
                        appear = yml.getInt(path1 + "chance_of_appearing");

                    meta = item.getItemMeta();
                    if (yml.contains(path1 + "name"))
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', yml.getString(path1 + "name")));
                    if (yml.contains(path1 + "lore"))
                        meta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', yml.getString(path1 + "lore")).split("\n")));

                    if (Utils.getEquipmentType(item.getType()).equals("boots"))
                        livingEntity.getEquipment().setBootsDropChance(chance);
                    else if (Utils.getEquipmentType(item.getType()).equals("leggings"))
                        livingEntity.getEquipment().setLeggingsDropChance(chance);
                    else if (Utils.getEquipmentType(item.getType()).equals("chestplate"))
                        livingEntity.getEquipment().setChestplateDropChance(chance);
                    else if (Utils.getEquipmentType(item.getType()).equals("helmet"))
                        livingEntity.getEquipment().setHelmetDropChance(chance);
                    else if (Utils.getEquipmentType(item.getType()).equals("hand"))
                        livingEntity.getEquipment().setItemInHandDropChance(chance);

                    if (random.nextInt(100) <= appear) {
                        item.setItemMeta(meta);
                        if (equipment.equalsIgnoreCase("boots"))
                            livingEntity.getEquipment().setBoots(item);
                        else if (equipment.equalsIgnoreCase("leggings"))
                            livingEntity.getEquipment().setLeggings(item);
                        else if (equipment.equalsIgnoreCase("chestplate"))
                            livingEntity.getEquipment().setChestplate(item);
                        else if (equipment.equalsIgnoreCase("helmet"))
                            livingEntity.getEquipment().setHelmet(item);
                        else if (equipment.equalsIgnoreCase("hand"))
                            livingEntity.getEquipment().setItemInHand(item);
                    }
                }
            }
        }
    }

    private void tertiaryMatcher(String path, EntityBuilder builder) {
        if (builder.getEntity() == null) return;

        if (yml.contains(path + "particles"))
            for (String particles : yml.getConfigurationSection(path + "particles").getKeys(false)) {
                ParticleEffect particleEffect = ParticleEffect.fromName(particles);
                if (particleEffect != null) {
                    String path1 = path + "particles." + particles + ".";
                    double x = 0, y = 0, z = 0;
                    int count = 10;
                    float xd = 0, yd = 0, zd = 0, speed = 0;
                    if (yml.contains(path1 + "x")) x = (double) injectMatches(yml.get(path1 + "x"), builder);
                    if (yml.contains(path1 + "y")) y = (double) injectMatches(yml.get(path1 + "y"), builder);
                    if (yml.contains(path1 + "z")) z = (double) injectMatches(yml.get(path1 + "z"), builder);
                    if (yml.contains(path1 + "xd"))
                        xd = ((Double) injectMatches(yml.get(path1 + "xd"), builder)).floatValue();
                    if (yml.contains(path1 + "yd"))
                        yd = ((Double) injectMatches(yml.get(path1 + "yd"), builder)).floatValue();
                    if (yml.contains(path1 + "zd"))
                        zd = ((Double) injectMatches(yml.get(path1 + "zd"), builder)).floatValue();
                    if (yml.contains(path1 + "speed"))
                        speed = ((Double) injectMatches(yml.get(path1 + "speed"), builder)).floatValue();
                    if (yml.contains(path1 + "count")) count = (int) injectMatches(yml.get(path1 + "count"), builder);
                    Location location = new Location(builder.getEntity().getLocation().getWorld()
                            , builder.getEntity().getLocation().getX() + x
                            , builder.getEntity().getLocation().getY() + y
                            , builder.getEntity().getLocation().getZ() + z);
                    particleEffect.display(xd, yd, zd, speed, count, location, 100);
                }
            }

        if (yml.contains(path + "potion_effects"))
            for (String effectType : yml.getConfigurationSection(path + "potion_effects").getKeys(false)) {
                PotionEffectType potionEffectType = PotionEffectType.getByName(effectType);
                if (potionEffectType != null) {
                    String path1 = path + "potion_effects." + effectType + ".";
                    int duration = 9999, amplifier = 1;
                    boolean ambient = false, particles = true;

                    if (yml.contains(path1 + "duration"))
                        duration = (int) injectMatches(yml.get(path1 + "duration"), builder);
                    if (yml.contains(path1 + "amplifier"))
                        amplifier = (int) injectMatches(yml.get(path1 + "amplifier"), builder);
                    if (yml.contains(path1 + "ambient"))
                        ambient = (boolean) injectMatches(yml.get(path1 + "ambient"), builder);
                    if (yml.contains(path1 + "particles"))
                        particles = (boolean) injectMatches(yml.get(path1 + "particles"), builder);

                    builder.addPotionEffect(new PotionEffect(potionEffectType, duration, amplifier, ambient, particles));
                }
            }

        if (yml.contains(path + "run_command")) {
            List<UUID> senders = new ArrayList<>();
            String cmd = null;

            if (yml.contains(path + "run_command.sender"))
                if (yml.getString(path + "run_command.sender").split("=").length > 0)
                    senders = Utils.targetUUIDResolver(yml.getString(path + "run_command.sender").split("=")[1], builder.getEntity());

            if (yml.contains(path + "cmd")) cmd = yml.getString(path + "run_command.cmd");

            if (!senders.isEmpty())
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (senders.contains(players.getUniqueId()))
                        Bukkit.dispatchCommand(players, String.valueOf(injectMatches(cmd, builder)));
                }
            else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.valueOf(injectMatches(cmd, builder)));
        }

        if (yml.contains(path + "send_message")) {
            String to = "@a";
            String msg = null;
            if (yml.contains(path + "send_message.to")) to = yml.getString(path + "send_message.to");
            if (yml.contains(path + "send_message.msg")) msg = yml.getString(path + "send_message.msg");
            List<UUID> entities = Utils.targetUUIDResolver(to, builder.getEntity());
            for (World world : Bukkit.getWorlds())
                for (Entity entity : world.getEntities())
                    if (entities.contains(entity.getUniqueId()))
                        if (msg != null)
                            entity.sendMessage((String) injectMatches(ChatColor.translateAlternateColorCodes('&', msg), builder));
        }
    }

    public Object injectMatches(Object obj) {
        if (obj instanceof String) {
            String string = (String) obj;
            if (string.contains("%randomize[")) {
                Random r = new Random();

                Object[] objs;
                String extracted = string.substring(string.indexOf("[") + 1, string.indexOf("]"));

                if (!extracted.equals("") && extracted.contains(",")) {
                    objs = extracted.split(",");

                    for (int i = 0; i <= objs.length; i = i + 2) {
                        if (objs.length >= i) {
                            if (objs[i] instanceof Integer && objs[i++] instanceof Integer) {
                                int first = (int) objs[i], second = (int) objs[++i];
                                return string.replace(string.substring(string.indexOf("%randomize["), string.indexOf("]") + 1), "" + r.nextInt(((second - first) + 1) + first));
                            }

                            if (objs[i] instanceof Boolean || objs[++i] instanceof Boolean)
                                return string.replace(string.substring(string.indexOf("%randomize["), string.indexOf("]") + 1), "" + r.nextBoolean());

                            if (objs[i] instanceof String)
                                return string.replace(string.substring(string.indexOf("%randomize["), string.indexOf("]") + 1), "" + objs[r.nextInt(objs.length - 1)]);
                        }
                    }
                } else return obj;
            } else return obj;
        } else return obj;
        return obj;
    }

    public Object injectMatches(Object obj, EntityBuilder builder) {
        injectMatches(obj);
        if (builder.getEntity() != null) {
            if (obj instanceof String) {
                String string = (String) obj;

                if (string.contains("%location%"))
                    return string.replace("%location%", LocationHandler.toString(builder.getLocation()));

                if (string.contains("%location_x%"))
                    return string.replace("%location_x%", builder.getLocation().getBlockX() + "");

                if (string.contains("%location_y%"))
                    return string.replace("%location_y%", builder.getLocation().getBlockY() + "");

                if (string.contains("%location_z%"))
                    return string.replace("%location_z%", builder.getLocation().getBlockZ() + "");

                if (string.contains("%location_world%"))
                    return string.replace("%location_world%", builder.getLocation().getWorld().getName() + "");

                if (string.contains("%entity_type%"))
                    return string.replace("%entity_type%", builder.getEntityType().name());

                if (string.contains("%name%"))
                    return string.replace("%name%", builder.getCustomName());

                if (string.contains("%entity_type%"))
                    return string.replace("%entity_type%", builder.getEntityType().name());

                else return String.join(", ", Utils.targetStringResolver(string, builder.getEntity()));
            }
        }
        return obj;
    }
}