package land.face.jobbo.events;

import land.face.jobbo.data.Job;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobAbandonEvent extends Event implements Cancellable {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  @Getter
  private final Job jobInstance;
  @Getter
  private final Player player;
  private boolean cancel;

  public JobAbandonEvent(Player player, Job jobInstance) {
    this.jobInstance = jobInstance;
    this.player = player;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public void setCancelled(boolean cancel) {
    this.cancel = cancel;
  }

  public boolean isCancelled() {
    return this.cancel;
  }

}
