package de.craftery.craftinghomes;

import de.craftery.craftinghomes.common.gui.GuiClickCallback;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryProtector implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (BukkitPlatform.getInstance().protectedWindowTitles.contains(event.getView().getTitle())) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null) {
                return;
            }

            GuiClickCallback callback = BukkitPlatform.getInstance().guiClickCallbacks.get(clickedItem);
            if (callback != null) callback.onClick();
        }
    }
}
