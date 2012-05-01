package me.odium.simplemail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleMail extends JavaPlugin implements Listener {
  Logger log = Logger.getLogger("Minecraft");
  ChatColor GREEN = ChatColor.GREEN;
  ChatColor RED = ChatColor.RED;
  ChatColor GOLD = ChatColor.GOLD;
  ChatColor GRAY = ChatColor.GRAY;
  ChatColor WHITE = ChatColor.WHITE;

  //Custom Config  
  private FileConfiguration StorageConfig = null;
  private File StorageConfigFile = null;

  public void reloadStorageConfig() {
    if (StorageConfigFile == null) {
      StorageConfigFile = new File(getDataFolder(), "StorageConfig.yml");
    }
    StorageConfig = YamlConfiguration.loadConfiguration(StorageConfigFile);

    // Look for defaults in the jar
    InputStream defConfigStream = getResource("StorageConfig.yml");
    if (defConfigStream != null) {
      YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
      StorageConfig.setDefaults(defConfig);
    }
  }
  public FileConfiguration getStorageConfig() {
    if (StorageConfig == null) {
      reloadStorageConfig();
    }
    return StorageConfig;
  }
  public void saveStorageConfig() {
    if (StorageConfig == null || StorageConfigFile == null) {
      return;
    }
    try {
      StorageConfig.save(StorageConfigFile);
    } catch (IOException ex) {
      Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + StorageConfigFile, ex);
    }
  }
  // End Custom Config

  public void onEnable()  {
    log.info("[" + getDescription().getName() + "] " + getDescription().getVersion() + " enabled.");
    // Load Config.yml
    FileConfiguration cfg = getConfig();
    FileConfigurationOptions cfgOptions = cfg.options();
    cfgOptions.copyDefaults(true);
    cfgOptions.copyHeader(true);
    saveConfig();
    // Load Custom Config
    FileConfiguration ccfg = getStorageConfig();
    FileConfigurationOptions ccfgOptions = ccfg.options();
    ccfgOptions.copyDefaults(true);
    ccfgOptions.copyHeader(true);
    saveStorageConfig();
  }

  public void onDisable()
  {
    log.info("[" + getDescription().getName() + "] " + getDescription().getVersion() + " disabled.");
  }

  public static String getCurrentDTG (String string) {
    Calendar currentDate = Calendar.getInstance();
    SimpleDateFormat dtgFormat = new SimpleDateFormat ("dd/MMM/yy HH:mm");    
    return dtgFormat.format (currentDate.getTime());
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

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    Player player = null;
    if (sender instanceof Player) {
      player = (Player) sender;
    }

    if(cmd.getName().equalsIgnoreCase("simplemail")){
      sender.sendMessage(GOLD + getDescription().getName() + getDescription().getVersion());
      sender.sendMessage(GOLD + " /inbox" + WHITE + " - Check your inbox");
      sender.sendMessage(GOLD + " /readmail <#>" + WHITE + " - Read a message");
      sender.sendMessage(GOLD + " /delmail <#>" + WHITE + " - Delete a message");
      sender.sendMessage(GOLD + " /sendmail <Player> <Message>" + WHITE + " - Send a message");      
      sender.sendMessage(GOLD + " /searchplayer <PartOfNick>" + WHITE + " - Search for offline player");
      return true;      
    }
    
    if(cmd.getName().equalsIgnoreCase("mail")){
      sender.sendMessage(GOLD + getDescription().getName() + getDescription().getVersion());
      sender.sendMessage(GOLD + " /inbox" + WHITE + " - Check your inbox");
      sender.sendMessage(GOLD + " /readmail <#>" + WHITE + " - Read a message");
      sender.sendMessage(GOLD + " /delmail <#>" + WHITE + " - Delete a message");
      sender.sendMessage(GOLD + " /sendmail <Player> <Message>" + WHITE + " - Send a message");      
      sender.sendMessage(GOLD + " /searchplayer <PartOfNick>" + WHITE + " - Search for offline player");
      return true;      
    }

    if(cmd.getName().equalsIgnoreCase("sendmail")){ 
      if (args[0].length() < 2) {
        sender.sendMessage("/sendmail <player> <message>");
      } else {
        StringBuilder sb = new StringBuilder();
        for (String arg : args)
          sb.append(arg + " "); 
            String name = myGetPlayerName(args[0]);
            String[] temp = sb.toString().split(" ");
            String[] temp2 = Arrays.copyOfRange(temp, 1, temp.length);
            sb.delete(0, sb.length());
            for (String details : temp2)
            {
              sb.append(details);
              sb.append(" ");
            }
            String details = sb.toString();            
            String targetnick = name;
            if (getStorageConfig().getString(targetnick+".inboxtotal") == null) {
              getStorageConfig().set(targetnick+".inboxtotal", 1);
            } else {
              int NoOfMsg = Integer.parseInt(getStorageConfig().getString(targetnick+".inboxtotal"));
              int maxsize = Integer.parseInt(getConfig().getString("MaxInboxSize"));
              String UsersNoOfMessages = getStorageConfig().getString(targetnick+".inboxtotal");
              if (NoOfMsg >= maxsize) {
                sender.sendMessage("Player's Inbox Full");
                return true;
              }
              int temp1 = Integer.parseInt( UsersNoOfMessages );
              ++temp1;
              getStorageConfig().set(targetnick+".inboxtotal", temp1);
            }
            String intot = getStorageConfig().getString(targetnick+".inboxtotal");           
            getStorageConfig().set(targetnick+"."+intot+".sender", player.getDisplayName());
            getStorageConfig().set(targetnick+"."+intot+".date", getCurrentDTG("date")); // insert the date
            getStorageConfig().set(targetnick+"."+intot+".message", details);
            saveStorageConfig();
            sender.sendMessage(GREEN + "Mail Sent to: " + WHITE + name);
            if (Bukkit.getPlayer(args[0]) != null) {
              Bukkit.getPlayer(args[0]).sendMessage("* "+GREEN+" You've Got Mail!");
            }
            return true;
      }
    }

    if(cmd.getName().equalsIgnoreCase("inbox")){
      if (!getStorageConfig().contains(player.getDisplayName())) {
        sender.sendMessage("Your inbox is empty.");
        return true;
      }
      String targetnick = player.getDisplayName();
      String UsersNoOfMessages = getStorageConfig().getString(targetnick+".inboxtotal");      
      sender.sendMessage(GRAY + "Inbox Total: " + GOLD + UsersNoOfMessages);
      int temp1 = Integer.parseInt(UsersNoOfMessages);
      for(int i = 1; i <= temp1; ++i) {
        String mailsender = getStorageConfig().getString(targetnick+"."+i+".sender");
        String Date = getStorageConfig().getString(targetnick+"."+i+".date");
        sender.sendMessage(GRAY + " [" + GOLD + i + GRAY + "] " + "From: " + WHITE + mailsender + GRAY + " - (" +WHITE+ Date +GRAY+ ")");        
      }      
      return true;      
    }

    if(cmd.getName().equalsIgnoreCase("readmail")){
      if (args.length != 1) {
        sender.sendMessage("/readmail <Mail #>");
        return true;
      } else {
        for (char c : args[0].toCharArray()) {
          if (!Character.isDigit(c)) {
            sender.sendMessage(ChatColor.RED + "Invalid Message Number: " + ChatColor.WHITE + args[0]);
            return true;
          }
        }
        String mailno = args[0];
        String targetnick = player.getDisplayName();
        String UsersNoOfMessages = getStorageConfig().getString(targetnick+".inboxtotal");
        int mailno1 = Integer.parseInt(mailno);
        int UsersNoOfMessages1 = Integer.parseInt(UsersNoOfMessages);

        if (mailno1 <= UsersNoOfMessages1 && mailno1 != 0) {         
          String mailsender = getStorageConfig().getString(targetnick+"."+mailno+".sender");
          String maildate = getStorageConfig().getString(targetnick+"."+mailno+".date");
          String mailmessage = getStorageConfig().getString(targetnick+"."+mailno+".message");
          sender.sendMessage(GRAY + "[" + GOLD + "Message " + mailno + GRAY +"]");
          sender.sendMessage(GRAY + " Date: " + WHITE + maildate);
          sender.sendMessage(GRAY + " Sender: " + WHITE + mailsender);
          sender.sendMessage(GRAY + " Message: " + WHITE + mailmessage);
          return true;
        } else {
          sender.sendMessage(RED + "Message does not exist.");
          return true;
        }
      }
    }

    if(cmd.getName().equalsIgnoreCase("delmail")){
      if (args.length != 1) {
        sender.sendMessage("/delmail <Mail #>");
        return true;
      } else {
        for (char c : args[0].toCharArray()) {
          if (!Character.isDigit(c)) {
            sender.sendMessage(ChatColor.RED + "Invalid Message Number: " + ChatColor.WHITE + args[0]);
            return true;
          }
        }
        String mailno = args[0];
        String targetnick = player.getDisplayName();
        int mailno1 = Integer.parseInt(mailno);        
        int UsersNoOfMessages1 = Integer.parseInt(getStorageConfig().getString(targetnick+".inboxtotal"));        
        if (mailno1 <= UsersNoOfMessages1 && mailno1 != 0) {
          getStorageConfig().set(targetnick+"."+mailno, null);
          String UsersNoOfMessages = getStorageConfig().getString(targetnick+".inboxtotal");
          int temp1 = Integer.parseInt( UsersNoOfMessages );
          --temp1;
          getStorageConfig().set(targetnick+".inboxtotal", temp1);
          if (temp1 == 0) {
            getStorageConfig().set(targetnick, null);
          }        
          saveStorageConfig();
          sender.sendMessage(GREEN + "Message Deleted.");
        } else {
          sender.sendMessage(RED + "Message does not exist.");
        }
      }
    }

    if(cmd.getName().equalsIgnoreCase("searchplayer")){
      if (args.length == 0) {        
        sender.sendMessage("/searchplayer <PartOfName>" + RED + " (Case Sensitive)");        
        return true;
      } else {
        sender.sendMessage(GRAY + "[" + GOLD + "Matches" + GRAY + "]");
        int i;           
        OfflinePlayer[] Results = org.bukkit.Bukkit.getServer().getOfflinePlayers();
        for (i = 0; i < Results.length; i++) {              
          if (Results[i].getName().contains(args[0])) {
            sender.sendMessage(GRAY + "- " + GREEN + Results[i].getName());  
          }      
        } 
      }
      return true;
    }


    return true;
  }
}

