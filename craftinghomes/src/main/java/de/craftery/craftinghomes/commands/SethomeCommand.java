package de.craftery.craftinghomes.commands;

import de.craftery.craftinghomes.CraftingHomes;
import de.craftery.craftinghomes.Home;
import de.craftery.craftinghomes.annotation.annotations.Argument;
import de.craftery.craftinghomes.annotation.annotations.Command;
import de.craftery.craftinghomes.common.PlayerOnlyCommand;
import de.craftery.craftinghomes.common.api.PlayerI;

import java.util.ArrayList;
import java.util.List;

@Command(name = "sethome")
@SuppressWarnings("unused")
public class SethomeCommand extends PlayerOnlyCommand {
    @Argument(defaultValue = "default")
    private String homeName;

    @Override
    public boolean onCommand(PlayerI player) {
        Home home = CraftingHomes.getHome(player, homeName);
        if (home != null) {
            CraftingHomes.deleteHome(player, home);
        }

        if (CraftingHomes.getHomes(player.getUniqueId()).size() >= CraftingHomes.getMaxHomes()) {
            player.sendMessage(this.i18n.maxHomesReached(CraftingHomes.getMaxHomes()));
            return true;
        }

        CraftingHomes.addHome(player, new Home(homeName, player.getLocation()));

        player.sendMessage("&aHome with the name &b" + homeName + " &ahas been set!");

        return true;
    }

    @Override
    public List<String> onTabComplete(PlayerI player, int argLength) {
        return new ArrayList<>();
    }
}
