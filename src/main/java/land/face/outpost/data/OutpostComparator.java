package land.face.outpost.data;

import com.soujah.poggersguilds.data.Guild;
import java.util.Comparator;

public class OutpostComparator implements Comparator<Outpost> {

  private Guild guild;

  public int compare(Outpost outpost1, Outpost outpost2) {
    if (guild == null) {
      return 0;
    }
    if (outpost1.getGuild() == guild && outpost2.getGuild() == guild) {
      return Integer.compare(outpost1.getState().ordinal(), outpost2.getState().ordinal());
    }
    if (outpost1.getGuild() != guild || outpost2.getGuild() != guild) {
      return Boolean.compare(outpost1.getGuild() == guild, outpost2.getGuild() == guild);
    }
    return Integer.compare(outpost1.getState().ordinal(), outpost2.getState().ordinal());
  }

  public Guild getGuild() {
    return guild;
  }

  public void setGuild(Guild guild) {
    this.guild = guild;
  }
}
