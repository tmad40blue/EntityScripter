package me.lordsaad.entityscripter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Saad on 2/3/2016.
 */
public class SpawnHandler extends EntityBuilder {

    private double chance = 10;
    private boolean highestBlock = false;
    private List<Biome> biomes = new ArrayList<>();
    private List<Material> blacklist = new ArrayList<>();
    private List<Material> whitelist = new ArrayList<>();
    private List<String> worlds = new ArrayList<>();

    public SpawnHandler(List<String> worlds, List<Biome> biomes, List<Material> whitelist, List<Material> blacklist, double chance, boolean highestblock) {
        this.worlds = worlds;
        this.biomes = biomes;
        this.whitelist = whitelist;
        this.blacklist = blacklist;
        this.chance = chance;
        this.highestBlock = highestblock;
    }

    public Location makeNewLocation(Location location) {
        if (worlds.contains(location.getWorld().getName())) {
            Random r = new Random();
            if (r.nextInt(10000) <= chance) {
                int max = 20;
                int min = -20;
                int x = location.getBlockX() + (r.nextInt(max + 1 - min) + min);
                int y = location.getBlockY() + (r.nextInt(max + 1 - min) + min);
                int z = location.getBlockZ() + (r.nextInt(max + 1 - min) + min);

                Location loc = new Location(location.getWorld(), x, y, z);
                if (highestBlock) {
                    if (loc.getBlock().getLightFromSky() != 15)
                        loc.setY(location.getWorld().getHighestBlockAt(x, z).getY());
                    else if (loc.getBlock().getLightFromSky() == 15 && loc.getBlock().getType() == Material.AIR)
                        loc.setY(location.getWorld().getHighestBlockAt(x, z).getY());
                    else if (loc.getBlock().getLightFromSky() == 15 && loc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
                        loc.setY(location.getWorld().getHighestBlockAt(x, z).getY());
                } else if (loc.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR && loc.getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() != Material.AIR) {
                    return null;
                }
                if (!whitelist.isEmpty())
                    if (!whitelist.contains(loc.getBlock().getType())) return null;
                if (!blacklist.isEmpty())
                    if (blacklist.contains(loc.getBlock().getType())) return null;
                if (!biomes.isEmpty())
                    if (!biomes.contains(loc.getBlock().getBiome())) return null;

                return loc;

            } else return null;
        } else return null;
    }
}
