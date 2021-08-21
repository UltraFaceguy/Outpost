package land.face.jobbo.managers;

import com.tealcube.minecraft.bukkit.facecore.utilities.AdvancedActionBarUtil;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.TitleUtils;
import com.tealcube.minecraft.bukkit.shade.google.gson.JsonArray;
import com.tealcube.minecraft.bukkit.shade.google.gson.JsonElement;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import io.pixeloutlaw.minecraft.spigot.garbage.ListExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.garbage.StringExtensionsKt;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import land.face.jobbo.JobboPlugin;
import land.face.jobbo.data.Job;
import land.face.jobbo.data.JobBoard;
import land.face.jobbo.data.JobTemplate;
import land.face.jobbo.data.PendingSignData;
import land.face.jobbo.data.PostedJob;
import land.face.jobbo.events.JobAbandonEvent;
import land.face.jobbo.events.JobAcceptEvent;
import land.face.jobbo.events.JobCompleteEvent;
import land.face.jobbo.events.JobGenerationEvent;
import land.face.jobbo.util.GsonFactory;
import land.face.strife.StrifePlugin;
import land.face.strife.data.champion.LifeSkillType;
import land.face.strife.util.StatUtil;
import land.face.waypointer.WaypointerPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JobManager {

  private final JobboPlugin plugin;

  private final Map<String, JobTemplate> loadedTemplates = new HashMap<>();
  private final Set<JobBoard> jobBoards = new HashSet<>();
  private final Map<UUID, Job> acceptedJobs = new HashMap<>();
  private final Map<UUID, Long> abandonCooldown = new HashMap<>();

  private final Random random = new Random();

  private final List<TextComponent> starRating = Arrays.asList(
      Component.text("✦✦✦✦✦").color(TextColor.color(0, 0, 0)),
      Component.text("✦").color(TextColor.color(67, 214, 35)).append(
          Component.text("✦✦✦✦").color(TextColor.color(0, 0, 0))
      ),
      Component.text("✦✦").color(TextColor.color(171, 214, 35)).append(
          Component.text("✦✦✦").color(TextColor.color(0, 0, 0))
      ),
      Component.text("✦✦✦").color(TextColor.color(222, 193, 53)).append(
          Component.text("✦✦").color(TextColor.color(0, 0, 0))
      ),
      Component.text("✦✦✦✦").color(TextColor.color(214, 109, 35)).append(
          Component.text("✦").color(TextColor.color(0, 0, 0))
      ),
      Component.text("✦✦✦✦✦").color(TextColor.color(255, 77, 77))
  );
  private final List<TextComponent> timeLeft = Arrays.asList(
      Component.text("Time Left: 0m").color(TextColor.color(0, 228, 217)),
      Component.text("Time Left: 1m").color(TextColor.color(0, 128, 255)),
      Component.text("Time Left: 2m").color(TextColor.color(0, 128, 255)),
      Component.text("Time Left: 3m").color(TextColor.color(0, 128, 255)),
      Component.text("Time Left: 4m").color(TextColor.color(0, 128, 255)),
      Component.text("Time Left: 5m").color(TextColor.color(0, 128, 255))
  );
  private final List<TextComponent> newJob = Arrays.asList(
      Component.text("New Job: 0m").color(TextColor.color(0, 128, 255)),
      Component.text("New Job: 1m").color(TextColor.color(0, 128, 255)),
      Component.text("New Job: 2m").color(TextColor.color(0, 128, 255)),
      Component.text("New Job: 3m").color(TextColor.color(0, 128, 255)),
      Component.text("New Job: 4m").color(TextColor.color(0, 128, 255)),
      Component.text("New Job: 5m").color(TextColor.color(0, 128, 255))
  );
  private final TextComponent jobAccepted = Component.text("[JOB TAKEN]")
      .color(TextColor.color(144, 12, 63)).decoration(TextDecoration.BOLD, true);
  private final TextComponent clickForInfo = Component.text("[Click For Info!]")
      .color(TextColor.color(255, 230, 255));

  public JobManager(JobboPlugin plugin) {
    this.plugin = plugin;
  }

  public boolean createBoard(String id) {
    for (JobBoard board : jobBoards) {
      if (board.getId().equals(id)) {
        return false;
      }
    }
    JobBoard board = new JobBoard();
    board.setId(id);
    jobBoards.add(board);
    return true;
  }

  public boolean removeBoard(String id) {
    JobBoard selectedBoard = null;
    for (JobBoard board : jobBoards) {
      if (board.getId().equals(id)) {
        selectedBoard = board;
      }
    }
    if (selectedBoard != null) {
      jobBoards.remove(selectedBoard);
      return true;
    }
    return false;
  }

  public JobBoard getBoard(String id) {
    for (JobBoard board : jobBoards) {
      if (board.getId().equals(id)) {
        return board;
      }
    }
    return null;
  }

  public void clearAllBoardJobs() {
    for (JobBoard board : jobBoards) {
      for (PostedJob postedJob : board.getJobListings()) {
        postedJob.setJob(null);
        postedJob.setSeconds(1);
      }
    }
  }

  public long getCooldownRemaining(Player player) {
    return (abandonCooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
  }

  public boolean isNewJobCooldown(Player player) {
    return abandonCooldown.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis();
  }

  public void clearJobCooldown(Player player) {
    abandonCooldown.remove(player.getUniqueId());
  }

  public boolean hasJob(Player player) {
    return acceptedJobs.containsKey(player.getUniqueId());
  }

  public void abandonJob(Player player) {
    if (!acceptedJobs.containsKey(player.getUniqueId())) {
      return;
    }
    JobAbandonEvent event = new JobAbandonEvent(player, acceptedJobs.get(player.getUniqueId()));
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return;
    }
    MessageUtils.sendMessage(event.getPlayer(), "&eJob Abandoned!");
    acceptedJobs.remove(player.getUniqueId());
  }


  public Job getJob(Player player) {
    return acceptedJobs.get(player.getUniqueId());
  }

  public void incrementJobProgress(Player player, int amount) {
    Job job = acceptedJobs.get(player.getUniqueId());
    if (job != null) {
      boolean complete = job.increment(amount);
      if (!complete) {
        AdvancedActionBarUtil.addOverrideMessage(player, "JOB-UPDATE",
            "&aJOB [" + job.getProgress() + "/" + job.getProgressCap() + "]", 30);
      } else {
        AdvancedActionBarUtil.addOverrideMessage(player, "JOB-UPDATE", "&aJOB &fCOMPLETE!", 30);
        doCompletion(player, job);
      }
    }
  }

  private void doCompletion(Player player, Job job) {
    if (job.getTemplate().getCompletionNpc() != -1) {
      NPC npc = CitizensAPI.getNPCRegistry().getById(job.getTemplate().getCompletionNpc());
      if (npc == null) {
        Bukkit.getLogger().warning("[Jobbo] NPC for completion of job " +
            job.getTemplate().getId() + " was missing, granted awards directly! Fix it tho!");
        awardPlayer(player, job);
        return;
      }
      if (JobboPlugin.isWaypointerEnabled()) {
        Location wpLoc = npc.getStoredLocation().clone().add(0, 3, 0);
        WaypointerPlugin.getInstance().getWaypointManager()
            .setWaypoint(player, "Job Turn-in", wpLoc);
      }
      player.playSound(player.getLocation(), Sound.UI_TOAST_IN, 5, 1);
      MessageUtils.sendMessage(player, "&2Job Task Finished! &aInform the person who issued" +
          " the job that you've finished it for your reward!");
    } else {
      awardPlayer(player, job);
    }
  }

  public void awardPlayer(Player player, Job job) {
    JobCompleteEvent jobCompleteEvent = new JobCompleteEvent(player, job);
    Bukkit.getPluginManager().callEvent(jobCompleteEvent);
    clearJobCooldown(player);
    if (StringUtils.isNotBlank(job.getTemplate().getCompletionMessage())) {
      MessageUtils.sendMessage(player, job.getTemplate().getCompletionMessage());
    }
    MessageUtils.sendMessage(player,
        "&2&lJOB COMPLETE! &aWell done! You can now accept a new job at the job board!");
    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 5, 2);
    acceptedJobs.remove(player.getUniqueId(), job);
    if (JobboPlugin.isStrifeEnabled()) {
      StrifePlugin.getInstance().getExperienceManager().addExperience(
          player, job.getXp(), true);
      for (LifeSkillType skill : job.getTemplate().getSkillXpReward().keySet()) {
        StrifePlugin.getInstance().getSkillExperienceManager().addExperience(
            player, skill, job.getTemplate().getSkillXpReward().get(skill), true, true);
      }
    } else {
      player.giveExp(job.getXp(), false);
    }
    if (plugin.getEconomy() != null) {
      plugin.getEconomy().depositPlayer(player, job.getMoney());
    }
    Map<Integer, ItemStack> remainder = player.getInventory()
        .addItem(job.getItemRewards().toArray(new ItemStack[0]));
    for (ItemStack stack : remainder.values()) {
      Item itemEntity = player.getWorld().dropItemNaturally(player.getLocation(), stack);
      itemEntity.setOwner(player.getUniqueId());
    }
    // give money
    // give gems
    // give guild xp?
  }

  public boolean isJobComplete(Player player) {
    return hasJob(player) && acceptedJobs.get(player.getUniqueId()).isCompleted();
  }

  public PostedJob getJobPosting(Location location) {
    for (JobBoard board : jobBoards) {
      for (PostedJob postedJob : board.getJobListings()) {
        if (location.equals(postedJob.getLocation())) {
          return postedJob;
        }
      }
    }
    return null;
  }

  public boolean acceptListing(Player player, Job job) {
    if (acceptedJobs.containsKey(player.getUniqueId())) {
      MessageUtils.sendMessage(player, ChatColor.RED + "Failed to accept! You already have a job!");
      return false;
    }
    JobAcceptEvent jobAcceptEvent = new JobAcceptEvent(player, job);
    Bukkit.getPluginManager().callEvent(jobAcceptEvent);
    if (jobAcceptEvent.isCancelled()) {
      return false;
    }
    for (PostedJob postedJob : job.getBoard().getJobListings()) {
      if (postedJob.getJob() == job) {
        acceptedJobs.put(player.getUniqueId(), job);
        postedJob.setJob(null);
        postedJob.setSeconds(315);
        updatePostingSign(job.getBoard(), postedJob);
        TitleUtils.sendTitle(player,
            StringExtensionsKt.chatColorize("&2JOB ACCEPTED"),
            StringExtensionsKt.chatColorize("&eGET THAT BREADDDDD"));
        MessageUtils.sendMessage(player,
            "&2&lJOB ACCEPTED! &aCheck &e/job &afor status and waypoints!");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_WORK_CARTOGRAPHER, 5, 1);
        abandonCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 300000);
        if (JobboPlugin.isWaypointerEnabled() && job.getTemplate().getLocation() != null) {
          WaypointerPlugin.getInstance().getWaypointManager().setWaypoint(player,
              ChatColor.stripColor(job.getTemplate().getJobName()),
              job.getTemplate().getLocation());
        }
        return true;
      }
    }
    return false;
  }

  public Job buildJobInstance(JobBoard board, PostedJob postedJob) {
    if (board.getTemplateIds().isEmpty()) {
      Bukkit.getLogger().warning("Board " + board.getId() + " cannot post. No templates!");
      return null;
    }
    String templateId;
    if (board.getTemplateIds().size() == 1) {
      templateId = board.getTemplateIds().get(0);
    } else {
      templateId = board.getTemplateIds().get(random.nextInt(board.getTemplateIds().size()));
    }
    JobTemplate selectedTemplate = loadedTemplates.get(templateId);
    while (Math.random() < selectedTemplate.getRerollChance()) {
      templateId = board.getTemplateIds().get(random.nextInt(board.getTemplateIds().size()));
      selectedTemplate = loadedTemplates.get(templateId);
    }
    Job job = selectedTemplate.generateJobInstance(board);

    JobGenerationEvent jobGenerationEvent = new JobGenerationEvent(job);
    Bukkit.getPluginManager().callEvent(jobGenerationEvent);
    if (jobGenerationEvent.isCancelled()) {
      if (postedJob != null) {
        postedJob.setJob(null);
        postedJob.setSeconds(10);
      }
      return null;
    }
    return job;
  }

  public void tickListings() {
    for (JobBoard board : jobBoards) {
      for (PostedJob postedJob : board.getJobListings()) {
        if (postedJob.getSeconds() == 0) {
          Job job = buildJobInstance(board, postedJob);
          if (job == null) {
            continue;
          }
          postedJob.setJob(job);
          postedJob.setSeconds(300 + (int) (35 * Math.random()));
          updatePostingSign(board, postedJob);
          continue;
        } else if (postedJob.getSeconds() % 60 == 0) {
          updatePostingSign(board, postedJob);
        }
        postedJob.setSeconds(postedJob.getSeconds() - 1);
      }
    }
  }

  public void tickPostingSigns() {
    for (JobBoard board : jobBoards) {
      for (PostedJob postedJob : board.getJobListings()) {
        postedJob.attemptSignUpdate();
      }
    }
  }

  private void updatePostingSign(JobBoard board, PostedJob postedJob) {
    PendingSignData signData = new PendingSignData();
    if (postedJob.getJob() == null) {
      signData.setLineOne(Component.text(""));
      signData.setLineTwo(jobAccepted);
      signData.setLineThree(newJob.get(postedJob.getSeconds() / 60));
      signData.setLineFour(Component.text(""));
    } else {
      JobTemplate template = postedJob.getJob().getTemplate();
      signData.setLineOne(Component.text(postedJob.getJob().getTaskType() + " JOB")
          .color(TextColor.color(0, 204, 0)).decoration(TextDecoration.BOLD, true));
      signData.setLineTwo(starRating.get(template.getDifficulty()));
      signData.setLineThree(timeLeft.get(postedJob.getSeconds() / 60));
      signData.setLineFour(clickForInfo);
    }
    postedJob.setPendingSignData(signData);
    postedJob.attemptSignUpdate();
  }

  public void saveBoards() {
    try (FileWriter writer = new FileWriter(plugin.getDataFolder() + "/boards.json")) {
      GsonFactory.getCompactGson().toJson(jobBoards, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadBoards() {
    jobBoards.clear();
    try (FileReader reader = new FileReader(plugin.getDataFolder() + "/boards.json")) {
      JsonArray array = GsonFactory.getCompactGson().fromJson(reader, JsonArray.class);
      for (JsonElement e : array) {
        JobBoard board = GsonFactory.getCompactGson().fromJson(e, JobBoard.class);
        jobBoards.add(board);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Needs to refresh/re-cache stored chunk keys
    for (JobBoard board : jobBoards) {
      for (PostedJob postedJob : board.getJobListings()) {
        postedJob.setLocation(postedJob.getLocation());
      }
    }
  }

  public void loadTemplates(VersionedSmartYamlConfiguration file) {
    loadedTemplates.clear();
    for (String key : file.getKeys(false)) {
      String jobType = file.getString(key + ".type", "KILL");
      String townId = file.getString(key + ".town-id", "NONE");
      int difficulty = file.getInt(key + ".difficulty", 1);
      JobTemplate jobTemplate = new JobTemplate(key, townId, jobType, difficulty);

      jobTemplate.setDataStringOne(file.getString(key + ".data-string-one", null));
      jobTemplate.setDataStringTwo(file.getString(key + ".data-string-two", null));

      jobTemplate.setTaskCap(file.getInt(key + ".task-amount", 1));
      jobTemplate.setBonusCap(file.getInt(key + ".task-amount-bonus", 0));
      jobTemplate.setMoneyReward(file.getInt(key + ".money-reward", 0));
      jobTemplate.setBonusMoney(file.getInt(key + ".money-reward-bonus", 0));
      jobTemplate.setXpReward(file.getInt(key + ".xp-reward", 0));
      jobTemplate.setBonusXp(file.getInt(key + ".xp-reward-bonus", 0));
      if (JobboPlugin.isStrifeEnabled()) {
        if (file.isConfigurationSection(key + ".life-skill-xp")) {
          Map<LifeSkillType, Float> skillmap = StatUtil.getSkillMapFromSection(file
              .getConfigurationSection(key + ".life-skill-xp"));
          for (Entry<LifeSkillType, Float> entry : skillmap.entrySet()) {
            jobTemplate.getSkillXpReward().put(entry.getKey(), Math.round(entry.getValue()));
          }
        }
      }

      jobTemplate.setWorldName(file.getString(key + ".waypoint.world", null));
      jobTemplate.setX(file.getDouble(key + ".waypoint.x", 0));
      jobTemplate.setY(file.getDouble(key + ".waypoint.y", 0));
      jobTemplate.setZ(file.getDouble(key + ".waypoint.z", 0));

      jobTemplate.setRerollChance(file.getDouble(key + ".reroll-chance", 0));

      jobTemplate.setCompletionNpc(file.getInt(key + ".completion-npc", -1));
      jobTemplate.setCompletionMessage(StringExtensionsKt
          .chatColorize(file.getString(key + ".completion-message", "")));

      jobTemplate.setJobName(StringExtensionsKt.chatColorize(
          file.getString(key + ".name", "Generic Job")));
      jobTemplate.getDescription().clear();
      jobTemplate.getDescription().addAll(ListExtensionsKt.chatColorize(
          file.getStringList(key + ".description")));
      jobTemplate.buildLocation();

      loadedTemplates.put(key, jobTemplate);
    }
    Bukkit.getLogger().info("[Jobbo] Loaded " + loadedTemplates.size() + " job templates!");
  }
}
