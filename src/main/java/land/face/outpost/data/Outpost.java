package land.face.outpost.data;

import com.soujah.poggersguilds.data.Guild;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
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
  @Setter
  private Set<String> spawnerIds;

  private transient Guild guild;
  private transient OutpostState state;
  private transient Long attackAlertDmCooldown = 1L;

  @Setter
  private transient String titleBar;
  private transient double collectedTaxes;
  private transient boolean canRally;
  private transient int lastPayment;

  public Outpost(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setWorld(String world) {
    this.world = world;
  }


  public void setPos1(Position pos1) {
    this.pos1 = pos1;
  }

  public void setPos2(Position pos2) {
    this.pos2 = pos2;
  }

  public void setPvpPos1(Position pvpPos1) {
    this.pvpPos1 = pvpPos1;
  }

  public void setPvpPos2(Position pvpPos2) {
    this.pvpPos2 = pvpPos2;
  }

  public void setMaxBarrier(float maxBarrier) {
    this.maxBarrier = maxBarrier;
    barrier = maxBarrier;
  }

  public void setMaxLife(float maxLife) {
    this.maxLife = maxLife;
    life = maxLife;
  }

  public void setBarrier(float barrier) {
    this.barrier = barrier;
  }

  public void setMinimumCashReward(int minimumCashReward) {
    this.minimumCashReward = minimumCashReward;
  }

  public void setLife(float life) {
    this.life = Math.min(maxLife, Math.max(0, life));
  }

  public void setProtectTime(long protectTime) {
    this.protectTime = protectTime;
  }

  public void setWaypoint(String waypoint) {
    this.waypoint = waypoint;
  }

  public void setGuild(Guild guild) {
    this.guild = guild;
    this.guildId = guild == null ? null : guild.getId().toString();
  }

  public void setState(OutpostState state) {
    this.state = state;
  }

  public void setAttackAlertDmCooldown(Long attackAlertDmCooldown) {
    this.attackAlertDmCooldown = attackAlertDmCooldown;
  }

  public void setCollectedTaxes(double collectedTaxes) {
    this.collectedTaxes = collectedTaxes;
  }

  public void setCanRally(boolean canRally) {
    this.canRally = canRally;
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

  public Location getCenterLocation() {
    float x = ((float) getPos1().getX() + (float) getPos2().getX()) / 2;
    float y = ((float) getPos1().getY() + (float) getPos2().getY()) / 2;
    float z = ((float) getPos1().getZ() + (float) getPos2().getZ()) / 2;
    World outpostWorld = Bukkit.getServer().getWorld(getWorld());
    return new Location(outpostWorld, x, y, z);
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
