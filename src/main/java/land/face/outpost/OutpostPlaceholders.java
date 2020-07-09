package land.face.outpost;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import land.face.outpost.data.Outpost;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.glaremasters.guilds.guild.Guild;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OutpostPlaceholders extends PlaceholderExpansion {

  private OutpostPlugin plugin;

  @Override
  public boolean register() {
    if (!canRegister()) {
      return false;
    }
    plugin = OutpostPlugin.getInstance();
    return PlaceholderAPI.registerPlaceholderHook(getIdentifier(), this);
  }

  @Override
  public String getAuthor() {
    return "Faceguy";
  }

  @Override
  public String getIdentifier() {
    return "outpost";
  }

  @Override
  public String getVersion() {
    return "1.0.0";
  }

  @Override
  public String onPlaceholderRequest(Player p, String identifier) {
    if (p == null || StringUtils.isBlank(identifier)) {
      return "";
    }
    if (identifier.startsWith("owner")) {
      Outpost outpost = plugin.getOutpostManager().getOutpost(identifier.replace("owner_", ""));
      if (outpost.getGuild() == null) {
        return ChatColor.GRAY + "<Unowned>";
      }
      return outpost.getGuild().getName();
    }
    if (identifier.startsWith("owned")) {
      Guild guild = plugin.getGuildsAPI().getGuild(p);
      if (guild == null) {
        return "0";
      }
      int count = 0;
      for (Outpost o : plugin.getOutpostManager().getOutposts()) {
        if (o.getGuild() == guild) {
          count++;
        }
      }
      return Integer.toString(count);
    }
    if (identifier.startsWith("income")) {
      Guild guild = plugin.getGuildsAPI().getGuild(p);
      if (guild == null) {
        return "0";
      }
      int income = 0;
      for (Outpost o : plugin.getOutpostManager().getOutposts()) {
        if (o.getGuild() == guild) {
          income += o.getLastPayment();
        }
      }
      return Integer.toString(income);
    }
    return null;
  }
}
