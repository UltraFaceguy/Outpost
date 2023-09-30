package land.face.outpost;

import com.soujah.poggersguilds.data.Guild;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import land.face.outpost.data.Outpost;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class OutpostPlaceholders extends PlaceholderExpansion {

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
  public String onPlaceholderRequest(Player p, @NotNull String placeholder) {
    if (StringUtils.isBlank(placeholder)) {
      return "";
    }
    if (placeholder.startsWith("owner")) {
      Outpost outpost = OutpostPlugin.getInstance().getOutpostManager().getOutpost(placeholder.replace("owner_", ""));
      if (outpost.getGuild() == null) {
        return ChatColor.GRAY + "<Unowned>";
      }
      return outpost.getGuild().getName();
    }
    if (placeholder.startsWith("owned")) {
      Guild guild = OutpostPlugin.getInstance().getGuildsAPI().getGuild(p);
      if (guild == null) {
        return "0";
      }
      int count = 0;
      for (Outpost o : OutpostPlugin.getInstance().getOutpostManager().getOutposts()) {
        if (o.getGuild() == guild) {
          count++;
        }
      }
      return Integer.toString(count);
    }
    if (placeholder.startsWith("income")) {
      Guild guild = OutpostPlugin.getInstance().getGuildsAPI().getGuild(p);
      if (guild == null) {
        return "0";
      }
      int income = 0;
      for (Outpost o : OutpostPlugin.getInstance().getOutpostManager().getOutposts()) {
        if (o.getGuild() == guild) {
          income += o.getLastPayment();
        }
      }
      return Integer.toString(income);
    }
    return null;
  }
}
