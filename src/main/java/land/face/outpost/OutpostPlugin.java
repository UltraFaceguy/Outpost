package land.face.outpost;

import com.soujah.poggersguilds.GuildPlugin;
import com.soujah.poggersguilds.api.GuildAPI;
import com.tealcube.minecraft.bukkit.shade.acf.PaperCommandManager;
import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import land.face.outpost.commands.OutpostCommand;
import land.face.outpost.listeners.CashDropListener;
import land.face.outpost.listeners.GuildAlliedMobListener;
import land.face.outpost.listeners.GuildListener;
import land.face.outpost.listeners.SpawnerListener;
import land.face.outpost.managers.OutpostManager;
import land.face.outpost.menus.OutpostsMenu;
import land.face.outpost.tasks.OutpostCaptureTicker;
import land.face.outpost.tasks.OutpostPayoutTicker;
import land.face.outpost.managers.GuildBannerManager;
import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class OutpostPlugin extends JavaPlugin {

  private static OutpostPlugin instance;

  public static final DecimalFormat INT_FORMAT = new DecimalFormat("#,###,###,###,###");
  public static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#,###,###,###,###.#");

  @Getter
  private OutpostManager outpostManager;
  @Getter
  private GuildBannerManager guildBannerManager;
  @Getter
  private GuildAPI guildsAPI;
  @Getter
  private GuildPlugin guildPlugin;
  private boolean waypointerEnabled;

  private MasterConfiguration settings;
  private VersionedSmartYamlConfiguration configYAML;
  private OutpostsMenu statusMenu;
  private PlaceholderExpansion outpostPlaceholder;

  public static OutpostPlugin getInstance() {
    return instance;
  }

  public OutpostPlugin() {
    instance = this;
  }

  public void onEnable() {
    List<VersionedSmartYamlConfiguration> configurations = new ArrayList<>();
    configurations.add(configYAML = defaultSettingsLoad("config.yml"));

    for (VersionedSmartYamlConfiguration config : configurations) {
      if (config.update()) {
        getLogger().info("Updating " + config.getFileName());
      }
    }

    settings = MasterConfiguration.loadFromFiles(configYAML);

    guildPlugin = GuildPlugin.getInstance();
    guildsAPI = guildPlugin.getGuildAPI();
    waypointerEnabled = Bukkit.getPluginManager().getPlugin("Waypointer") != null;

    outpostManager = new OutpostManager(this);
    guildBannerManager = new GuildBannerManager();

    Bukkit.getPluginManager().registerEvents(new GuildListener(this), this);
    Bukkit.getPluginManager().registerEvents(new GuildAlliedMobListener(this), this);
    Bukkit.getPluginManager().registerEvents(new CashDropListener(this, configYAML), this);
    Bukkit.getPluginManager().registerEvents(new SpawnerListener(this), this);
    //Bukkit.getPluginManager().registerEvents(new PvPListener(this), this);

    outpostManager.loadOutposts();

    loadOutpostUniques(configYAML);

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      outpostPlaceholder = new OutpostPlaceholders();
      outpostPlaceholder.register();
    }

    OutpostCaptureTicker outpostCaptureTicker = new OutpostCaptureTicker(this);
    outpostCaptureTicker.runTaskTimer(this, 60L, 20L);

    int secondsPerPayout = settings.getInt("config.payout-seconds", 5);
    OutpostPayoutTicker payoutTicker = new OutpostPayoutTicker(this, secondsPerPayout);
    payoutTicker.runTaskTimer(this, 200L, secondsPerPayout * 20L);

    statusMenu = new OutpostsMenu(this);
    OutpostsMenu.setInstance(statusMenu);

    PaperCommandManager commandManager = new PaperCommandManager(this);
    commandManager.registerCommand(new OutpostCommand(this));

    Bukkit.getServer().getLogger().info("Outpost Enabled!");
  }

  public void onDisable() {
    outpostManager.saveOutposts();
    outpostPlaceholder.unregister();
    HandlerList.unregisterAll(this);
    Bukkit.getServer().getScheduler().cancelTasks(this);
    Bukkit.getServer().getLogger().info("Outposts Disabled!");
  }

  public MasterConfiguration getSettings() {
    return settings;
  }

  public boolean isWaypointerEnabled() {
    return waypointerEnabled;
  }

  private VersionedSmartYamlConfiguration defaultSettingsLoad(String name) {
    return new VersionedSmartYamlConfiguration(new File(getDataFolder(), name),
        getResource(name), VersionedConfiguration.VersionUpdateType.BACKUP_AND_UPDATE);
  }

  public OutpostsMenu getStatusMenu() {
    return statusMenu;
  }

  private void loadOutpostUniques(VersionedSmartYamlConfiguration config) {
    ConfigurationSection cs = config.getConfigurationSection("outpost-mobs");
    for (String unique : cs.getKeys(false)) {
      String outpostId = cs.getString(unique);
      if (outpostManager.getOutpost(outpostId) == null) {
        continue;
      }
      outpostManager.getUniqueIdToOutpost().put(unique, outpostId);
    }
  }
}