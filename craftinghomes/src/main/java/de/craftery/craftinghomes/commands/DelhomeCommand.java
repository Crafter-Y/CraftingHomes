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
public class DelhomeCommand extends AbstractCommand {
    public DelhomeCommand() {
        super("delhome");
    }

    @Override
    public boolean onCommand(CommandSenderI sender, String[] args) {
        if (!(sender instanceof PlayerI player)) {
            sender.sendMessage(this.i18n.senderNotPlayer());
            return true;
        }

        String homeName;
        if (args.length == 0) {
            homeName = "default";
        } else {
            homeName = args[0];
        }

        Home home = CraftingHomes.getHome(player, homeName);
        if (home == null) {
            player.sendMessage(this.i18n.homeNotExisting(homeName));
            return true;
        }

        CraftingHomes.deleteHome(player, home);
        player.sendMessage(this.i18n.homeDeleted(homeName));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSenderI sender, String[] args) {
        if (!(sender instanceof PlayerI player)) {
            return new ArrayList<>();
        }
        if (args.length == 1) {
            return CraftingHomes.getHomes(player).stream().map(Home::name).toList();
        }

        return new ArrayList<>();
    }
}
