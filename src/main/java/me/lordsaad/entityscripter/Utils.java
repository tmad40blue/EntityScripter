package me.lordsaad.entityscripter;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by Saad on 1/28/2016.
 */
public class Utils {

    public static List<UUID> targetUUIDResolver(String to, Entity entity) {
        List<UUID> entities = new ArrayList<>();
        if (to.contains("@a"))
            entities.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));

        else if (to.contains("@damager")) {
            if (entity != null)
                if (EntityScripter.lastDamage.containsKey(entity.getUniqueId()))
                    entities.addAll(Bukkit.getOnlinePlayers().stream().filter(p -> EntityScripter.lastDamage.get(entity.getUniqueId()).equals(p.getUniqueId())).map(Player::getUniqueId).collect(Collectors.toList()));

        } else if (to.contains("@p")) {
            if (entity != null) {
                double closest = Double.MAX_VALUE;
                Player closestp = null;
                for (Player i : Bukkit.getOnlinePlayers()) {
                    double dist = i.getLocation().distance(entity.getLocation());
                    if (closest == Double.MAX_VALUE || dist < closest) {
                        closest = dist;
                        closestp = i;
                    }
                }
                if (closestp != null)
                    entities.add(closestp.getUniqueId());
            }

        } else if (to.contains("@r")) {
            if (entity != null) {
                if (StringUtils.isNumeric(to.split("=")[1])) {
                    double radius = Double.parseDouble(to.split("=")[1]);
                    entities.addAll(entity.getNearbyEntities(radius, radius, radius).stream().filter(entity1 -> entity1 instanceof Player).map(Entity::getUniqueId).collect(Collectors.toList()));
                }
            }
        }

        return entities;
    }

    public static List<String> targetStringResolver(String to, Entity entity) {
        List<String> entities = new ArrayList<>();
        if (to.contains("@a"))
            entities.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

        else if (to.contains("@damager")) {
            if (entity != null)
                if (EntityScripter.lastDamage.containsKey(entity.getUniqueId()))
                    entities.addAll(Bukkit.getOnlinePlayers().stream().filter(p -> EntityScripter.lastDamage.get(entity.getUniqueId()).equals(p.getUniqueId())).map(Player::getName).collect(Collectors.toList()));

        } else if (to.contains("@p")) {
            if (entity != null) {
                double closest = Double.MAX_VALUE;
                Player closestp = null;
                for (Player i : Bukkit.getOnlinePlayers()) {
                    double dist = i.getLocation().distance(entity.getLocation());
                    if (closest == Double.MAX_VALUE || dist < closest) {
                        closest = dist;
                        closestp = i;
                    }
                }
                if (closestp != null)
                    entities.add(closestp.getName());
            }

        } else if (to.contains("@r")) {
            if (entity != null) {
                if (StringUtils.isNumeric(to.split("=")[1])) {
                    double radius = Double.parseDouble(to.split("=")[1]);
                    entities.addAll(entity.getNearbyEntities(radius, radius, radius).stream().filter(entity1 -> entity1 instanceof Player).map(Entity::getName).collect(Collectors.toList()));
                }
            }
        }

        return entities;
    }

    public static String getEquipmentType(Material mat) {
        if (mat == Material.DIAMOND_BOOTS
                || mat == Material.CHAINMAIL_BOOTS
                || mat == Material.IRON_BOOTS
                || mat == Material.LEATHER_BOOTS) return "boots";

        else if (mat == Material.DIAMOND_LEGGINGS
                || mat == Material.CHAINMAIL_LEGGINGS
                || mat == Material.IRON_LEGGINGS
                || mat == Material.LEATHER_LEGGINGS) return "leggings";

        else if (mat == Material.DIAMOND_CHESTPLATE
                || mat == Material.CHAINMAIL_CHESTPLATE
                || mat == Material.IRON_CHESTPLATE
                || mat == Material.LEATHER_CHESTPLATE) return "chestplate";

        else if (mat == Material.DIAMOND_HELMET
                || mat == Material.CHAINMAIL_HELMET
                || mat == Material.IRON_HELMET
                || mat == Material.LEATHER_HELMET) return "helmet";

        else return "hand";
    }
}