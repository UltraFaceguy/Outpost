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

  private static final Map<String, String> REGISTERED_JOB_TYPES = new HashMap<>();
  private static final Map<String, String> EXTERNAL_JOB_TYPES = new HashMap<>();

  public static void refreshInstance() {
    REGISTERED_JOB_TYPES.clear();
    REGISTERED_JOB_TYPES.put("KILL", "Kill Task");
    REGISTERED_JOB_TYPES.put("GATHER", "Gather Task");
    REGISTERED_JOB_TYPES.put("DELIVER", "Delivery");
    REGISTERED_JOB_TYPES.putAll(EXTERNAL_JOB_TYPES);
  }

  @Deprecated
  public static boolean bumpTaskProgress(Player player, String type, String dataOne, String dataTwo) {
    return bumpTaskProgress(player, dataOne, dataTwo);
  }

  public static boolean bumpTaskProgress(Player player, String dataOne, String dataTwo) {
    return bumpTaskProgress(player, dataOne, dataTwo, 1);
  }

  public static boolean bumpTaskProgress(Player player, String dataOne, String dataTwo, int amount) {
    Job job = JobboPlugin.getApi().getJobManager().getJob(player);
    if (job == null || job.isCompleted()) {
      return false;
    }
    if (dataOne != null && !dataOne.equalsIgnoreCase(job.getKeyStringOne())) {
      return false;
    }
    if (dataTwo != null && !dataTwo.equalsIgnoreCase(job.getKeyStringTwo())) {
      return false;
    }
    JobboPlugin.getApi().getJobManager().incrementJobProgress(player, amount);
    return true;
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
