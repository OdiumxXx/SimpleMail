package me.odium.test.commands;

import java.sql.Connection;
import java.sql.ResultSet;

import me.odium.test.DBConnection;
import me.odium.test.simplemail;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class readmail implements CommandExecutor {   

  public simplemail plugin;
  public readmail(simplemail plugin)  {
    this.plugin = plugin;
  }
  
  DBConnection service = DBConnection.getInstance();

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }

      if (args.length != 1) {
        sender.sendMessage("/readmail <ID>");
        return true;
      }
      ResultSet rs;
      java.sql.Statement stmt;
      Connection con;
      try {
        con = service.getConnection();
        stmt = con.createStatement();
        String Playername = player.getName();

        rs = stmt.executeQuery("SELECT * FROM SM_Mail WHERE id='"+args[0]+"' AND target='"+Playername+"'");
        String date = rs.getString("date");
        String id = rs.getString("id");
        if (rs.getString("expiration").equalsIgnoreCase("NONE")) {
          String expiration = plugin.getExpiration(date);        

          sender.sendMessage(plugin.GOLD+"Message Open: "+plugin.WHITE+id);        
          while(rs.next()){
            sender.sendMessage(plugin.GRAY+" From: " +plugin.GREEN+ rs.getString("sender"));
            sender.sendMessage(plugin.GRAY+" Date: " +plugin.WHITE+ date );      
            sender.sendMessage(plugin.GRAY+" Expires: " +plugin.WHITE+ expiration);
            sender.sendMessage(plugin.GRAY+" Message: " +plugin.WHITE+ rs.getString("message"));

          }
          rs.close();
          stmt.executeUpdate("UPDATE SM_Mail SET read='YES', expiration='"+expiration+"' WHERE id='"+args[0]+"' AND target='"+Playername+"'");
        } else {
          String expiration = rs.getString("expiration");        

          sender.sendMessage(plugin.GOLD+"Message Open: "+plugin.WHITE+id);        
          while(rs.next()){
            sender.sendMessage(plugin.GRAY+" From: " +plugin.GREEN+ rs.getString("sender"));
            sender.sendMessage(plugin.GRAY+" Date: " +plugin.WHITE+ date );      
            sender.sendMessage(plugin.GRAY+" Expires: " +plugin.WHITE+ expiration);
            sender.sendMessage(plugin.GRAY+" Message: " +plugin.WHITE+ rs.getString("message"));

          }
          rs.close();
          stmt.close();
        }
      } catch(Exception e) {         
        if (e.toString().contains("ResultSet closed")) {
          sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"This is not your message to read or it does not exist.");
        } else if (e.toString().contains("java.lang.ArrayIndexOutOfBoundsException")) {
          sender.sendMessage("/readmail <id>");
        } else {
          player.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"Error: "+plugin.WHITE+e);
        }
      }
      return true;

        

  }

}