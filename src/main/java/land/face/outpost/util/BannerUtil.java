package land.face.outpost.util;

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
    ArrayList<String> splitBannerData = new ArrayList<>();
    Collections.addAll(splitBannerData, bannerData.split("(?<=\\G..)"));

    ItemStack banner = new ItemStack(getBaseColor(splitBannerData.get(0).charAt(0)));
    splitBannerData.remove(0);

    ArrayList<Pattern> patterns = new ArrayList<>();
    for (String s : splitBannerData) {
      DyeColor color = getColor(s.charAt(0));
      PatternType pattern = getPattern(s.charAt(1));
      if (color == null || pattern == null) continue;
      patterns.add(new Pattern(color, pattern));
    }

    BannerMeta meta = (BannerMeta) banner.getItemMeta();
    meta.setPatterns(patterns);
    banner.setItemMeta(meta);
    return banner;
  }

  private static Material getBaseColor(char c) {
    switch (c) {
      case 'a':
        return Material.BLACK_BANNER;
      case 'b':
        return Material.RED_BANNER;
      case 'c':
        return Material.GREEN_BANNER;
      case 'd':
        return Material.BROWN_BANNER;
      case 'e':
        return Material.BLUE_BANNER;
      case 'f':
        return Material.PURPLE_BANNER;
      case 'g':
        return Material.CYAN_BANNER;
      case 'h':
        return Material.LIGHT_GRAY_BANNER;
      case 'i':
        return Material.GRAY_BANNER;
      case 'j':
        return Material.PINK_BANNER;
      case 'k':
        return Material.LIME_BANNER;
      case 'l':
        return Material.YELLOW_BANNER;
      case 'm':
        return Material.LIGHT_BLUE_BANNER;
      case 'n':
        return Material.MAGENTA_BANNER;
      case 'o':
        return Material.ORANGE_BANNER;
      case 'p':
      default:
        break;
    }
    return Material.WHITE_BANNER;
  }

  private static DyeColor getColor(char c) {
    switch (c) {
      case 'a':
        return DyeColor.BLACK;
      case 'b':
        return DyeColor.RED;
      case 'c':
        return DyeColor.GREEN;
      case 'd':
        return DyeColor.BROWN;
      case 'e':
        return DyeColor.BLUE;
      case 'f':
        return DyeColor.PURPLE;
      case 'g':
        return DyeColor.CYAN;
      case 'h':
        return DyeColor.LIGHT_GRAY;
      case 'i':
        return DyeColor.GRAY;
      case 'j':
        return DyeColor.PINK;
      case 'k':
        return DyeColor.LIME;
      case 'l':
        return DyeColor.YELLOW;
      case 'm':
        return DyeColor.LIGHT_BLUE;
      case 'n':
        return DyeColor.MAGENTA;
      case 'o':
        return DyeColor.ORANGE;
      case 'p':
      default:
        break;
    }
    return DyeColor.WHITE;
  }

  private static PatternType getPattern(char c) {
    switch (c) {
      case 'A':
        return PatternType.SKULL;
      case 'b':
        return PatternType.SQUARE_BOTTOM_LEFT;
      case 'B':
        return PatternType.STRIPE_SMALL;
      case 'c':
        return PatternType.BORDER;
      case 'C':
        return PatternType.SQUARE_TOP_LEFT;
      case 'd':
        return PatternType.SQUARE_BOTTOM_RIGHT;
      case 'D':
        return PatternType.SQUARE_TOP_RIGHT;
      case 'e':
        return PatternType.BRICKS;
      case 'E':
        return PatternType.STRIPE_TOP;
      case 'f':
        return PatternType.STRIPE_BOTTOM;
      case 'F':
        return PatternType.TRIANGLE_TOP;
      case 'g':
        return PatternType.TRIANGLE_BOTTOM;
      case 'G':
        return PatternType.TRIANGLES_TOP;
      case 'H':
        return PatternType.HALF_VERTICAL;
      case 'h':
        return PatternType.TRIANGLES_BOTTOM;
      case 'i':
        return PatternType.CURLY_BORDER;
      case 'I':
        return PatternType.DIAGONAL_LEFT_MIRROR;
      case 'j':
        return PatternType.CROSS;
      case 'J':
        return PatternType.DIAGONAL_RIGHT;
      case 'k':
        return PatternType.CREEPER;
      case 'K':
        return PatternType.GRADIENT_UP;
      case 'l':
        return PatternType.STRIPE_CENTER;
      case 'L':
        return PatternType.HALF_HORIZONTAL_MIRROR;
      case 'm':
        return PatternType.STRIPE_DOWNLEFT;
      case 'M':
        return PatternType.HALF_VERTICAL_MIRROR;
      case 'n':
        return PatternType.STRIPE_DOWNRIGHT;
      case 'o':
        return PatternType.FLOWER;
      case 'p':
        return PatternType.GRADIENT;
      case 'q':
        return PatternType.HALF_HORIZONTAL;
      case 'r':
        return PatternType.DIAGONAL_LEFT;
      case 's':
        return PatternType.STRIPE_LEFT;
      case 't':
        return PatternType.CIRCLE_MIDDLE;
      case 'u':
        return PatternType.MOJANG;
      case 'v':
        return PatternType.RHOMBUS_MIDDLE;
      case 'w':
        return PatternType.STRIPE_MIDDLE;
      case 'x':
        return PatternType.DIAGONAL_RIGHT_MIRROR;
      case 'y':
        return PatternType.STRIPE_RIGHT;
      case 'z':
        return PatternType.STRAIGHT_CROSS;
    }
    return null;
  }
}
