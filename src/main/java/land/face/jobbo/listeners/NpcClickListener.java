package land.face.jobbo.listeners;

import land.face.jobbo.JobboPlugin;
import land.face.jobbo.data.Job;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NpcClickListener implements Listener {

  private final JobboPlugin plugin;

  public NpcClickListener(JobboPlugin plugin) {
    this.plugin = plugin;
    Bukkit.getLogger().info("SNEED1");
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onTaskComplete(NPCRightClickEvent event) {
    Bukkit.getLogger().info("SNEED");
    Job job = plugin.getJobManager().getJob(event.getClicker());
    if (job == null) {
      return;
    }
    Bukkit.getLogger().info("SNEED");
    if (!job.isCompleted()) {
      return;
    }
    Bukkit.getLogger().info("SNEED");
    if (job.getTemplate().getCompletionNpc() == event.getNPC().getId()) {
      plugin.getJobManager().awardPlayer(event.getClicker(), job);
      event.setCancelled(true);
    }
  }
}
