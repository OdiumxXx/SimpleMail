package me.odium.test.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

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
      con = DriverManager.getConnection("jdbc:sqlite:test.db");
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
      if (player == null || player.hasPermission("simplemail.admin")) {
        System.err.println(e);
      }
      sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"This is not your message to delete or it does not exist. ");
    }
    return true;
  }
}