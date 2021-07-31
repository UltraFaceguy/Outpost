package land.face.jobbo.listeners;

import land.face.jobbo.JobboPlugin;
import land.face.jobbo.data.Job;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NpcClickListener implements Listener {

  @EventHandler(priority = EventPriority.LOWEST)
  public void onTaskComplete(NPCRightClickEvent event) {
    Job job = JobboPlugin.getApi().getJobManager().getJob(event.getClicker());
    if (job == null) {
      return;
    }
    if (!job.isCompleted()) {
      return;
    }
    if (job.getTemplate().getCompletionNpc() == event.getNPC().getId()) {
      JobboPlugin.getApi().getJobManager().awardPlayer(event.getClicker(), job);
      event.setCancelled(true);
    }
  }
}
