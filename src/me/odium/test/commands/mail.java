package me.odium.test.commands;

import me.odium.test.simplemail;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mail implements CommandExecutor {   

  public simplemail plugin;
  public mail(simplemail plugin)  {
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }
    if (args.length == 0) {
      plugin.displayHelp(player);
      return true;
    } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
      if(player == null || player.hasPermission("simplemail.admin")) {
        plugin.reloadConfig();
        sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.GREEN + "Config Reloaded!");
        return true;
      } else {
        sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED + "You do not have permission");
      }
    }

    return true;    
  }

}