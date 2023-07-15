package de.craftery.craftinghomes.common.gui;

import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.api.PlayerI;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class GuiBuilder {
    @Getter
    private final String title;
    @Getter
    private final Integer rows;
    @Getter
    private final Map<Integer, GuiItem> slots = new HashMap<>();

    public GuiBuilder(String title, Integer rows) {
        this.title = title;
        this.rows = rows;
    }

    public GuiBuilder setSlot(Integer slot, GuiItem item) {
        if (slot > (rows * 9) - 1) throw new IllegalArgumentException("Slot is out of bounds");
        slots.put(slot, item);
        return this;
    }

    public GuiBuilder addSlot(GuiItem item) {
        for (int i = 0; i < (rows * 9); i++) {
            if (!slots.containsKey(i)) {
                slots.put(i, item);
                return this;
            }
        }

        return this;
    }

    public void open(PlayerI player) {
        Platform.getServer().openGui(player, this);
    }
}
