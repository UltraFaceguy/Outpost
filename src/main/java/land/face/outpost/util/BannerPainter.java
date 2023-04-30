package land.face.outpost.util;

import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.google.gson.Gson;
import com.tealcube.minecraft.bukkit.shade.google.gson.JsonArray;
import com.tealcube.minecraft.bukkit.shade.google.gson.JsonElement;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.BannerData;
import me.glaremasters.guilds.guild.Guild;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class BannerPainter {

  private final OutpostPlugin plugin;
  private final Gson gson = new Gson();

  public BannerPainter(OutpostPlugin plugin) {
    this.plugin = plugin;
  }

  private static final Map<UUID, String> bannerCodeByGuildID = new HashMap<>();

  public static void setGuildBannersInArea(Guild guild, int borderSize, Location center) {
    setBannersInArea(center, borderSize, bannerCodeByGuildID.get(guild.getId()));
  }

  public static void setBannersInArea(Location center, int size, String code) {
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

  public static void setGuildBannerCode(Guild guild, String bannerCode) {
    bannerCodeByGuildID.put(guild.getId(), bannerCode);
  }

  public void saveGuildBanners() {
    try (FileWriter writer = new FileWriter(plugin.getDataFolder() + "/banners.json")) {
      List<BannerData> data = new ArrayList<>();
      for (Entry<UUID, String> entry : bannerCodeByGuildID.entrySet()) {
        BannerData data1 = new BannerData();
        data1.setUuid(entry.getKey().toString());
        data1.setBannerCode(entry.getValue());
        data.add(data1);
      }
      gson.toJson(data, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void loadGuildBanners() {
    bannerCodeByGuildID.clear();
    try (FileReader reader = new FileReader(plugin.getDataFolder() + "/banners.json")) {
      JsonArray array = gson.fromJson(reader, JsonArray.class);
      List<BannerData> data = new ArrayList<>();
      for (JsonElement e : array) {
        data.add(gson.fromJson(e, BannerData.class));
      }
      for (BannerData bd : data) {
        bannerCodeByGuildID.put(UUID.fromString(bd.getUuid()), bd.getBannerCode());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
