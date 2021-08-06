package land.face.jobbo.listeners;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import java.awt.Color;
import land.face.jobbo.JobboPlugin;
import land.face.jobbo.events.JobAbandonEvent;
import land.face.jobbo.util.JobUtil;
import land.face.strife.StrifePlugin;
import land.face.strife.data.StrifeMob;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

public class BuiltInTaskListener implements Listener {

  @EventHandler(priority = EventPriority.NORMAL)
  public void onKillMob(EntityDeathEvent event) {
    if (event.getEntity().getKiller() == null) {
      return;
    }
    JobUtil.bumpTaskProgress(event.getEntity().getKiller(),
        "kill", "normal", event.getEntity().getType().toString());
    if (JobboPlugin.isStrifeEnabled()) {
      StrifeMob mob = StrifePlugin.getInstance().getStrifeMobManager()
          .getStatMob(event.getEntity());
      if (StringUtils.isNotBlank(mob.getUniqueEntityId())) {
        JobUtil.bumpTaskProgress(event.getEntity().getKiller(),
            "kill", "strife_kill", mob.getUniqueEntityId());
      }
      for (String faction : mob.getFactions()) {
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

  @EventHandler(priority = EventPriority.NORMAL)
  public void onJobAbandon(JobAbandonEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (JobUtil.bumpTaskProgress(event.getPlayer(), "job", "job_abandon", null)) {
      MessageUtils.sendMessage(event.getPlayer(), ChatColor.of(new Color(176, 0, 0)) +
          "                           ＳＯＭＥＴＨＩＮＧ ＨＡＳ ＣＨＡＮＧＥＤ . ＹＯＵ ＷＩＬＬ ＫＮＯＷ ＴＨＥ ＴＲＵＴＨ");
      event.setCancelled(true);
    }
  }
}
