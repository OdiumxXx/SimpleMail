package me.odium.test.commands;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import me.odium.test.simplemail;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class inbox implements CommandExecutor {   

  public simplemail plugin;
  public inbox(simplemail plugin)  {
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }

    ResultSet rs;
    java.sql.Statement stmt;
    Connection con;
    try {
      con = DriverManager.getConnection("jdbc:sqlite:test.db");
      stmt = con.createStatement();
      String targetnick = player.getDisplayName(); 
      rs = stmt.executeQuery("SELECT * FROM SM_Mail WHERE target='" + targetnick.toLowerCase() + "'");        
      sender.sendMessage(plugin.GOLD+"- ID ----- FROM ----------- DATE ------");
      while(rs.next()){
        String isread = rs.getString("read");          
        if (isread.contains("NO")) {
          sender.sendMessage(plugin.GRAY+"  [" +plugin.GREEN+ rs.getInt("id") +plugin.GRAY+"]"+"         "+rs.getString("sender")+"          "+rs.getString("date"));            
        } else {
          sender.sendMessage(plugin.GRAY+"  [" +rs.getInt("id") +plugin.GRAY+"]"+"         "+rs.getString("sender")+"          "+rs.getString("date"));
        }
      }
      rs.close();
    } catch(Exception e) {                
      sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"Error: "+plugin.WHITE+e);
    }        
    return true;    
  }

}