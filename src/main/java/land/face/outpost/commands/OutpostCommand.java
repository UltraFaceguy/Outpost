package land.face.outpost.commands;

import static com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils.sendMessage;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.acf.BaseCommand;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandAlias;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandPermission;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.Default;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.Subcommand;
import com.tealcube.minecraft.bukkit.shade.acf.bukkit.contexts.OnlinePlayer;
import java.util.List;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import land.face.outpost.data.Position;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("outpost|outposts")
public class OutpostCommand extends BaseCommand {

  private final OutpostPlugin plugin;

  public OutpostCommand(OutpostPlugin plugin) {
    this.plugin = plugin;
  }

  @Subcommand("reload")
  @CommandPermission("outpost.reload")
  public void reload(CommandSender sender) {
    plugin.onDisable();
    plugin.onEnable();
  }

  @Default
  @Subcommand("status")
  @CommandPermission("outpost.status")
  public void status(Player sender) {
    plugin.getStatusMenu().open(sender);
  }

  @Subcommand("set")
  @CommandPermission("outpost.set")
  public void set(OnlinePlayer p, String name, int life, int barrier) {
    Player sender = p.getPlayer();
    Position pos1 = new Position(sender.getLocation().getBlockX() + 20,
        sender.getLocation().getBlockY() + 20,
        sender.getLocation().getBlockZ() + 20);
    Position pos2 = new Position(sender.getLocation().getBlockX() - 20,
        sender.getLocation().getBlockY() - 20,
        sender.getLocation().getBlockZ() - 20);
    Outpost outpost = plugin.getOutpostManager().createOutpost(name);
    outpost.setMaxLife(life);
    outpost.setMaxBarrier(barrier);
    outpost.setWorld(sender.getWorld().getName());
    outpost.setPos1(pos1);
    outpost.setPos2(pos2);
    outpost.setPvpPos1(pos1);
    outpost.setPvpPos2(pos2);
  }

  @Subcommand("list")
  @CommandPermission("outpost.list")
  public void list(CommandSender sender) {
    List<String> ids = plugin.getOutpostManager().getOutpostIds();
    StringBuilder list = new StringBuilder();
    for (String s : ids) {
      list.append(s).append(" ");
    }
    MessageUtils.sendMessage(sender, list.toString());
  }

  @Subcommand("remove")
  @CommandPermission("outpost.edit")
  public void remove(CommandSender sender, String id) {
    plugin.getOutpostManager().removeOutpost(id);
  }

  @Subcommand("addSpawner")
  @CommandPermission("outpost.edit")
  public void addSpawner(CommandSender sender, String id, String spawner) {
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    outpost.getSpawnerIds().add(spawner);
  }

  @Subcommand("removeSpawner")
  @CommandPermission("outpost.edit")
  public void removeSpawner(CommandSender sender, String id, String spawner) {
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    outpost.getSpawnerIds().remove(spawner);
  }

  @Subcommand("reward")
  @CommandPermission("outpost.reward")
  public void reward(CommandSender sender, String id, int reward) {
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    if (outpost == null) {
      MessageUtils.sendMessage(sender, "specified outpost does not exist");
      return;
    }
    MessageUtils.sendMessage(sender, "rewardset!");
    outpost.setMinimumCashReward(reward);
  }

  @Subcommand("setLife")
  @CommandPermission("outpost.edit")
  public void setLife(CommandSender sender, String id, int amount) {
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    if (outpost == null) {
      MessageUtils.sendMessage(sender, "specified outpost does not exist");
      return;
    }
    MessageUtils.sendMessage(sender, "Set max life to " + amount);
    outpost.setMaxLife(amount);
  }

  @Subcommand("setBarrier")
  @CommandPermission("outpost.edit")
  public void setBarrier(CommandSender sender, String id, int amount) {
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    if (outpost == null) {
      MessageUtils.sendMessage(sender, "specified outpost does not exist");
      return;
    }
    MessageUtils.sendMessage(sender, "Set max barrier to " + amount);
    outpost.setMaxBarrier(amount);
  }

  @Subcommand("setWaypoint")
  @CommandPermission("outpost.edit")
  public void setBarrier(CommandSender sender, String id, String waypointId) {
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    if (outpost == null) {
      MessageUtils.sendMessage(sender, "specified outpost does not exist");
      return;
    }
    MessageUtils.sendMessage(sender, "Set waypoint to " + waypointId);
    outpost.setWaypoint(waypointId);
  }

  @Subcommand("pos1")
  @CommandPermission("outpost.edit")
  public void pos1(OnlinePlayer p, String id) {
    Player sender = p.getPlayer();
    Position pos = new Position(sender.getLocation().getBlockX(), sender.getLocation().getBlockY(),
        sender.getLocation().getBlockZ());
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    if (outpost == null) {
      MessageUtils.sendMessage(sender, "specified outpost does not exist");
      return;
    }
    outpost.setPos1(pos);
    sendMessage(sender, "&aset pos1");
  }

  @Subcommand("pos2")
  @CommandPermission("outpost.edit")
  public void pos2(Player p, String id) {
    Player sender = p.getPlayer();
    Position pos = new Position(sender.getLocation().getBlockX(), sender.getLocation().getBlockY(),
        sender.getLocation().getBlockZ());
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    if (outpost == null) {
      MessageUtils.sendMessage(sender, "specified outpost does not exist");
      return;
    }
    outpost.setPos2(pos);
    sendMessage(sender, "&aset pos2");
  }

  @Subcommand("pvp1")
  @CommandPermission("outpost.edit")
  public void pvppos1(OnlinePlayer p, String id) {
    Player sender = p.getPlayer();
    Position pos = new Position(sender.getLocation().getBlockX(), sender.getLocation().getBlockY(),
        sender.getLocation().getBlockZ());
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    if (outpost == null) {
      MessageUtils.sendMessage(sender, "specified outpost does not exist");
      return;
    }
    outpost.setPvpPos1(pos);
    sendMessage(sender, "&aset pvp pos1");
  }

  @Subcommand("pvp2")
  @CommandPermission("outpost.edit")
  public void pvppos2(Player p, String id) {
    Player sender = p.getPlayer();
    Position pos = new Position(sender.getLocation().getBlockX(), sender.getLocation().getBlockY(),
        sender.getLocation().getBlockZ());
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    if (outpost == null) {
      MessageUtils.sendMessage(sender, "specified outpost does not exist");
      return;
    }
    outpost.setPvpPos2(pos);
    sendMessage(sender, "&aset pvp pos2");
  }

  @Subcommand("bannerdebug")
  @CommandPermission("outpost.banner.debug")
  public void debugOutpostBanner(Player player, String bannerCode) {
    if (bannerCode.contains("https")){
      String segments[] = bannerCode.split("=");
      bannerCode = segments[segments.length - 1];
    }
    plugin.getGuildBannerManager().setBannersInArea(player.getLocation(), 32, bannerCode);
  }
}