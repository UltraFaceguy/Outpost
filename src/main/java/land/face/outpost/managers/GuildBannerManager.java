package land.face.outpost.managers;

import com.soujah.poggersguilds.data.Guild;
import com.soujah.poggersguilds.util.BannerUtil;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class GuildBannerManager {

  public void setGuildBannersInArea(Guild guild, int borderSize, Location center) {
    setBannersInArea(center, borderSize, guild.getBanner());
  }

  public void setBannersInArea(Location center, int size, String code) {
    Bukkit.getLogger().info("[Outpost] Setting banners in area...");
    ItemStack bannerItem = BannerUtil.getBanner(code);
    List<Block> bannerBlockList = new ArrayList<>();
    for (int x = center.getBlockX() - size; x <= center.getBlockX() + size; x++) {
      for (int y = center.getBlockY() - size; y <= center.getBlockY() + size; y++) {
        for (int z = center.getBlockZ() - size; z <= center.getBlockZ() + size; z++) {
          Block block = center.getWorld().getBlockAt(x, y, z);
          if (block.getState() instanceof Banner) {
            bannerBlockList.add(block);
            Bukkit.getLogger().info("[Outpost] found banner!");
          }
        }
      }
    }
    List<Pattern> patterns = ((BannerMeta) bannerItem.getItemMeta()).getPatterns();
    Material material = bannerItem.getType();
    Material wallMaterial = switch (bannerItem.getType()) {
      case ORANGE_BANNER -> Material.ORANGE_WALL_BANNER;
      case MAGENTA_BANNER -> Material.MAGENTA_WALL_BANNER;
      case CYAN_BANNER -> Material.CYAN_WALL_BANNER;
      case YELLOW_BANNER -> Material.YELLOW_WALL_BANNER;
      case GREEN_BANNER -> Material.GREEN_WALL_BANNER;
      case PINK_BANNER -> Material.PINK_WALL_BANNER;
      case GRAY_BANNER -> Material.GRAY_WALL_BANNER;
      case LIGHT_GRAY_BANNER -> Material.LIGHT_GRAY_WALL_BANNER;
      case LIGHT_BLUE_BANNER -> Material.LIGHT_BLUE_WALL_BANNER;
      case PURPLE_BANNER -> Material.PURPLE_WALL_BANNER;
      case BLUE_BANNER -> Material.BLUE_WALL_BANNER;
      case BROWN_BANNER -> Material.BROWN_WALL_BANNER;
      case LIME_BANNER -> Material.LIME_WALL_BANNER;
      case RED_BANNER -> Material.RED_WALL_BANNER;
      case BLACK_BANNER -> Material.BLACK_WALL_BANNER;
      default -> Material.WHITE_WALL_BANNER;
    };
    for (Block block : bannerBlockList) {
      if (block.getBlockData() instanceof Rotatable) {
        BlockFace rotation = ((Rotatable) block.getBlockData()).getRotation();
        block.setType(material);
        Banner bannerState = (Banner) block.getState();
        bannerState.setPatterns(patterns);
        bannerState.setBlockData(block.getBlockData());
        bannerState.update();
        BlockData data = block.getBlockData();
        ((Rotatable) data).setRotation(rotation);
        block.setBlockData(data);
      } else {
        BlockFace direction = ((Directional) block.getBlockData()).getFacing();
        block.setType(wallMaterial);
        Banner bannerState = (Banner) block.getState();
        bannerState.setPatterns(patterns);
        bannerState.update();
        BlockData data = block.getBlockData();
        ((Directional) data).setFacing(direction);
        block.setBlockData(data);
      }
    }
  }
}
