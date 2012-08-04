package me.odium.test.listeners;

import java.sql.Connection;
import java.sql.ResultSet;

import me.odium.test.DBConnection;
import me.odium.test.simplemail;

import org.bukkit.Bukkit;
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
  
  DBConnection service = DBConnection.getInstance();

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (plugin.getConfig().getBoolean("OnPlayerJoin.ShowNewMessages")) {
      final Player player = event.getPlayer();
      String targetnick = player.getName();
      Connection con;
      java.sql.Statement stmt;
      ResultSet rs;
      try {        
        con = service.getConnection();
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT COUNT(target) AS inboxtotal FROM SM_Mail WHERE target='"+targetnick.toLowerCase()+"' AND read='NO'");
        final int id = rs.getInt("inboxtotal");
        if(player.hasPermission("simplemail.inbox") && id != 0) {
          int tempDelay = plugin.getConfig().getInt("OnPlayerJoin.DelayInSeconds");
          int Delay = 20*tempDelay;
          
          Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getServer().getPluginManager().getPlugin("SimpleMail"), new Runnable() {
            public void run() {
              player.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.GREEN+ "You have " + plugin.GOLD +id+plugin.GREEN+" new messages");
            }
          }, Delay);
          
        }
        rs.close();
      } catch(Exception e) {
        player.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"Error: "+plugin.WHITE+e);
      }
    }
  }
}