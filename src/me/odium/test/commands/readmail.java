package me.odium.test.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

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

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }

      if (args.length != 1) {
        sender.sendMessage("/readmail <ID>");
      }
      ResultSet rs;
      java.sql.Statement stmt;
      Connection con;
      try {
        con = DriverManager.getConnection("jdbc:sqlite:test.db");
        stmt = con.createStatement();
        String Playername = player.getDisplayName().toLowerCase(); 

        stmt.executeUpdate("UPDATE SM_Mail SET read='YES' WHERE id='"+args[0]+"' AND target='"+Playername+"'");
        rs = stmt.executeQuery("SELECT * FROM SM_Mail WHERE id='"+args[0]+"' AND target='"+Playername+"'");

        sender.sendMessage(plugin.GOLD+"Message Open: "+plugin.WHITE+rs.getInt("id"));        

        while(rs.next()){
          sender.sendMessage(plugin.GRAY+" From: " +plugin.GREEN+ rs.getString("sender"));
          sender.sendMessage(plugin.GRAY+" Date: " +plugin.GREEN+ rs.getString("date"));                    
          sender.sendMessage(plugin.GRAY+" Message: " +plugin.WHITE+ rs.getString("message"));
        }
        rs.close();
      } catch(Exception e) {
        if (player == null || player.hasPermission("simplemail.admin")) {
          System.err.println(e);
        }
        sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"This is not your message to delete or it does not exist. ");
      }
      return true;
  
  }

}