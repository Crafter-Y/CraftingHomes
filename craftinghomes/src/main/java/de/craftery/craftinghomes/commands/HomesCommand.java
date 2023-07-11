package de.craftery.craftinghomes.commands;

import de.craftery.craftinghomes.CraftingHomes;
import de.craftery.craftinghomes.Home;
import de.craftery.craftinghomes.annotation.annotations.Command;
import de.craftery.craftinghomes.common.PlayerOnlyCommand;
import de.craftery.craftinghomes.common.api.PlayerI;

import java.util.ArrayList;
import java.util.List;

@Command(name = "homes")
@SuppressWarnings("unused")
public class HomesCommand extends PlayerOnlyCommand {
    @Override
    public boolean onCommand(PlayerI player) {
        List<Home> homes = CraftingHomes.getHomes(player);

        if (homes.isEmpty()) {
            player.sendMessage(this.i18n.noHomes());
            return true;
        }
        String homeNames = String.join(", ", homes.stream().map(Home::name).toList());
        player.sendMessage(this.i18n.yourHomes(homeNames));

        return true;
    }

    @Override
    public List<String> onTabComplete(PlayerI player, int argLength) {
        return new ArrayList<>();
    }
}
