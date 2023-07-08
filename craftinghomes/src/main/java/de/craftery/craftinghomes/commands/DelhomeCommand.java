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
            sender.sendMessage("&cYou must be a player to use this command!");
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
            player.sendMessage("&cHome with the name &b" + homeName + " &cdoes not exist!");
            return true;
        }

        CraftingHomes.deleteHome(player, home);
        player.sendMessage("&aHome &b" + homeName +" &adeleted!");

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
