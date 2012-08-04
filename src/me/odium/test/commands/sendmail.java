package me.odium.test.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import me.odium.test.DBConnection;
import me.odium.test.simplemail;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class sendmail implements CommandExecutor {   

  public simplemail plugin;
  public sendmail(simplemail plugin)  {
    this.plugin = plugin;
  }
  
  DBConnection service = DBConnection.getInstance();

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {    
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }
  
      if (args.length < 2) {
        sender.sendMessage("/sendmail <ExactPlayerName> <Message>");
        return true;
      }
      Connection con;
      java.sql.Statement stmt;
      try {        
        con = service.getConnection();
        stmt = con.createStatement();

        StringBuilder sb = new StringBuilder();
        for (String arg : args)
          sb.append(arg + " ");            
            String[] temp = sb.toString().split(" ");
            String[] temp2 = Arrays.copyOfRange(temp, 1, temp.length);
            sb.delete(0, sb.length());
            for (String details : temp2)
            {
              sb.append(details);
              sb.append(" ");
            }
            String details = sb.toString();  

            String Rightnow = plugin.getCurrentDTG("date");            
            String target = plugin.myGetPlayerName(args[0]);

            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(target) AS inboxtotal FROM SM_Mail WHERE target='"+target+"'");
            int MaxMailboxSize = plugin.getConfig().getInt("MaxMailboxSize");
            if (rs2.getInt("inboxtotal") >= MaxMailboxSize) {
              sender.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"Player's Inbox is full");
              rs2.close();
              return true;
            }
            PreparedStatement statement = con.prepareStatement("insert into SM_MAIL values (?,?,?,?,?,?,?);");
            if (player == null) {              
              statement.setString(2, "console");              

              statement.setString(3, target);
              
              statement.setString(4, Rightnow);             
              statement.setString(5, details);
              
              statement.setString(6, "NO");
              statement.setString(7, "NONE"); 
              statement.executeUpdate();
              statement.close();

              
              sender.sendMessage(plugin.GRAY+"[SimpleMail] "+ChatColor.GREEN + "Message Sent to: " +ChatColor.WHITE+ target);
              if (Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).hasPermission("simplemail.inbox")) {
                Bukkit.getPlayer(args[0]).sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.GREEN+"You've Got Mail!"+plugin.GOLD+" [/Mail]");
              }
              return true;
              
            } else {                        
              statement.setString(2, player.getName());              

              statement.setString(3, target);
              
              statement.setString(4, Rightnow);              
              statement.setString(5, details);
              
              statement.setString(6, "NO");
              statement.setString(7, "NONE");
              statement.executeUpdate();
              statement.close();
              
              sender.sendMessage(plugin.GRAY+"[SimpleMail] "+ChatColor.GREEN + "Message Sent to: " +ChatColor.WHITE+ target);
              if (Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).hasPermission("simplemail.inbox")) {
                Bukkit.getPlayer(args[0]).sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.GREEN+"You've Got Mail!"+plugin.GOLD+" [/Mail]");
              }
              return true;
            }
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