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
            sender.sendMessage(this.i18n.senderNotPlayer());
            return true;
        }

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
    public List<String> onTabComplete(CommandSenderI sender, String[] args) {
        return new ArrayList<>();
    }
}
