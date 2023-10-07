/**
 * The MIT License Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package land.face.outpost.menus;

import com.soujah.poggersguilds.data.Guild;
import java.util.HashMap;
import java.util.Map;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import land.face.outpost.menus.icons.OutpostIcon;
import ninja.amp.ampmenus.menus.ItemMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OutpostsMenu extends ItemMenu {

  private static OutpostsMenu instance;

  private final OutpostPlugin plugin;
  private final Map<Player, Guild> guildCache = new HashMap<>();

  public OutpostsMenu(OutpostPlugin plugin) {
    super(ChatColor.WHITE + "\uF808抪", Size.FIVE_LINE, plugin);
    this.plugin = plugin;
    int slot = 9;
    for (Outpost o : plugin.getOutpostManager().getOutposts()) {
      setItem(slot, new OutpostIcon(o));
      slot++;
    }
  }

  @Override
  public void open(Player player) {
    guildCache.put(player, plugin.getGuildPlugin().getGuildManager().getGuild(player));
    super.open(player);
  }

  @Override
  public void update(Player player) {
    guildCache.put(player, plugin.getGuildPlugin().getGuildManager().getGuild(player));
    super.update(player);
  }

  public static OutpostsMenu getInstance() {
    return instance;
  }

  public static void setInstance(OutpostsMenu menu) {
    instance = menu;
  }

  public Map<Player, Guild> getGuildCache() {
    return guildCache;
  }

}

/*
00 01 02 03 04 05 06 07 08
09 10 11 12 13 14 15 16 17
18 19 20 21 22 23 24 25 26
27 28 29 30 31 32 33 34 35
36 37 38 39 40 41 42 43 44
45 46 47 48 49 50 51 52 53
*/
