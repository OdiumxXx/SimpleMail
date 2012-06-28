package me.odium.test.listeners;

import java.sql.Connection;
import java.sql.ResultSet;

import me.odium.test.DBConnection;
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
  
  DBConnection service = DBConnection.getInstance();

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerJoin(PlayerJoinEvent event) {
    if (plugin.getConfig().getBoolean("ShowInboxOnJoin")) {
      Player player = event.getPlayer();
      String targetnick = player.getName();
      Connection con;
      java.sql.Statement stmt;
      ResultSet rs;
      try {        
        con = service.getConnection();
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT COUNT(target) AS inboxtotal FROM SM_Mail WHERE target='"+targetnick.toLowerCase()+"' AND read='NO'");
        if(player.hasPermission("simplemail.inbox") && rs.getInt("inboxtotal") != 0) {
          player.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.GREEN+ "You have " + plugin.GOLD + rs.getInt("inboxtotal")+plugin.GREEN+" new messages");
        }
        rs.close();
      } catch(Exception e) {
        player.sendMessage(plugin.GRAY+"[SimpleMail] "+plugin.RED+"Error: "+plugin.WHITE+e);
      }
    }
  }
}