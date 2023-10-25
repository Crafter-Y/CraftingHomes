package de.craftery.craftinghomes.common.gui;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GuiItem {
    private GuiItemType type;
    private String name;
    private List<String> lores;
    private String identifier;
    private GuiClickCallback callback;
}
