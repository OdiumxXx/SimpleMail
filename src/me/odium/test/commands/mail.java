package me.odium.test.commands;

import me.odium.test.simplemail;

import org.bukkit.ChatColor;
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
      if (player == null) {
        sender.sendMessage(ChatColor.GOLD+"[ SimpleMail "+plugin.getDescription().getVersion()+" ]");
        sender.sendMessage(plugin.GREEN+" /inbox " +plugin.WHITE+"- Check your inbox");
        sender.sendMessage(plugin.GREEN+" /outbox " +plugin.WHITE+"- Display your outbox");
        sender.sendMessage(plugin.GREEN+" /sendmail <player> <msg> " +plugin.WHITE+"- Send a message");
        sender.sendMessage(plugin.GREEN+" /readmail <id> " +plugin.WHITE+"- Read a message");
        sender.sendMessage(plugin.GREEN+" /delmail <id> " +plugin.WHITE+"- Delete a message");
        sender.sendMessage(plugin.GOLD+"[Admin Commands]");
        sender.sendMessage(plugin.AQUA+" /mailboxes " +plugin.WHITE+"- List active mailboxes");
        sender.sendMessage(plugin.AQUA+" /clearmailbox <playername> " +plugin.WHITE+"- Clear an active mailbox");
        return true;
      }      
      plugin.displayHelp(sender);
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