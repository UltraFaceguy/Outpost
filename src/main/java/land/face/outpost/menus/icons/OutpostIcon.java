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
package land.face.outpost.menus.icons;

import com.soujah.poggersguilds.data.Guild;
import com.tealcube.minecraft.bukkit.facecore.utilities.FaceColor;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.facecore.utilities.TextUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang.WordUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.time.DurationFormatUtils;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.ArrayList;
import java.util.List;
import land.face.outpost.OutpostPlugin;
import land.face.outpost.data.Outpost;
import land.face.outpost.data.Outpost.OutpostState;
import land.face.waypointer.WaypointerPlugin;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class OutpostIcon extends MenuItem {

  private Outpost outpost;

  public OutpostIcon(Outpost outpost) {
    super("", new ItemStack(Material.AIR));
    this.outpost = outpost;
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack stack = new ItemStack(Material.PAPER);
    ItemStackExtensionsKt.setDisplayName(stack, FaceColor.RED + outpost.getName());
    Guild guild = OutpostPlugin.getInstance().getGuildsAPI().getGuild(player);
    switch (outpost.getState()) {
      case OPEN -> ItemStackExtensionsKt.setCustomModelData(stack, 10000);
      case CONTESTED -> ItemStackExtensionsKt.setCustomModelData(stack, 10001);
      case DEFENDED -> ItemStackExtensionsKt.setCustomModelData(stack, 10002);
      case PROTECTED -> ItemStackExtensionsKt.setCustomModelData(stack, 10003);
    }
    List<String> lore = new ArrayList<>();
    lore.add("&fOutpost Status: &e" + WordUtils.capitalize(outpost.getState().toString()));
    if (outpost.getState() == OutpostState.PROTECTED) {
      long timeTillProtectEnds = outpost.getProtectTime() - System.currentTimeMillis();
      lore.add("&a " + DurationFormatUtils.formatDuration(timeTillProtectEnds, "HH'h' mm'm'") + " Remaining");
    }
    if (outpost.getGuild() == null) {
      lore.add("&fCurrent Owner: &7<Unowned>");
    } else {
      lore.add("&fCurrent Owner: &e" + outpost.getGuild().getName());
    }
    lore.add("");
    if (guild != null && outpost.getGuild() == guild && outpost.isCanRally()) {
      lore.add("&fLeft Click: &c&lRally Guild!");
    }
    if (OutpostPlugin.getInstance().isWaypointerEnabled() && StringUtils
        .isNotBlank(outpost.getWaypoint())) {
      lore.add("&fRight Click: &bSet Waypoint");
    }
    TextUtils.setLore(stack, lore, true);
    return stack;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    if (event.getClickType() == ClickType.RIGHT) {
      if (OutpostPlugin.getInstance().isWaypointerEnabled() && StringUtils.isNotBlank(outpost.getWaypoint())) {
        WaypointerPlugin.getInstance().getWaypointManager().setWaypoint(event.getPlayer(), outpost.getWaypoint());
      }
    } else if (event.getClickType() == ClickType.LEFT) {
      Guild guild = OutpostPlugin.getInstance().getGuildsAPI().getGuild(event.getPlayer());
      if (guild == null) {
        MessageUtils.sendMessage(event.getPlayer(), "&eYou cannot rally without a guild...");
      } else if (outpost.getGuild() != guild) {
        MessageUtils.sendMessage(event.getPlayer(), "&eYou must own an outpost to rally!");
      } else if (!outpost.isCanRally()) {
        MessageUtils.sendMessage(event.getPlayer(), "&eYou can only rally guild members once while under attack!");
      } else {
        MessageUtils.sendMessage(event.getPlayer(), "&aRALLY SUCCESS! but this feature is not implemented");
      }
    }
    event.setWillUpdate(false);
    event.setWillClose(false);
  }
}
