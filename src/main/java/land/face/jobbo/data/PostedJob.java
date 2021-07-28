package land.face.jobbo.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class PostedJob {

  @Getter
  @Setter
  private Location location;
  @Getter
  @Setter
  private transient int seconds = 10;
  @Getter
  @Setter
  private transient Job job = null;

}
