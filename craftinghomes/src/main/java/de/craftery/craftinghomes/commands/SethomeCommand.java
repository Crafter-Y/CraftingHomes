package de.craftery.craftinghomes.commands;

import de.craftery.craftinghomes.Home;
import de.craftery.craftinghomes.annotation.annotations.Argument;
import de.craftery.craftinghomes.annotation.annotations.Command;
import de.craftery.craftinghomes.common.Platform;
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
        Home home = Home.getPlayerHome(player, homeName);
        if (home == null) {
            home = new Home();
        }

        Integer maxHomes = Platform.getServer().getConfiguration().getInt("maxHomes", 3);
        if (Home.getByPlayer(player).size() >= maxHomes) {
            player.sendMessage(this.i18n.maxHomesReached(maxHomes));
            return true;
        }

        home.setPlayer(player);
        home.setName(homeName);
        home.setLocation(player.getLocation());

        home.saveOrUpdate();

        player.sendMessage("&aHome with the name &b" + homeName + " &ahas been set!");

        return true;
    }

    @Override
    public List<String> onTabComplete(PlayerI player, int argLength) {
        return new ArrayList<>();
    }
}
