package de.craftery.craftinghomes.commands;

import de.craftery.craftinghomes.Home;
import de.craftery.craftinghomes.annotation.annotations.Argument;
import de.craftery.craftinghomes.annotation.annotations.Command;
import de.craftery.craftinghomes.common.PlayerOnlyCommand;
import de.craftery.craftinghomes.common.api.PlayerI;

import java.util.ArrayList;
import java.util.List;

@Command(name = "delhome")
@SuppressWarnings("unused")
public class DelhomeCommand extends PlayerOnlyCommand {
    @Argument(defaultValue = "default")
    private String homeName;

    @Override
    public boolean onCommand(PlayerI player) {
        Home home = Home.getPlayerHome(player, homeName);
        if (home == null) {
            player.sendMessage(this.i18n.homeNotExisting(homeName));
            return true;
        }

        home.delete();
        player.sendMessage(this.i18n.homeDeleted(homeName));
        return true;
    }

    @Override
    public List<String> onTabComplete(PlayerI player, int argLength) {
        if (argLength == 1) {
            return Home.getByPlayer(player).stream().map(Home::getName).toList();
        }

        return new ArrayList<>();
    }
}
