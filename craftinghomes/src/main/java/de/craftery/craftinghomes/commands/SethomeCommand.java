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
public class SethomeCommand extends AbstractCommand {
    public SethomeCommand() {
        super("sethome");
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
        if (home != null) {
            CraftingHomes.deleteHome(player, home);
        }

        if (CraftingHomes.getHomes(player).size() >= CraftingHomes.getMaxHomes()) {
            player.sendMessage(this.i18n.maxHomesReached(CraftingHomes.getMaxHomes()));
            return true;
        }

        CraftingHomes.addHome(player, new Home(homeName, player.getLocation()));

        player.sendMessage("&aHome with the name &b" + homeName + " &ahas been set!");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSenderI sender, String[] args) {
        return new ArrayList<>();
    }
}
