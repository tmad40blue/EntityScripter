package me.lordsaad.entityscripter;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    public static List<UUID> targetEnigmaResolver(String to, String msg, Entity entity) {
        List<UUID> entities = new ArrayList<>();
        if (to.equalsIgnoreCase("@a"))
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', msg));

        else if (to.equalsIgnoreCase("@damager")) {
            if (entity != null)
                if (EntityScripter.lastDamage.containsKey(entity.getUniqueId())) {
                    final String finalMsg = msg;
                    Bukkit.getOnlinePlayers().stream().filter(p -> EntityScripter.lastDamage.get(entity.getUniqueId()).equals(p.getUniqueId())).forEach(p -> p.sendMessage(ChatColor.translateAlternateColorCodes('&', finalMsg)));
                }

        } else if (to.equalsIgnoreCase("@p")) {
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
                    closestp.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
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
}