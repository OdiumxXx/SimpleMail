package me.odium.test.commands;

import java.sql.Connection;
import me.odium.test.DBConnection;
import me.odium.test.simplemail;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class clearmailbox implements CommandExecutor {   

  public simplemail plugin;
  public clearmailbox(simplemail plugin)  {
    this.plugin = plugin;
  }

  DBConnection service = DBConnection.getInstance();
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }
    
    java.sql.Statement stmt;
    Connection con;
    try {        
      con = service.Database();
      stmt = con.createStatement();
      stmt.executeUpdate("DELETE FROM SM_Mail WHERE target='"+args[0].toLowerCase()+"'");
      sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.GREEN+"Mailbox Cleared.");
    } catch(Exception e) {
      plugin.log.info("[SimpleMail] "+"Error: "+e);        
      if (e.toString().contains("locked")) {
        sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.GOLD+"The database is busy. Please wait a moment before trying again...");
      } else {
        player.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"Error: "+plugin.WHITE+e);
      }
    }

    return true;

  }

}