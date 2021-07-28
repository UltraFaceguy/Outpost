package land.face.jobbo.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import land.face.jobbo.JobboPlugin;
import land.face.jobbo.data.Job;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class JobUtil {

  private static JobboPlugin instance;
  private static final Map<String, String> REGISTERED_JOB_TYPES = new HashMap<>();
  private static final Map<String, String> EXTERNAL_JOB_TYPES = new HashMap<>();

  public static void refreshInstance(JobboPlugin plugin) {
    instance = plugin;
    REGISTERED_JOB_TYPES.clear();
    REGISTERED_JOB_TYPES.put("KILL", "Kill Task");
    REGISTERED_JOB_TYPES.put("GATHER", "Gather Task");
    REGISTERED_JOB_TYPES.put("DELIVER", "Delivery");
    REGISTERED_JOB_TYPES.putAll(EXTERNAL_JOB_TYPES);
  }

  public static void incrementJobProgress(Player player) {
    instance.getJobManager().incrementJobProgress(player);
  }

  public static void bumpTaskProgress(Player player, String type, String dataOne, String dataTwo) {
    Job job = instance.getJobManager().getJob(player);
    if (job == null || job.isCompleted()) {
      return;
    }
    Bukkit.getLogger().info("job: " + job.toString());
    Bukkit.getLogger().info("t: " + type);
    Bukkit.getLogger().info("d1: " + dataOne);
    Bukkit.getLogger().info("d2: " + dataTwo);
    if (!job.getTaskType().equalsIgnoreCase(type)) {
      return;
    }
    if (dataOne != null && !dataOne.equalsIgnoreCase(job.getKeyStringOne())) {
      return;
    }
    if (dataTwo != null && !dataTwo.equalsIgnoreCase(job.getKeyStringTwo())) {
      return;
    }
    instance.getJobManager().incrementJobProgress(player);
  }

  public static void registerJobTask(String taskId, String taskDesc) {
    if (EXTERNAL_JOB_TYPES.containsKey(taskId) || REGISTERED_JOB_TYPES.containsKey(taskId)) {
      Bukkit.getLogger().warning("Attempted to register task type " + taskId + " but it already exists");
      return;
    }
    if (taskDesc.length() > 13) {
      taskDesc = taskDesc.substring(0, 12);
    }
    EXTERNAL_JOB_TYPES.put(taskId, taskDesc);
    REGISTERED_JOB_TYPES.put(taskId, taskDesc);
    Bukkit.getLogger().warning("[Jobbo] Registered task " + taskId + " successfully!");
  }

  public static Set<String> getTaskIds() {
    return new HashSet<>(REGISTERED_JOB_TYPES.keySet());
  }

  public static String getTaskDesc(String taskId) {
    return REGISTERED_JOB_TYPES.getOrDefault(taskId, "");
  }

}
