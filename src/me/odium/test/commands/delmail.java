package me.odium.test.commands;

import java.sql.Connection;
import java.sql.ResultSet;

import me.odium.test.DBConnection;
import me.odium.test.simplemail;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class delmail implements CommandExecutor {   

  public simplemail plugin;
  public delmail(simplemail plugin)  {
    this.plugin = plugin;
  }
  
  DBConnection service = DBConnection.getInstance();
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }

    if (args.length != 1) {
      sender.sendMessage("/delmail <ID>");
    }
    ResultSet rs;
    java.sql.Statement stmt;
    Connection con;
    try {
      String Playername = player.getDisplayName().toLowerCase();
      con = service.Database();
      stmt = con.createStatement();

      rs = stmt.executeQuery("SELECT * FROM SM_Mail WHERE id='" + args[0] + "'");

      if (!rs.getString("target").contains(Playername)) {
        sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"This is not your message to delete or it does not exist. ");
      } else {
        stmt.executeUpdate("DELETE FROM SM_Mail WHERE id='"+args[0]+"' AND target='"+Playername+"'");
        sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.GREEN+"Message Deleted.");
      } 
      rs.close();
    } catch(Exception e) {
      plugin.log.info("[SimpleMail] "+"Error: "+e);        
      if (e.toString().contains("locked")) {
        sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.GOLD+"The database is busy. Please wait a moment before trying again...");
      } else if (e.toString().contains("java.lang.ArrayIndexOutOfBoundsException")) {
        sender.sendMessage("/delmail <id>");
      } else {
        player.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"Error: "+plugin.WHITE+e);
      }
    }

    return true;
  }
}