package me.odium.test.commands;

import java.sql.Connection;
import java.sql.ResultSet;


import me.odium.test.DBConnection;
import me.odium.test.simplemail;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class purgemail implements CommandExecutor {   

  public simplemail plugin;
  public purgemail(simplemail plugin)  {
    this.plugin = plugin;
  }

  DBConnection service = DBConnection.getInstance();
  ResultSet rs;
  java.sql.Statement stmt;
  Connection con;

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
//    Player player = null;
//    if (sender instanceof Player) {
//      player = (Player) sender;
//    }    
    sender.sendMessage(plugin.GRAY+"[SimpleMail] "+ChatColor.GRAY+"Purged "+ChatColor.GREEN+plugin.expireMail()+ChatColor.GRAY+" expired messages");
    return true;
    
  }
}
