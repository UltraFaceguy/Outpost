package land.face.jobbo.listeners;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import land.face.jobbo.JobboPlugin;
import land.face.jobbo.data.PostedJob;
import land.face.jobbo.menus.AcceptJobMenu;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignClickListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onSignClick(PlayerInteractEvent event) {
    if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Sign) {
      Location location = event.getClickedBlock().getLocation();
      PostedJob postedJob = JobboPlugin.getApi().getJobManager().getJobPosting(location);
      if (postedJob == null) {
        return;
      }
      event.setCancelled(true);
      if (JobboPlugin.getApi().getJobManager().hasJob(event.getPlayer())) {
        MessageUtils.sendMessage(event.getPlayer(),
            "&eYou cannot accept a job if you already have one! Use &f/job &eto check your current progress or abandon your current job.");
        return;
      }
      if (JobboPlugin.getApi().getJobManager().isNewJobCooldown(event.getPlayer())) {
        int secRemaining = (int) JobboPlugin.getApi().getJobManager()
            .getCooldownRemaining(event.getPlayer());
        MessageUtils.sendMessage(event.getPlayer(),
            "&cYou started a different job too recently to do this! Please wait for &f"
                + secRemaining + "s&c!");
        return;
      }
      AcceptJobMenu.getInstance().openForPlayer(event.getPlayer(), postedJob.getJob());
    }
  }
}
