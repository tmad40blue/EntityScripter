package me.lordsaad.entityscripter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Created by Saad on 1/23/2016.
 */
public class CommandSpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("spawnmob")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("entityscripter.spawn")) {
                    if (args.length >= 1) {
                        File f = new File(EntityScripter.plugin.getDataFolder() + "/mobs/" + args[0] + ".txt");
                        CodeInterpreter code = new CodeInterpreter(f);
                        EntityBuilder builder = code.interpretProperties();
                        builder.setLocation(((Player) sender).getLocation());
                        builder.spawn();

                    } else {
                        sender.sendMessage(ChatColor.RED + "Too few arguments. /spawnmob <mob file>");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "The console is now allowed to run this command.");
            }
        }
        return true;
    }
}
