package land.face.jobbo.listeners;

import land.face.jobbo.JobboPlugin;
import land.face.jobbo.util.JobUtil;
import land.face.strife.StrifePlugin;
import land.face.strife.data.StrifeMob;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class BuiltInTaskListener implements Listener {

  private final JobboPlugin plugin;

  public BuiltInTaskListener(JobboPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onKillMob(EntityDeathEvent event) {
    if (event.getEntity().getKiller() == null) {
      return;
    }
    Bukkit.getLogger().info("killer: " + event.getEntity().getKiller());
    JobUtil.bumpTaskProgress(event.getEntity().getKiller(),
        "kill", "normal", event.getEntity().getType().toString());
    if (plugin.isStrifeEnabled()) {
      StrifeMob mob = StrifePlugin.getInstance().getStrifeMobManager()
          .getStatMob(event.getEntity());
      if (StringUtils.isNotBlank(mob.getUniqueEntityId())) {
        Bukkit.getLogger().info("unique: " + mob.getUniqueEntityId());
        JobUtil.bumpTaskProgress(event.getEntity().getKiller(),
            "kill", "strife_kill", mob.getUniqueEntityId());
      }
      for (String faction : mob.getFactions()) {
        Bukkit.getLogger().info("fac: " + faction);
        JobUtil.bumpTaskProgress(event.getEntity().getKiller(),
            "kill", "strife_kill_faction", faction);
      }
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onFish(PlayerFishEvent event) {
    if (event.isCancelled() || !(event.getCaught() instanceof Item)) {
      return;
    }
    JobUtil.bumpTaskProgress(event.getPlayer(),
        "fish", "normal", ((Item) event.getCaught()).getItemStack().getType().toString());
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBlockBreak(BlockBreakEvent event) {
    if (event.isCancelled()) {
      return;
    }
    JobUtil.bumpTaskProgress(event.getPlayer(),
        "mine", "normal", event.getBlock().getType().toString());
  }
}
