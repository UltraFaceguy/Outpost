package land.face.jobbo.data;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;

public class PendingSignData {

  @Getter
  @Setter
  private TextComponent lineOne, lineTwo, lineThree, lineFour;

}
