package land.face.outpost.listeners;

import java.util.UUID;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import land.face.outpost.data.Position;
import me.glaremasters.guilds.Guilds;
import me.glaremasters.guilds.api.GuildsAPI;
import me.glaremasters.guilds.guild.Guild;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PvPListener implements Listener {

  private OutpostPlugin plugin;
  private GuildsAPI guildsAPI;

  public PvPListener(OutpostPlugin plugin) {
    this.plugin = plugin;
    guildsAPI = Guilds.getApi();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onOutpostCombat(final EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Entity attacker = getAttacker(event.getDamager());
    if (!(attacker instanceof Player)) {
      return;
    }
    Guild defendGuild = guildsAPI.getGuild((Player) event.getEntity());
    if (defendGuild == null) {
      return;
    }
    Guild attackGuild = guildsAPI.getGuild((Player) attacker);
    if (attackGuild == null) {
      return;
    }
    for (UUID uuid : defendGuild.getAllies()) {
      if (uuid.equals(attackGuild.getId())) {
        return;
      }
    }
    for (Outpost o : plugin.getOutpostManager().getOutposts()) {
      if (Position.isWithin(o, event.getEntity().getLocation(), o.getPvpPos1(), o.getPvpPos2())) {
        if (Position.isWithin(o, attacker.getLocation(), o.getPvpPos1(), o.getPvpPos2())) {
          event.setCancelled(false);
        }
        return;
      }
    }
  }

  private static Entity getAttacker(Entity entity) {
    if (entity instanceof LivingEntity) {
      return entity;
    } else if (entity instanceof Projectile) {
      if (((Projectile) entity).getShooter() instanceof Entity) {
        return (Entity) ((Projectile) entity).getShooter();
      }
    } else if (entity instanceof EvokerFangs) {
      return ((EvokerFangs) entity).getOwner();
    }
    return null;
  }

}
