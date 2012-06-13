package me.odium.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class simplemail extends JavaPlugin {
  Logger log = Logger.getLogger("Minecraft");

  ChatColor GREEN = ChatColor.GREEN;
  ChatColor RED = ChatColor.RED;
  ChatColor GOLD = ChatColor.GOLD;
  ChatColor GRAY = ChatColor.GRAY;
  ChatColor WHITE = ChatColor.WHITE; 
  ChatColor AQUA = ChatColor.AQUA;

  public void onEnable(){    
    log.info("[" + getDescription().getName() + "] " + getDescription().getVersion() + " enabled.");
    // Load Config.yml
    FileConfiguration cfg = getConfig();
    FileConfigurationOptions cfgOptions = cfg.options();
    cfgOptions.copyDefaults(true);
    cfgOptions.copyHeader(true);
    saveConfig();
    // declare new listener
    new PListener(this);
    // Create connection & table
    Connection con;
    java.sql.Statement stmt;
    try{
      Class.forName("org.sqlite.JDBC");
      con = DriverManager.getConnection("jdbc:sqlite:test.db");
      stmt = con.createStatement();
      String queryC = "CREATE TABLE IF NOT EXISTS SM_Mail (id INTEGER PRIMARY KEY, sender varchar(16), target varchar(16), date timestamp, message varchar(30), read varchar(10))";
      stmt.executeUpdate(queryC);
    } catch(Exception e) {
      System.err.println(e);
    }
  }

  public void onDisable(){ 
    log.info("[" + getDescription().getName() + "] " + getDescription().getVersion() + " disabled."); 
  }  

  public String myGetPlayerName(String name) { 
    Player caddPlayer = getServer().getPlayerExact(name);
    String pName;
    if(caddPlayer == null) {
      caddPlayer = getServer().getPlayer(name);
      if(caddPlayer == null) {
        pName = name;
      } else {
        pName = caddPlayer.getName();
      }
    } else {
      pName = caddPlayer.getName();
    }
    return pName;
  }

  public static String getCurrentDTG (String string) {
    Calendar currentDate = Calendar.getInstance();
    SimpleDateFormat dtgFormat = new SimpleDateFormat ("dd/MMM/yy HH:mm");    
    return dtgFormat.format (currentDate.getTime());
  }

  public class PListener implements Listener {

    public PListener(simplemail instance) {
      Plugin plugin = instance;
      getServer().getPluginManager().registerEvents(this, plugin);  
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
      if (getConfig().getBoolean("ShowInboxOnJoin")) {
        Player player = event.getPlayer();
        String targetnick = player.getName();
        Connection con;
        java.sql.Statement stmt;
        ResultSet rs;
        try {
          con = DriverManager.getConnection("jdbc:sqlite:test.db");
          stmt = con.createStatement();
          rs = stmt.executeQuery("SELECT COUNT(target) AS inboxtotal FROM SM_Mail WHERE target='"+targetnick+"'");
          if(player.hasPermission("simplemail.inbox") && rs.getInt("inboxtotal") != 0) {
            player.sendMessage(GRAY+"[SimpleMail] "+GREEN+ "Inbox Total: " + GOLD + rs.getInt("inboxtotal"));
          }
          rs.close();
        } catch(Exception e) {
          player.sendMessage(GRAY+"[SimpleMail] "+RED+"Error:v"+WHITE+e);
        }
      }
    }
  }


  public void displayHelp(Player player) {
    player.sendMessage(GOLD+"[ SimpleMail "+getDescription().getVersion()+" ]");
    player.sendMessage(GREEN+" /inbox " +WHITE+"- Check your inbox");
    player.sendMessage(GREEN+" /sendmail <player> <msg> " +WHITE+"- Send a message");
    player.sendMessage(GREEN+" /readmail <id> " +WHITE+"- Read a message");
    player.sendMessage(GREEN+" /delmail <id> " +WHITE+"- Delete a message");
    if (player == null || player.hasPermission("SimpleMail.admin")) {     
      player.sendMessage(GOLD+"[Admin Commands]");
      player.sendMessage(AQUA+" /mailboxes " +WHITE+"- List active mailboxes");
      player.sendMessage(AQUA+" /clearmailbox <playername> " +WHITE+"- Clear an active mailbox");
    }
  }

  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }

    if(cmd.getName().equalsIgnoreCase("mail")){
      if (args.length == 0) {
        displayHelp(player);
        return true;
      } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
        if(player == null || player.hasPermission("simplemail.admin")) {
          reloadConfig();
          sender.sendMessage(GRAY+"[SimpleMail] "+ChatColor.GREEN + "Config Reloaded!");
          return true;
        } else {
          sender.sendMessage(GRAY+"[SimpleMail] "+ChatColor.RED + "You do not have permission");
        }
      }
    }

    if(cmd.getName().equalsIgnoreCase("simplemail")){
      if (args.length == 0) {
        displayHelp(player);
        return true;
      } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
        if(player == null || player.hasPermission("simplemail.admin")) {
          reloadConfig();
          sender.sendMessage(GRAY+"[SimpleMail] "+ChatColor.GREEN + "Config Reloaded!");
          return true;
        } else {
          sender.sendMessage(GRAY+"[SimpleMail] "+ChatColor.RED + "You do not have permission");
        }
      }
    }

    if(cmd.getName().equalsIgnoreCase("sendmail")){
      if (args.length < 2) {
        sender.sendMessage("/sendmail <ExactPlayerName> <Message>");
      }
      Connection con;
      java.sql.Statement stmt;
      try {
        con = DriverManager.getConnection("jdbc:sqlite:test.db");
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

            String Rightnow = getCurrentDTG("date");            
            String target = myGetPlayerName(args[0]);

            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(target) AS inboxtotal FROM SM_Mail WHERE target='"+target+"'");
            int MaxMailboxSize = getConfig().getInt("MaxMailboxSize");
            if (rs2.getInt("inboxtotal") >= MaxMailboxSize) {
              sender.sendMessage(RED+"Player's Inbox is full");
              rs2.close();
              return true;
            }
            PreparedStatement statement = con.prepareStatement("insert into SM_MAIL values (?,?,?,?,?,?);");
            if (player == null) {              
              statement.setString(2, "Console");              

              statement.setString(3, target);
              statement.setString(4, Rightnow);
              statement.setString(5, details);
              statement.setString(6, "NO");
              statement.executeUpdate();
            } else {                        
              statement.setString(2, player.getDisplayName());              

              statement.setString(3, target);
              statement.setString(4, Rightnow);
              statement.setString(5, details);
              statement.setString(6, "NO");
              statement.executeUpdate();
            }        
      } catch(Exception e) {
        sender.sendMessage(GRAY+"[SimpleMail] "+RED+"Error:v"+WHITE+e);
      }
      String target = myGetPlayerName(args[0]);
      sender.sendMessage(GRAY+"[SimpleMail] "+ChatColor.GREEN + "Message Sent to: " +ChatColor.WHITE+ target);
      if (Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).hasPermission("simplemail.inbox")) {
        Bukkit.getPlayer(args[0]).sendMessage(GRAY+"[SimpleMail] "+GREEN+"You've Got Mail!"+GOLD+" [/Mail]");
      }

      return true;
    }

    if(cmd.getName().equalsIgnoreCase("inbox")){
      ResultSet rs;
      java.sql.Statement stmt;
      Connection con;
      try {
        con = DriverManager.getConnection("jdbc:sqlite:test.db");
        stmt = con.createStatement();
        String targetnick = player.getDisplayName(); 
        rs = stmt.executeQuery("SELECT * FROM SM_Mail WHERE target='" + targetnick + "'");        
        sender.sendMessage(GOLD+"- ID ----- FROM ----------- DATE ------");
        while(rs.next()){
          String isread = rs.getString("read");          
          if (isread.contains("NO")) {
            sender.sendMessage(GRAY+"  [" +GREEN+ rs.getInt("id") +GRAY+"]"+"         "+rs.getString("sender")+"          "+rs.getString("date"));            
          } else {
            sender.sendMessage(GRAY+"  [" +rs.getInt("id") +GRAY+"]"+"         "+rs.getString("sender")+"          "+rs.getString("date"));
          }
        }
        rs.close();
      } catch(Exception e) {                
        sender.sendMessage(GRAY+"[SimpleMail] "+RED+"Error:v"+WHITE+e);
      }        
      return true;
    }

    if(cmd.getName().equalsIgnoreCase("readmail")){
      if (args.length != 1) {
        sender.sendMessage("/readmail <ID>");
      }
      ResultSet rs;
      java.sql.Statement stmt;
      Connection con;
      try {
        con = DriverManager.getConnection("jdbc:sqlite:test.db");
        stmt = con.createStatement();
        String Playername = player.getDisplayName(); 

        stmt.executeUpdate("UPDATE SM_Mail SET read='YES' WHERE id='"+args[0]+"' AND target='"+Playername+"'");
        rs = stmt.executeQuery("SELECT * FROM SM_Mail WHERE id='"+args[0]+"' AND target='"+Playername+"'");

        sender.sendMessage(GOLD+"Message Open: "+WHITE+rs.getInt("id"));        

        while(rs.next()){
          sender.sendMessage(GRAY+" From: " +GREEN+ rs.getString("sender"));
          sender.sendMessage(GRAY+" Date: " +GREEN+ rs.getString("date"));                    
          sender.sendMessage(GRAY+" Message: " +WHITE+ rs.getString("message"));
        }
        rs.close();
      } catch(Exception e) {
        if (player == null || player.hasPermission("simplemail.admin")) {
          System.err.println(e);
        }
        sender.sendMessage(GRAY+"[SimpleMail] "+RED+"This is not your message to delete or it does not exist. ");
      }
      return true;
    }


    if(cmd.getName().equalsIgnoreCase("delmail")){
      if (args.length != 1) {
        sender.sendMessage("/delmail <ID>");
      }
      ResultSet rs;
      java.sql.Statement stmt;
      Connection con;
      try {
        String Playername = player.getDisplayName();
        con = DriverManager.getConnection("jdbc:sqlite:test.db");
        stmt = con.createStatement();

        rs = stmt.executeQuery("SELECT * FROM SM_Mail WHERE id='" + args[0] + "'");

        if (!rs.getString("target").contains(Playername)) {
          sender.sendMessage(GRAY+"[SimpleMail] "+RED+"This is not your message to delete or it does not exist. ");
        } else {
          stmt.executeUpdate("DELETE FROM SM_Mail WHERE id='"+args[0]+"' AND target='"+Playername+"'");
          sender.sendMessage(GRAY+"[SimpleMail] "+GREEN+"Message Deleted.");
        } 
        rs.close();
      } catch(Exception e) {
        if (player == null || player.hasPermission("simplemail.admin")) {
          System.err.println(e);
        }
        sender.sendMessage(GRAY+"[SimpleMail] "+RED+"This is not your message to delete or it does not exist. ");
      }
      return true;
    }

    // ADMINISTRATION COMMANDS

    if(cmd.getName().equalsIgnoreCase("mailboxes")){
      ResultSet rs;
      java.sql.Statement stmt;
      Connection con;
      try {        
        con = DriverManager.getConnection("jdbc:sqlite:test.db");
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT DISTINCT target FROM SM_Mail");        
        sender.sendMessage(GOLD+"Active Inboxes: ");
        while(rs.next()){
          sender.sendMessage(GRAY+" Mailbox: " +GREEN+ rs.getString("target"));
        }
        rs.close();
      } catch(Exception e) {
        sender.sendMessage(GRAY+"[SimpleMail] "+RED+"Error:v"+WHITE+e);
      }
      return true;
    }

    if(cmd.getName().equalsIgnoreCase("clearmailbox")){
      java.sql.Statement stmt;
      Connection con;
      try {        
        con = DriverManager.getConnection("jdbc:sqlite:test.db");
        stmt = con.createStatement();
        stmt.executeUpdate("DELETE FROM SM_Mail WHERE target='"+args[0]+"'");
        sender.sendMessage(GREEN+"Mailbox Cleared.");
      } catch(Exception e) {
        sender.sendMessage(GRAY+"[SimpleMail] "+RED+"Error:v"+WHITE+e);
      }
      return true;
    }

    return true;
  }
}