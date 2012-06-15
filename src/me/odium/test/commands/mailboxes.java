package me.odium.test.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import me.odium.test.simplemail;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class mailboxes implements CommandExecutor {   

  public simplemail plugin;
  public mailboxes(simplemail plugin)  {
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    

      ResultSet rs;
      java.sql.Statement stmt;
      Connection con;
      try {        
        con = DriverManager.getConnection("jdbc:sqlite:test.db");
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT DISTINCT target FROM SM_Mail");        
        sender.sendMessage(plugin.GOLD+"Active Inboxes: ");
        while(rs.next()){
          sender.sendMessage(plugin.GRAY+" Mailbox: " +plugin.GREEN+ rs.getString("target"));
        }
        rs.close();
      } catch(Exception e) {
        sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"Error: "+plugin.WHITE+e);
      }
      return true;
  }

}