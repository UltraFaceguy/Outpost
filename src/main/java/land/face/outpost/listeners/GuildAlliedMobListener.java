package land.face.outpost.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import land.face.outpost.events.OutpostCaptureEvent;
import land.face.strife.StrifePlugin;
import land.face.strife.data.StrifeMob;
import land.face.strife.events.UniqueSpawnEvent;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GuildAlliedMobListener implements Listener {

  private OutpostPlugin plugin;
  private Map<String, String> uniqueIdToOutpost = new HashMap<>();

  public GuildAlliedMobListener(OutpostPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onSpawnGuildAlly(final UniqueSpawnEvent event) {
    StrifeMob mob = event.getStrifeMob();
    if (!uniqueIdToOutpost.containsKey(mob.getUniqueEntityId())) {
      return;
    }
    Outpost outpost = plugin.getOutpostManager()
        .getOutpost(uniqueIdToOutpost.get(mob.getUniqueEntityId()));
    if (outpost == null || outpost.getGuild() == null) {
      return;
    }
    mob.setAlliedGuild(outpost.getGuild().getId());
  }

  @EventHandler
  public void onOutpostCapture(final OutpostCaptureEvent event) {
    if (!uniqueIdToOutpost.containsValue(event.getOutpost().getId())) {
      return;
    }
    Set<String> uniqueIds = new HashSet<>();
    for (String uniqueId : uniqueIdToOutpost.keySet()) {
      if (uniqueId.equals(event.getOutpost().getId())) {
        uniqueIds.add(uniqueId);
      }
    }
    for (StrifeMob mob : StrifePlugin.getInstance().getStrifeMobManager().getMobs().values()) {
      if (uniqueIds.contains(mob.getUniqueEntityId())) {
        mob.setAlliedGuild(event.getOutpost().getGuild().getId());
        if (mob.getEntity() instanceof Mob) {
          ((Mob) mob.getEntity()).setTarget(null);
        }
      }
    }
  }

  public Map<String, String> getUniqueIdToOutpost() {
    return uniqueIdToOutpost;
  }
}
