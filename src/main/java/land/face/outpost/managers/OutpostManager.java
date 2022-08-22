package land.face.outpost.managers;

import static land.face.outpost.OutpostPlugin.INT_FORMAT;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.google.gson.Gson;
import com.tealcube.minecraft.bukkit.shade.google.gson.JsonArray;
import com.tealcube.minecraft.bukkit.shade.google.gson.JsonElement;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import land.face.outpost.data.Outpost.OutpostState;
import land.face.outpost.data.Position;
import land.face.outpost.events.OutpostCaptureEvent;
import land.face.strife.StrifePlugin;
import land.face.strife.data.Spawner;
import land.face.strife.data.StrifeMob;
import land.face.strife.data.UniqueEntity;
import land.face.strife.managers.GuiManager;
import me.glaremasters.guilds.guild.Guild;
import me.glaremasters.guilds.guild.GuildMember;
import me.glaremasters.guilds.guild.GuildRolePerm;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

public class OutpostManager {

  private final OutpostPlugin plugin;
  private final Map<String, Outpost> outposts = new HashMap<>();
  private final Map<String, String> uniqueIdToOutpost = new HashMap<>();
  private final Gson gson = new Gson();

  private TextChannel outpostChannel;

  private final String attackMsg = TextUtils.color(
      "&c[&7Outpost&c] &7Your controlled outpost &c{name} &7is under attack!");
  private final String contestMsg = TextUtils.color(
      "&c[&7Outpost&c] &7This outpost is contested! Clear out enemy players!");
  private final String captureMsg = TextUtils.color(
      "&c[&7Outpost&c] &7The guild &a{gname} &7has captured &c{oname}&7!");
  private final String discordCaptureAnnouncement = ":crossed_swords: **{gname} has captured {oname}!**";
  private final String discordCaptureDM = "**GAMER ALERT!!** Your guild's outpost **{oname}** has been captured by **{gname}**";
  private final String discordAttackDm = "**GAMER ALERT!!** Your guild's outpost **{oname}** is under attack!";
  private final String cappedDepositDm = "**GAMER ALERT!!** You have gained funds from owning an outpost but your guild's bank could not contain all of them! Your guild bank is now full. Please login to spend or withdraw some Bits so you can keep raking in that sweet moolah!";
  private final String normalDepositMsg = TextUtils.color(
      "&e[&7Outpost&e] &7Your guild bank has been credited &e{amount} Bits &7in earnings from outposts. Total: {total} Bits");
  private String cappedDepositMsg = TextUtils.color(
      "&e[&7Outpost&e] &7Your guild bank has been credited &e{amount} Bits &7in earnings from outposts. Your guild bank is now full! Please spend or withdraw funds! Total: {total} Bits");

  public OutpostManager(OutpostPlugin plugin) {
    this.plugin = plugin;
    String channelId = plugin.getSettings().getString("config.outpost-channel-id", "42069");
    outpostChannel = DiscordUtil.getTextChannelById(channelId);
  }

  public Outpost getOutpost(String id) {
    return outposts.get(id);
  }

  public List<String> getOutpostIds() {
    return new ArrayList<>(outposts.keySet());
  }

  public List<Outpost> getOutposts() {
    return new ArrayList<>(outposts.values());
  }

  public Outpost createOutpost(String name) {

    String id = name.toLowerCase().replaceAll(" ", "-");

    Outpost outpost = new Outpost(id);
    outpost.setName(name.replaceAll("_", " "));
    outpost.setSpawnerIds(new HashSet<>());
    outpost.setMaxBarrier(750);
    outpost.setMaxLife(1250);
    outpost.setGuildId(null);

    outposts.put(outpost.getId(), outpost);
    return outpost;
  }

  public void removeOutpost(String id) {
    outposts.remove(id);
  }

