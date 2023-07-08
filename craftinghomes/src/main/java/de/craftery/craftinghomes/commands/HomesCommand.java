package de.craftery.craftinghomes.commands;

import com.google.auto.service.AutoService;
import de.craftery.craftinghomes.CraftingHomes;
import de.craftery.craftinghomes.Home;
import de.craftery.craftinghomes.common.AbstractCommand;
import de.craftery.craftinghomes.common.api.CommandSenderI;
import de.craftery.craftinghomes.common.api.PlayerI;

import java.util.ArrayList;
import java.util.List;

@AutoService(AbstractCommand.class)
@SuppressWarnings("unused")
public class HomesCommand extends AbstractCommand {
    public HomesCommand() {
        super("homes");
    }

    @Override
    public boolean onCommand(CommandSenderI sender, String[] args) {
        if (!(sender instanceof PlayerI player)) {
            sender.sendMessage("&cYou must be a player to use this command!");
            return true;
        }

        List<Home> homes = CraftingHomes.getHomes(player);

        if (homes.isEmpty()) {
            player.sendMessage("&cYou don't have any homes!");
            return true;
        }
        String homeNames = String.join(", ", homes.stream().map(Home::name).toList());
        player.sendMessage("&aYour homes: &b" + homeNames);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSenderI sender, String[] args) {
        return new ArrayList<>();
    }
}
