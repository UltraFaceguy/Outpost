package land.face.jobbo.data;

import com.tealcube.minecraft.bukkit.facecore.utilities.ChunkUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public class PostedJob {

  @Getter
  private Location location;
  @Getter
  @Setter
  private transient int seconds = 10;
  @Getter
  @Setter
  private transient Job job = null;
  @Getter
  private String chunkKey;
  @Getter
  @Setter
  private transient PendingSignData pendingSignData;

  public void setLocation(Location location) {
    this.location = location;
    chunkKey = ChunkUtil.buildChunkKey(location.getChunk());
  }

  public void attemptSignUpdate() {
    if (pendingSignData == null || !ChunkUtil.isChuckLoaded(chunkKey)) {
      return;
    }
    BlockState state = location.getBlock().getState();
    if (!(state instanceof Sign sign)) {
      Bukkit.getLogger().warning("Missing sign at " + location.getBlockX() + " " +
          location.getBlockY() + " " + location.getBlockZ());
      return;
    }
    sign.setEditable(true);
    sign.line(0, pendingSignData.getLineOne());
    sign.line(1, pendingSignData.getLineTwo());
    sign.line(2, pendingSignData.getLineThree());
    sign.line(3, pendingSignData.getLineFour());
    sign.setEditable(false);
    sign.update();
    pendingSignData = null;
  }

}
