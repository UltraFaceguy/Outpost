package land.face.outpost.listeners;

import static com.sk89q.worldedit.math.BlockVector3.at;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.StringMatcher;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.soujah.poggersguilds.data.Guild;
import com.tealcube.minecraft.bukkit.bullion.GoldDropEvent;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.util.LinkedHashMap;
import java.util.Map;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CashDropListener implements Listener {

  private final OutpostPlugin plugin;

  private RegionContainer regionContainer;
  private StringMatcher stringMatcher;

  private Map<String, String> regionToPost = new LinkedHashMap<>();

  public CashDropListener(OutpostPlugin plugin, VersionedSmartYamlConfiguration config) {
    this.plugin = plugin;
    regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
    stringMatcher = WorldGuard.getInstance().getPlatform().getMatcher();
    ConfigurationSection cs = config.getConfigurationSection("region-map");
    for (String regionId : cs.getKeys(false)) {
      regionToPost.put(regionId, cs.getString(regionId));
    }
  }

  private String getOutpostId(ApplicableRegionSet regions) {
    for (String region : regionToPost.keySet()) {
      for (ProtectedRegion p : regions.getRegions()) {
        if (p.getId().equals(region)) {
          return regionToPost.get(region);
        }
      }
    }
    return null;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void cashDropTax(final GoldDropEvent event) {
    Location loc = event.getLivingEntity().getLocation();

    BlockVector3 vectorLoc = at(loc.getX(), loc.getY(), loc.getZ());
    World world = stringMatcher.getWorldByName(loc.getWorld().getName());
    if (world == null) {
      return;
    }
    RegionManager manager = regionContainer.get(world);
    if (manager == null) {
      return;
    }

    ApplicableRegionSet regions = manager.getApplicableRegions(vectorLoc);
    if (regions.getRegions().size() == 0) {
      return;
    }

    String outpostId = getOutpostId(regions);
    if (outpostId == null) {
      return;
    }

    Outpost outpost = plugin.getOutpostManager().getOutpost(outpostId);
    if (outpost == null) {
      return;
    }

    Guild killerGuild = event.getKiller() == null ? null : plugin.getGuildPlugin().getGuildManager().getGuild(event.getKiller());
    boolean isKillerMember = killerGuild != null && killerGuild == outpost.getGuild();

    if (isKillerMember) {
      event.setAmount(event.getAmount() * 1.05);
    } else {
      double tax = event.getAmount() * 0.05;
      event.setAmount(event.getAmount() - tax);
      outpost.setCollectedTaxes(outpost.getCollectedTaxes() + tax);
    }
  }

}
