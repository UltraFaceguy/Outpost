package land.face.outpost.commands;

import static com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils.sendMessage;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import land.face.outpost.data.Position;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;

public class BaseCommand {

  private OutpostPlugin plugin;

  public BaseCommand(OutpostPlugin plugin) {
    this.plugin = plugin;
  }

  @Command(identifier = "outpost reload", permissions = "outpost.set", onlyPlayers = false)
  public void set(CommandSender sender) {
    plugin.onDisable();
    plugin.onEnable();
  }

  @Command(identifier = "outpost status", permissions = "outpost.status", onlyPlayers = true)
  public void status(Player sender) {
    plugin.getStatusMenu().open(sender);
  }

  @Command(identifier = "outpost set", permissions = "outpost.set", onlyPlayers = true)
  public void set(Player sender, @Arg(name = "name") String name, @Arg(name = "life") int life, @Arg(name = "barrier") int barrier) {
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

  @Command(identifier = "outpost list", permissions = "outpost.view", onlyPlayers = false)
  public void list(CommandSender sender) {
    List<String> ids = plugin.getOutpostManager().getOutpostIds();
    StringBuilder list = new StringBuilder();
    for (String s : ids) {
      list.append(s).append(" ");
    }
    MessageUtils.sendMessage(sender, list.toString());
  }

  @Command(identifier = "outpost remove", permissions = "outpost.edit", onlyPlayers = false)
  public void remove(CommandSender sender, @Arg(name = "outpostId") String id) {
    plugin.getOutpostManager().removeOutpost(id);
  }

  @Command(identifier = "outpost reward", permissions = "outpost.edit", onlyPlayers = false)
  public void reward(CommandSender sender, @Arg(name = "outpostId") String id,
      @Arg(name = "reward") int reward) {
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    if (outpost == null) {
      MessageUtils.sendMessage(sender, "specified outpost does not exist");
      return;
    }
    MessageUtils.sendMessage(sender, "rewardset!");
    outpost.setMinimumCashReward(reward);
  }

  @Command(identifier = "outpost setlife", permissions = "outpost.edit", onlyPlayers = false)
  public void setLife(CommandSender sender, @Arg(name = "outpostId") String id,
      @Arg(name = "amount") int amount) {
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    if (outpost == null) {
      MessageUtils.sendMessage(sender, "specified outpost does not exist");
      return;
    }
    MessageUtils.sendMessage(sender, "Set max life to " + amount);
    outpost.setMaxLife(amount);
  }

  @Command(identifier = "outpost setBarrier", permissions = "outpost.edit", onlyPlayers = false)
  public void setBarrier(CommandSender sender, @Arg(name = "outpostId") String id,
      @Arg(name = "amount") int amount) {
    Outpost outpost = plugin.getOutpostManager().getOutpost(id);
    if (outpost == null) {
      MessageUtils.sendMessage(sender, "specified outpost does not exist");
      return;
    }
    MessageUtils.sendMessage(sender, "Set max barrier to " + amount);
    outpost.setMaxBarrier(amount);
  }

  @Command(identifier = "outpost pos1", permissions = "outpost.edit", onlyPlayers = true)
  public void pos1(Player sender, @Arg(name = "outpostId") String id) {
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

  @Command(identifier = "outpost pos2", permissions = "outpost.edit", onlyPlayers = true)
  public void pos2(Player sender, @Arg(name = "outpostId") String id) {
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

  @Command(identifier = "outpost pvp1", permissions = "outpost.edit", onlyPlayers = true)
  public void pvppos1(Player sender, @Arg(name = "outpostId") String id) {
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

  @Command(identifier = "outpost pvp2", permissions = "outpost.edit", onlyPlayers = true)
  public void pvppos2(Player sender, @Arg(name = "outpostId") String id) {
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
}