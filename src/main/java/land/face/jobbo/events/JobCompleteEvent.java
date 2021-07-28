package land.face.jobbo.events;

import land.face.jobbo.data.Job;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JobCompleteEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  @Getter
  @Setter
  private Player player;
  @Getter
  @Setter
  private Job jobInstance;

  public JobCompleteEvent(Player player, Job jobInstance) {
    this.player = player;
    this.jobInstance = jobInstance;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

}
