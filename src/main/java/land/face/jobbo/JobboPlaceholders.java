package land.face.jobbo;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JobboPlaceholders extends PlaceholderExpansion {

  @Override
  public @NotNull String getAuthor() {
    return "Faceguy";
  }

  @Override
  public @NotNull String getIdentifier() {
    return "jobbo";
  }

  @Override
  public @NotNull String getVersion() {
    return "1.0.0";
  }

  @Override
  public String onPlaceholderRequest(Player p, @NotNull String placeholder) {
    if (StringUtils.isBlank(placeholder)) {
      return "";
    }
    return null;
  }
}
