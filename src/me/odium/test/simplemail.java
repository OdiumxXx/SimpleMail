package me.odium.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import me.odium.test.commands.clearmailbox;
import me.odium.test.commands.delmail;
import me.odium.test.commands.inbox;
import me.odium.test.commands.mail;
import me.odium.test.commands.mailboxes;
import me.odium.test.commands.readmail;
import me.odium.test.commands.sendmail;
import me.odium.test.listeners.PListener;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class simplemail extends JavaPlugin {
  public Logger log = Logger.getLogger("Minecraft");

  public ChatColor GREEN = ChatColor.GREEN;
  public ChatColor RED = ChatColor.RED;
  public ChatColor GOLD = ChatColor.GOLD;
  public ChatColor GRAY = ChatColor.GRAY;
  public ChatColor WHITE = ChatColor.WHITE; 
  public ChatColor AQUA = ChatColor.AQUA;

  DBConnection service = DBConnection.getInstance();

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
    this.getCommand("mail").setExecutor(new mail(this));
    this.getCommand("readmail").setExecutor(new readmail(this));
    this.getCommand("delmail").setExecutor(new delmail(this));
    this.getCommand("sendmail").setExecutor(new sendmail(this));
    this.getCommand("clearmailbox").setExecutor(new clearmailbox(this));
    this.getCommand("inbox").setExecutor(new inbox(this));
    this.getCommand("mailboxes").setExecutor(new mailboxes(this));    
    // Create connection & table
   try {
    service.setConnection();
    service.createTable();
   } catch(Exception e) {
     log.info("[SimpleMail] "+"Error: "+e); 
   }
  }

  public void onDisable(){ 
    log.info("[" + getDescription().getName() + "] " + getDescription().getVersion() + " disabled.");
    service.closeConnection();
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

  public String getCurrentDTG (String string) {
    Calendar currentDate = Calendar.getInstance();
    SimpleDateFormat dtgFormat = new SimpleDateFormat ("dd/MMM/yy HH:mm");    
    return dtgFormat.format (currentDate.getTime());
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
}