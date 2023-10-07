package land.face.outpost.listeners;

import com.soujah.poggersguilds.events.GuildLeaveEvent;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
    if (!event.getGuild().isOwner((Player) event.getPlayer())) {
      return;
    }
    for (String outpostId : plugin.getOutpostManager().getOutpostIds()) {
      Outpost outpost = plugin.getOutpostManager().getOutpost(outpostId);
      if (event.getGuild() == outpost.getGuild()) {
        Bukkit.getLogger().info("Outpost " + outpostId + " has been abandoned due to guild disbanding");
        outpost.setGuild(null);
        outpost.setProtectTime(1L);
      }
    }
  }
}
