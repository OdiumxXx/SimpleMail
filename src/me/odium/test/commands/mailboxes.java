package me.odium.test.commands;

import java.sql.Connection;
import java.sql.ResultSet;

import me.odium.test.DBConnection;
import me.odium.test.simplemail;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mailboxes implements CommandExecutor {   

  public simplemail plugin;
  public mailboxes(simplemail plugin)  {
    this.plugin = plugin;
  }
  
  DBConnection service = DBConnection.getInstance();

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {   
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }

      ResultSet rs;
      java.sql.Statement stmt;
      Connection con;
      try {        
        con = service.getConnection();
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT DISTINCT target FROM SM_Mail");        
        sender.sendMessage(plugin.GOLD+"Active Inboxes: ");
        while(rs.next()){
          sender.sendMessage(plugin.GRAY+" Mailbox: " +plugin.GREEN+ rs.getString("target"));
        }
        rs.close();
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