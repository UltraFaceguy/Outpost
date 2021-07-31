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
package land.face.jobbo.menus.icons;

import io.pixeloutlaw.minecraft.spigot.garbage.ListExtensionsKt;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.util.Arrays;
import java.util.List;
import land.face.jobbo.JobboPlugin;
import land.face.jobbo.data.Job;
import land.face.jobbo.data.JobTemplate;
import land.face.jobbo.menus.BlankIcon;
import land.face.waypointer.WaypointerPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import ninja.amp.ampmenus.events.ItemClickEvent;
import ninja.amp.ampmenus.items.MenuItem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JobWaypointButton extends MenuItem {

  private final JobboPlugin plugin;
  private final List<String> desc = ListExtensionsKt.chatColorize(Arrays.asList(
      "&7Click to set a waypoint to",
      "&7the location of this job!"
  ));

  public JobWaypointButton(JobboPlugin plugin) {
    super("", new ItemStack(Material.AIR));
    this.plugin = plugin;
  }

  @Override
  public ItemStack getFinalIcon(Player player) {
    ItemStack stack = new ItemStack(Material.COMPASS);
    if (!JobboPlugin.isWaypointerEnabled()) {
      return BlankIcon.getBlankStack();
    }
    Job job = JobboPlugin.getApi().getJobManager().getJob(player);
    JobTemplate jobTemplate = job.getTemplate();
    if (jobTemplate.getLocation() == null) {
      return BlankIcon.getBlankStack();
    }
    ItemStackExtensionsKt.setDisplayName(stack, ChatColor.AQUA + "Set Waypoint");
    ItemStackExtensionsKt.setLore(stack, desc);
    return stack;
  }

  @Override
  public void onItemClick(ItemClickEvent event) {
    super.onItemClick(event);
    event.setWillUpdate(false);
    event.setWillClose(false);
    if (!JobboPlugin.isWaypointerEnabled()) {
      return;
    }
    Job job = JobboPlugin.getApi().getJobManager().getJob(event.getPlayer());
    if (job.isCompleted() && JobboPlugin.isCitizensEnabled()) {
      NPC npc = CitizensAPI.getNPCRegistry().getById(job.getTemplate().getCompletionNpc());
      if (npc != null) {
        Location wpLoc = npc.getStoredLocation().clone().add(0, 3, 0);
        WaypointerPlugin.getInstance().getWaypointManager()
            .setWaypoint(event.getPlayer(), "Job Turn-in", wpLoc);
        return;
      }
    }
    JobTemplate jobTemplate = job.getTemplate();
    if (jobTemplate.getLocation() == null) {
      return;
    }
    WaypointerPlugin.getInstance().getWaypointManager().setWaypoint(event.getPlayer(),
        ChatColor.stripColor(jobTemplate.getJobName()), jobTemplate.getLocation());
  }
}
