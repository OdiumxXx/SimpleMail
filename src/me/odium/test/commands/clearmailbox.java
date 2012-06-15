package me.odium.test.commands;

import java.sql.Connection;
import java.sql.DriverManager;

import me.odium.test.simplemail;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class clearmailbox implements CommandExecutor {   

  public simplemail plugin;
  public clearmailbox(simplemail plugin)  {
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    

    java.sql.Statement stmt;
    Connection con;
    try {        
      con = DriverManager.getConnection("jdbc:sqlite:test.db");
      stmt = con.createStatement();
      stmt.executeUpdate("DELETE FROM SM_Mail WHERE target='"+args[0].toLowerCase()+"'");
      sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.GREEN+"Mailbox Cleared.");
    } catch(Exception e) {
      sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"Error: "+plugin.WHITE+e);
    }
    return true;

  }

}