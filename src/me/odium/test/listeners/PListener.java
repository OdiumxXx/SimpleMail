package me.odium.test.listeners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import me.odium.test.simplemail;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PListener implements Listener {

  public simplemail plugin;
  public PListener(simplemail plugin) {    
    this.plugin = plugin;    
    plugin.getServer().getPluginManager().registerEvents(this, plugin);  
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (plugin.getConfig().getBoolean("ShowInboxOnJoin")) {
      Player player = event.getPlayer();
      String targetnick = player.getName();
      Connection con;
      java.sql.Statement stmt;
      ResultSet rs;
      try {
        con = DriverManager.getConnection("jdbc:sqlite:test.db");
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT COUNT(target) AS inboxtotal FROM SM_Mail WHERE target='"+targetnick.toLowerCase()+"'");
        if(player.hasPermission("simplemail.inbox") && rs.getInt("inboxtotal") != 0) {
          player.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.GREEN+ "Inbox Total: " + plugin.GOLD + rs.getInt("inboxtotal"));
        }
        rs.close();
      } catch(Exception e) {
        player.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"Error: "+plugin.WHITE+e);
      }
    }
  }
}