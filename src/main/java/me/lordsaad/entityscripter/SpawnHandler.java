package me.lordsaad.entityscripter;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
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

    public Location makeNewLocation(Location player) {
        Random r = new Random();
        if (r.nextInt() <= chance) {
            List<Block> blocks = new ArrayList<>();
            int radius = 30;

            for (int x = -radius; x <= radius; x++)
                for (int y = -radius; y <= radius; y++)
                    for (int z = -radius; z <= radius; z++)
                        blocks.add(player.getBlock().getRelative(x, y, z));

            if (worlds.contains(player.getWorld().getName())) {

                for (Block block : blocks) {
                    if (!whitelist.contains(block.getType())) blocks.remove(block);
                    if (blacklist.contains(block.getType())) blocks.remove(block);
                    if (!biomes.contains(block.getBiome())) blocks.remove(block);
                    if (highestBlock) if (block.getLightFromSky() != 15) blocks.remove(block);
                    if (block.getRelative(BlockFace.UP).getType() != Material.AIR
                            || block.getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() != Material.AIR)
                        blocks.remove(block);
                }

                Location loc = blocks.get(r.nextInt(blocks.size())).getLocation();
                loc.setY(loc.getY() + 1);
                return loc;
            }
        }
        return null;
    }
}
