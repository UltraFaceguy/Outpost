package land.face.outpost.listeners;

import com.soujah.poggersguilds.api.GuildAPI;
import com.soujah.poggersguilds.data.Guild;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import land.face.outpost.data.Position;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class PvPListener implements Listener {

  private OutpostPlugin plugin;
  private GuildAPI guildAPI;

  public PvPListener(OutpostPlugin plugin) {
    this.plugin = plugin;
    guildAPI = plugin.getGuildAPI();
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
    Guild defendGuild = guildAPI.getOnlineGuildFromPlayer((Player) event.getEntity());
    if (defendGuild == null) {
      return;
    }
    Guild attackGuild = guildAPI.getOnlineGuildFromPlayer((Player) attacker);
    if (attackGuild == null) {
      return;
    }
//    for (UUID uuid : defendGuild.getAllies()) {
//      if (uuid.equals(attackGuild.getId())) {
//        return;
//      }
//    }
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
