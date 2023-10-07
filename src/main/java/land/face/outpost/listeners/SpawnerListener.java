package land.face.outpost.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import land.face.strife.events.SpawnerSpawnEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpawnerListener implements Listener {

  private final OutpostPlugin plugin;
  private final Map<String, UUID> spawnerGuilds = new HashMap<>();
  private long cacheTime = System.currentTimeMillis();

  public SpawnerListener(OutpostPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onSpawnerSpawn(final SpawnerSpawnEvent event) {
    if (cacheTime < System.currentTimeMillis()) {
      for (Outpost outpost : plugin.getOutpostManager().getOutposts()) {
        if (outpost.getGuild() != null) {
          for (String id : outpost.getSpawnerIds()) {
            spawnerGuilds.put(id, outpost.getGuild().getId());
          }
        }
      }
      cacheTime = System.currentTimeMillis() + 300000;
    }
    if (spawnerGuilds.containsKey(event.getSpawner().getId())) {
      String spawnerId = event.getSpawner().getId();
      UUID guildUUid = spawnerGuilds.get(spawnerId);
      String prefix = plugin.getGuildPlugin().getGuildManager().getGuild(guildUUid).getTag();
      event.getStrifeMob().setAlliedGuild(spawnerGuilds.get(spawnerId));
      event.getStrifeMob().getEntity().setCustomName(ChatColor.GOLD + "[" + prefix + "] " +
          event.getStrifeMob().getEntity().getCustomName());
    }
  }
}