  public void payout(double payoutRatio) {
    Bukkit.getLogger().info("Sending outpost payments...");
    Map<Guild, Double> earnings = new HashMap<>();
    for (Outpost o : outposts.values()) {
      if (o.getGuild() == null) {
        continue;
      }
      double amount = (double) o.getMinimumCashReward() * payoutRatio;
      amount += o.getCollectedTaxes();
      o.setCollectedTaxes(0);
      o.setLastPayment((int) amount);
      earnings.put(o.getGuild(), earnings.getOrDefault(o.getGuild(), 0D) + amount);
    }
    for (Guild guild : earnings.keySet()) {

      double balance = guild.getBalance();
      double maxBalance = guild.getTier().getMaxBankBalance();

      int cash = (int) Math.ceil(earnings.get(guild));
      boolean wasAlreadyFull = balance == maxBalance;
      boolean capped = balance + cash >= maxBalance;
      guild.setBalance(Math.min(balance + cash, maxBalance));

      informPayout(guild, cash, capped, capped && !wasAlreadyFull);
    }
    Bukkit.getLogger().info("Outpost payments complete!");
  }

  public void tickOutpost(Outpost o, int ticks) {

    Set<Player> playersOnOutpost = getPlayersOnOutpost(o);

    if (o.getProtectTime() > System.currentTimeMillis()) {
      o.setTitleBar(ChatColor.GOLD + "" + ChatColor.BOLD + o.getGuild().getName() +
          ChatColor.GREEN + "" + ChatColor.BOLD + " PROTECTED");
      updateBars(o, playersOnOutpost);
      o.setState(OutpostState.PROTECTED);
      return;
    }

    if (playersOnOutpost.isEmpty()) {
      o.setBarrier(Math.min(o.getMaxBarrier(), o.getBarrier() + o.getMaxBarrier() / 100));
      o.setState(OutpostState.OPEN);
      return;
    }

    Map<Guild, Integer> contestingGuilds = getGuildsOnOutpost(o, playersOnOutpost);

    if (contestingGuilds.isEmpty()) {
      o.setBarrier(Math.min(o.getMaxBarrier(), o.getBarrier() + o.getMaxBarrier() / 100));
      o.setState(OutpostState.OPEN);
      return;
    }

    Guild owningGuild = o.getGuild();

    o.setTitleBar(ChatColor.GOLD + "" + ChatColor.BOLD + o.getName() +
        ChatColor.WHITE + "  " + INT_FORMAT.format(o.getBarrier()) + "♡ " +
        ChatColor.RED + INT_FORMAT.format(o.getLife()) + "♡");

    boolean isOwnerDefending = o.getGuild() != null && contestingGuilds.containsKey(owningGuild);

    if (isOwnerDefending) {
      if (contestingGuilds.size() == 1) {
        o.setBarrier(Math.min(o.getMaxBarrier(), o.getBarrier() + o.getMaxBarrier() / 100));
        o.setState(OutpostState.DEFENDED);
        updateBars(o, playersOnOutpost);
        return;
      }
      o.setState(OutpostState.CONTESTED);
      if (ticks % 25 == 0) {
        for (Player p : playersOnOutpost) {
          MessageUtils.sendMessage(p, contestMsg);
        }
      }
      if (o.getGuild() != null && ticks % 200 == 0) {
        for (Player p : o.getGuild().getOnlineAsPlayers()) {
          MessageUtils.sendMessage(p, attackMsg.replace("{name}", o.getName()));
        }
      }
      updateBars(o, playersOnOutpost);
      return;
    }

    float damage = 0;
    for (Guild g : contestingGuilds.keySet()) {
      if (g != owningGuild) {
        damage += contestingGuilds.get(g);
      }
    }
    damage = Math.min(damage, 4);

    o.damage(damage);
    o.setState(OutpostState.CONTESTED);

    if (o.getBarrier() / o.getMaxBarrier() < 0.75 && System.currentTimeMillis() > o
        .getAttackAlertDmCooldown() && o.getGuild() != null) {
      dmGuildMembers(o.getGuild(), discordAttackDm.replace("{oname}", o.getName()));
      o.setAttackAlertDmCooldown(System.currentTimeMillis() + 1500000);
    }

    if (o.getGuild() != null && ticks % 200 == 0) {
      for (Player p : o.getGuild().getOnlineAsPlayers()) {
        MessageUtils.sendMessage(p, attackMsg.replace("{name}", o.getName()));
      }
    }

    if (o.getLife() < 1) {
      if (contestingGuilds.size() > 1) {
        o.setLife(1);
      } else {
        Guild capGuild = contestingGuilds.keySet().iterator().next();
        captureOutpost(o, capGuild);
      }
    }
    updateBars(o, playersOnOutpost);
  }

