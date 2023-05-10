package land.face.outpost.listeners;

import com.soujah.poggersguilds.events.GuildLeaveEvent;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GuildListener implements Listener {

  private OutpostPlugin plugin;

  public GuildListener(OutpostPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onGuildAbandon(final GuildLeaveEvent event) {
    if (event.isCancelled()) {
      return;
    }

    if (!plugin.getGuildAPI().getGuildMaster(event.getGuildID()).equals(event.getPlayer().getUniqueId())){
      return;
    }
    for (String outpostId : plugin.getOutpostManager().getOutpostIds()) {
      Outpost outpost = plugin.getOutpostManager().getOutpost(outpostId);
      if (event.getGuildID() == outpost.getGuild().getId()) {
        Bukkit.getLogger().info("Outpost " + outpostId + " has been abandoned due to guild disbanding");
        outpost.setGuild(null);
        outpost.setGuildId(null);
        outpost.setProtectTime(1L);
      }
    }
  }

//  @EventHandler
//  public void onGuildRename(final GuildRenameEvent event) {
//    if (event.isCancelled()) {
//      return;
//    }
//    //if (!event.getPlayer() has gems){
//    //  event.setCancelled(true);
//    //  MessageUtils.sendMessage(event.getPlayer(), "&eYou don't have enough &dFaceGems &eto do this!");
//    //}
//  }

}
