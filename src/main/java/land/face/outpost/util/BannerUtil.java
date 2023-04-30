package land.face.outpost.util;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class BannerUtil {

  public static ItemStack getBanner(String bannerData) {
    if (StringUtils.isBlank(bannerData)) {
      return new ItemStack(Material.RED_BANNER);
    }
    try {
      ArrayList<String> splitBannerData = new ArrayList<>();
      Collections.addAll(splitBannerData, bannerData.split("(?<=\\G..)"));

      ItemStack banner = new ItemStack(getBaseColor(splitBannerData.get(0).charAt(0)));
      splitBannerData.remove(0);

      ArrayList<Pattern> patterns = new ArrayList<>();
      for (String s : splitBannerData) {
        DyeColor color = getColor(s.charAt(0));
        PatternType pattern = getPattern(s.charAt(1));
        if (color == null || pattern == null)
          continue;
        patterns.add(new Pattern(color, pattern));
      }

      BannerMeta meta = (BannerMeta) banner.getItemMeta();
      meta.setPatterns(patterns);
      banner.setItemMeta(meta);
      return banner;
    } catch (Exception e) {
      return new ItemStack(Material.RED_BANNER);
    }
  }

  private static Material getBaseColor(char c) {
    return switch (c) {
      case 'a' -> Material.BLACK_BANNER;
      case 'b' -> Material.RED_BANNER;
      case 'c' -> Material.GREEN_BANNER;
      case 'd' -> Material.BROWN_BANNER;
      case 'e' -> Material.BLUE_BANNER;
      case 'f' -> Material.PURPLE_BANNER;
      case 'g' -> Material.CYAN_BANNER;
      case 'h' -> Material.LIGHT_GRAY_BANNER;
      case 'i' -> Material.GRAY_BANNER;
      case 'j' -> Material.PINK_BANNER;
      case 'k' -> Material.LIME_BANNER;
      case 'l' -> Material.YELLOW_BANNER;
      case 'm' -> Material.LIGHT_BLUE_BANNER;
      case 'n' -> Material.MAGENTA_BANNER;
      case 'o' -> Material.ORANGE_BANNER;
      default -> Material.WHITE_BANNER;
    };
  }

  private static DyeColor getColor(char c) {
    return switch (c) {
      case 'a' -> DyeColor.BLACK;
      case 'b' -> DyeColor.RED;
      case 'c' -> DyeColor.GREEN;
      case 'd' -> DyeColor.BROWN;
      case 'e' -> DyeColor.BLUE;
      case 'f' -> DyeColor.PURPLE;
      case 'g' -> DyeColor.CYAN;
      case 'h' -> DyeColor.LIGHT_GRAY;
      case 'i' -> DyeColor.GRAY;
      case 'j' -> DyeColor.PINK;
      case 'k' -> DyeColor.LIME;
      case 'l' -> DyeColor.YELLOW;
      case 'm' -> DyeColor.LIGHT_BLUE;
      case 'n' -> DyeColor.MAGENTA;
      case 'o' -> DyeColor.ORANGE;
      default -> DyeColor.WHITE;
    };
  }

  private static PatternType getPattern(char c) {
    return switch (c) {
      case 'A' -> PatternType.SKULL;
      case 'b' -> PatternType.SQUARE_BOTTOM_LEFT;
      case 'B' -> PatternType.STRIPE_SMALL;
      case 'c' -> PatternType.BORDER;
      case 'C' -> PatternType.SQUARE_TOP_LEFT;
      case 'd' -> PatternType.SQUARE_BOTTOM_RIGHT;
      case 'D' -> PatternType.SQUARE_TOP_RIGHT;
      case 'e' -> PatternType.BRICKS;
      case 'E' -> PatternType.STRIPE_TOP;
      case 'f' -> PatternType.STRIPE_BOTTOM;
      case 'F' -> PatternType.TRIANGLE_TOP;
      case 'g' -> PatternType.TRIANGLE_BOTTOM;
      case 'G' -> PatternType.TRIANGLES_TOP;
      case 'H' -> PatternType.HALF_VERTICAL;
      case 'h' -> PatternType.TRIANGLES_BOTTOM;
      case 'i' -> PatternType.CURLY_BORDER;
      case 'I' -> PatternType.DIAGONAL_LEFT_MIRROR;
      case 'j' -> PatternType.CROSS;
      case 'J' -> PatternType.DIAGONAL_RIGHT;
      case 'k' -> PatternType.CREEPER;
      case 'K' -> PatternType.GRADIENT_UP;
      case 'l' -> PatternType.STRIPE_CENTER;
      case 'L' -> PatternType.HALF_HORIZONTAL_MIRROR;
      case 'm' -> PatternType.STRIPE_DOWNLEFT;
      case 'M' -> PatternType.HALF_VERTICAL_MIRROR;
      case 'n' -> PatternType.STRIPE_DOWNRIGHT;
      case 'o' -> PatternType.FLOWER;
      case 'p' -> PatternType.GRADIENT;
      case 'q' -> PatternType.HALF_HORIZONTAL;
      case 'r' -> PatternType.DIAGONAL_LEFT;
      case 's' -> PatternType.STRIPE_LEFT;
      case 't' -> PatternType.CIRCLE_MIDDLE;
      case 'u' -> PatternType.MOJANG;
      case 'v' -> PatternType.RHOMBUS_MIDDLE;
      case 'w' -> PatternType.STRIPE_MIDDLE;
      case 'x' -> PatternType.DIAGONAL_RIGHT_MIRROR;
      case 'y' -> PatternType.STRIPE_RIGHT;
      case 'z' -> PatternType.STRAIGHT_CROSS;
      default -> null;
    };
  }
}
