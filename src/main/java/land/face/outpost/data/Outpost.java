package land.face.outpost.data;

import land.face.outpost.OutpostPlugin;
import me.glaremasters.guilds.guild.Guild;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class Outpost {

  private final String id;
  private String name;
  private String guildId;
  private String world;
  private Position pos1;
  private Position pos2;
  private Position pvpPos1;
  private Position pvpPos2;
  private float maxBarrier;
  private float maxLife;
  private float barrier;
  private float life;
  private int minimumCashReward;
  private long protectTime;
  private String waypoint;

  private transient Guild guild;
  private transient OutpostState state;
  private transient BossBar bossBar;
  private transient Long attackAlertDmCooldown = 1L;
  private transient double collectedTaxes;
  private transient boolean canRally;
  private transient int lastPayment;

  public Outpost(String id) {
    this.id = id;
    bossBar = makeBar();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGuildId() {
    return guildId;
  }

  public void setGuildId(String guildId) {
    this.guildId = guildId;
  }

  public String getWorld() {
    return world;
  }

  public void setWorld(String world) {
    this.world = world;
  }


  public Position getPos1() {
    return pos1;
  }

  public void setPos1(Position pos1) {
    this.pos1 = pos1;
  }

  public Position getPos2() {
    return pos2;
  }

  public void setPos2(Position pos2) {
    this.pos2 = pos2;
  }

  public Position getPvpPos1() {
    return pvpPos1;
  }

  public void setPvpPos1(Position pvpPos1) {
    this.pvpPos1 = pvpPos1;
  }

  public Position getPvpPos2() {
    return pvpPos2;
  }

  public void setPvpPos2(Position pvpPos2) {
    this.pvpPos2 = pvpPos2;
  }

  public float getMaxBarrier() {
    return maxBarrier;
  }

  public void setMaxBarrier(float maxBarrier) {
    this.maxBarrier = maxBarrier;
    barrier = maxBarrier;
  }

  public float getMaxLife() {
    return maxLife;
  }

  public void setMaxLife(float maxLife) {
    this.maxLife = maxLife;
    life = maxLife;
  }

  public float getBarrier() {
    return barrier;
  }

  public void setBarrier(float barrier) {
    this.barrier = barrier;
  }

  public int getMinimumCashReward() {
    return minimumCashReward;
  }

  public void setMinimumCashReward(int minimumCashReward) {
    this.minimumCashReward = minimumCashReward;
  }

  public float getLife() {
    return life;
  }

  public void setLife(float life) {
    this.life = Math.min(maxLife, Math.max(0, life));
  }

  public long getProtectTime() {
    return protectTime;
  }

  public void setProtectTime(long protectTime) {
    this.protectTime = protectTime;
  }

  public String getWaypoint() {
    return waypoint;
  }

  public void setWaypoint(String waypoint) {
    this.waypoint = waypoint;
  }

  public Guild getGuild() {
    return guild;
  }

  public void setGuild(Guild guild) {
    this.guild = guild;
  }

  public OutpostState getState() {
    return state;
  }

  public void setState(OutpostState state) {
    this.state = state;
  }

  public BossBar getBossBar() {
    return bossBar;
  }

  public Long getAttackAlertDmCooldown() {
    return attackAlertDmCooldown;
  }

  public void setAttackAlertDmCooldown(Long attackAlertDmCooldown) {
    this.attackAlertDmCooldown = attackAlertDmCooldown;
  }

  public double getCollectedTaxes() {
    return collectedTaxes;
  }

  public void setCollectedTaxes(double collectedTaxes) {
    this.collectedTaxes = collectedTaxes;
  }

  public boolean isCanRally() {
    return canRally;
  }

  public void setCanRally(boolean canRally) {
    this.canRally = canRally;
  }

  public int getLastPayment() {
    return lastPayment;
  }

  public void setLastPayment(int lastPayment) {
    this.lastPayment = lastPayment;
  }

  public void damage(float amount) {
    barrier -= amount;
    if (barrier < 0) {
      setLife(life + barrier);
      barrier = 0;
    }
  }

  public void buildBar() {
    bossBar = makeBar();
  }

  private static BossBar makeBar() {
    return OutpostPlugin.getInstance().getServer().createBossBar("OUTPOST", BarColor.YELLOW, BarStyle.SOLID);
  }

  // Don't change order without intent, cardinality is important
  // as it is used in OutpostComparator
  public enum OutpostState {
    CONTESTED,
    DEFENDED,
    OPEN,
    PROTECTED,
  }

}