  public void tickOutposts(int ticks) {
    for (Outpost o : outposts.values()) {
      tickOutpost(o, ticks);
    }
  }

  private void updateBars(Outpost o, Set<Player> playersOnOutpost) {
    if (playersOnOutpost.isEmpty()) {
      return;
    }

    double progress = (double) o.getLife() / o.getMaxLife();
    int stage = (int) (138D * progress);
    int barrierState = (int) ((138f * o.getBarrier()) / o.getMaxBarrier());
    String s = "\uD806\uDCB9" + GuiManager.HEALTH_BAR_TARGET.get(138 - stage);
    if (barrierState > 0) {
       s += GuiManager.BARRIER_BAR_TARGET.get(barrierState);
    }

    for (Player p : playersOnOutpost) {
      StrifePlugin.getInstance().getBossBarManager().updateBar(p, 3, 2, o.getTitleBar(), 30);
      StrifePlugin.getInstance().getBossBarManager().updateBar(p, 2, 2, s, 30);
    }
  }

  public void captureOutpost(Outpost outpost, Guild newGuild) {
    if (outpostChannel != null) {
      DiscordUtil.sendMessage(outpostChannel, discordCaptureAnnouncement
          .replace("{gname}", newGuild.getName())
          .replace("{oname}", outpost.getName()));
    }

    Bukkit.getServer().broadcastMessage(captureMsg
        .replace("{gname}", newGuild.getName())
        .replace("{oname}", outpost.getName()));

    if (outpost.getGuild() != null) {
      String capDM = discordCaptureDM
          .replace("{gname}", newGuild.getName())
          .replace("{oname}", outpost.getName());
      dmGuildMembers(outpost.getGuild(), capDM);
    }

    long protectTime = plugin.getSettings().getLong("config.capture-protection-ms", 82800000L);
    outpost.setProtectTime(System.currentTimeMillis() + protectTime);
    outpost.setAttackAlertDmCooldown(1L);
    outpost.setCanRally(true);
    outpost.setGuildId(newGuild.getId().toString());
    outpost.setGuild(newGuild);
    outpost.setLife(outpost.getMaxLife());
    outpost.setBarrier(outpost.getMaxBarrier());
    outpost.setState(OutpostState.PROTECTED);

    OutpostCaptureEvent event = new OutpostCaptureEvent(outpost);
    Bukkit.getPluginManager().callEvent(event);

    for (String s : outpost.getSpawnerIds()) {
      Spawner spawner = StrifePlugin.getInstance().getSpawnerManager().getSpawnerMap().get(s);
      UniqueEntity ue = spawner.getUniqueEntity();
      for (LivingEntity le : spawner.getEntities()) {
        StrifeMob mob = StrifePlugin.getInstance().getStrifeMobManager().getMobs().get(le);
        mob.setAlliedGuild(newGuild.getId());
        if (le instanceof Mob) {
          ((Mob) le).setTarget(null);
        }
        le.getWorld().playSound(le.getLocation(),
            Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 1, (float) (0.8 + (Math.random() * 0.4)));
        le.setCustomName(ChatColor.GOLD + "[" + newGuild.getPrefix() + "] " + ue.getName());
      }
    }
  }

