package land.face.outpost.events;

import land.face.outpost.data.Outpost;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OutpostCaptureEvent extends Event {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  private final Outpost outpost;

  public OutpostCaptureEvent(Outpost outpost) {
    this.outpost = outpost;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public Outpost getOutpost() {
    return outpost;
  }

}
