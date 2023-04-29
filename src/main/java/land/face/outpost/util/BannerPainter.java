package land.face.outpost.util;

import com.tealcube.minecraft.bukkit.shade.google.gson.Gson;
import com.tealcube.minecraft.bukkit.shade.google.gson.JsonArray;
import com.tealcube.minecraft.bukkit.shade.google.gson.JsonElement;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import me.glaremasters.guilds.guild.Guild;
import org.bukkit.Location;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BannerPainter {

    private final OutpostPlugin plugin;
    private final Gson gson = new Gson();

    public BannerPainter(OutpostPlugin plugin) {
        this.plugin = plugin;
    }

    private static final Map<String, String> bannerCodeByGuildID = new HashMap<>();

    public static void setGuildBannersInArea(Guild guild, int borderSize, Location center, String bannerCode){

        bannerCode = bannerCode == null ? bannerCodeByGuildID.get(guild.getId().toString()) : bannerCode;

        if (bannerCode == ""){
            return;
        }

        ItemStack bannerItem = BannerUtil.getBanner(bannerCode);

        List<Block> bannerBlockList = new ArrayList<>();

        int X = center.getBlockX();
        int Z = center.getBlockZ();
        for (int x = X - borderSize; x <= X + borderSize; x++) {
            for (int y = 0; y <= center.getWorld().getMaxHeight(); y++) {
                for (int z = Z - borderSize; z <= Z + borderSize; z++) {
                    Block block = center.getWorld().getBlockAt(x, y, z);
                    if (block.getBlockData() instanceof BannerMeta){
                        bannerBlockList.add(center.getWorld().getBlockAt(x, y, z));
                    }
                }
            }
        }

        for (Block block : bannerBlockList){
            Banner banner = (Banner) block;
            BannerMeta bannerMeta = (BannerMeta) bannerItem.getItemMeta();
            banner.setPatterns(bannerMeta.getPatterns());
        }
    }

    public static void setGuildBannerCode(Guild guild, String bannerCode){
        bannerCodeByGuildID.put(guild.getId().toString(), bannerCode);
    }
    public void saveGuildBanners() {

        try (FileWriter writer = new FileWriter(plugin.getDataFolder() + "/banners.json")) {
            gson.toJson(bannerCodeByGuildID.values(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGuildBanners() {
        try (FileReader reader = new FileReader(plugin.getDataFolder() + "/banners.json")) {
            JsonArray array = gson.fromJson(reader, JsonArray.class);
            for (JsonElement e : array) {
                String bannerCode = gson.fromJson(e, String.class);
                bannerCodeByGuildID.put(e.toString(), bannerCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