  public void dmGuildMembers(Guild guild, String message) {
    List<UUID> guildMembers = guild.getMembers().stream().map(GuildMember::getUuid).toList();
    for (UUID uuid : guildMembers) {
      String id = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);
      if (StringUtils.isBlank(id)) {
        continue;
      }
      User user = DiscordUtil.getJda().getUserById(id);
      if (user == null) {
        continue;
      }
      DiscordUtil.privateMessage(user, message);
    }
  }

  public void informPayout(Guild guild, int amount, boolean capped, boolean alert) {
    List<GuildMember> members = guild.getMembers();
    String message;
    if (capped) {
      message = cappedDepositMsg
          .replace("{amount}", Integer.toString(amount))
          .replace("{total}", INT_FORMAT.format(guild.getBalance()));
    } else {
      message = normalDepositMsg
          .replace("{amount}", Integer.toString(amount))
          .replace("{total}", INT_FORMAT.format(guild.getBalance()));
    }
    List<UUID> discordDmUuids = new ArrayList<>();
    for (GuildMember member : members) {
      if (member.getRole().hasPerm(GuildRolePerm.WITHDRAW_MONEY)) {
        discordDmUuids.add(member.getUuid());
      }
      if (member.isOnline()) {
        MessageUtils.sendMessage(member.getAsPlayer(), message);
      }
    }
    if (capped && alert) {
      for (UUID uuid : discordDmUuids) {
        String id = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(uuid);
        if (StringUtils.isBlank(id)) {
          continue;
        }
        User user = DiscordUtil.getJda().getUserById(id);
        if (user == null) {
          continue;
        }
        DiscordUtil.privateMessage(user, cappedDepositDm);
      }
    }
  }

  public Set<Player> getPlayersOnOutpost(Outpost outpost) {
    Set<Player> players = new HashSet<>();
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (Position.isWithin(outpost, p.getLocation(), outpost.getPos1(), outpost.getPos2())) {
        players.add(p);
      }
    }
    return players;
  }

  public Map<Guild, Integer> getGuildsOnOutpost(Outpost outpost, Set<Player> players) {
    Map<Guild, Integer> capPlayerCount = new HashMap<>();
    for (Player p : players) {
      Guild guild = plugin.getGuildsAPI().getGuild(p);
      if (guild == null) {
        continue;
      }
      if (outpost.getGuild() != null && outpost.getGuild().getAllies().contains(guild.getId())) {
        guild = outpost.getGuild();
      }
      capPlayerCount.put(guild, capPlayerCount.getOrDefault(guild, 0) + 1);
    }
    return capPlayerCount;
  }

  public Map<String, String> getUniqueIdToOutpost() {
    return uniqueIdToOutpost;
  }

  public void saveOutposts() {
    try (FileWriter writer = new FileWriter(plugin.getDataFolder() + "/outposts.json")) {
      gson.toJson(outposts.values(), writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadOutposts() {
    try (FileReader reader = new FileReader(plugin.getDataFolder() + "/outposts.json")) {
      JsonArray array = gson.fromJson(reader, JsonArray.class);
      for (JsonElement e : array) {
        Outpost outpost = gson.fromJson(e, Outpost.class);
        if (outpost.getGuildId() != null) {
          outpost.setGuild(plugin.getGuildsAPI().getGuildHandler()
              .getGuild(UUID.fromString(outpost.getGuildId())));
        } else {
          outpost.setGuild(null);
        }
        if (outpost.getSpawnerIds() == null) {
          outpost.setSpawnerIds(new HashSet<>());
        }
        outpost.setState(OutpostState.OPEN);
        outpost.setAttackAlertDmCooldown(1L);
        outpost.setCanRally(true);
        outposts.put(outpost.getId(), outpost);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
