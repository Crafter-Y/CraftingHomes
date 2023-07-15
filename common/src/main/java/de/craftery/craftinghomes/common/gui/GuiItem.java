package de.craftery.craftinghomes.common.gui;

import java.util.List;

public record GuiItem(GuiItemType type, String name, List<String> lores, String identifier, GuiClickCallback callback) {
}
