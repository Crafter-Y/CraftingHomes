package de.craftery.craftinghomes.commands;

import de.craftery.craftinghomes.Home;
import de.craftery.craftinghomes.annotation.annotations.Argument;
import de.craftery.craftinghomes.annotation.annotations.Command;
import de.craftery.craftinghomes.common.AbstractCommand;
import de.craftery.craftinghomes.common.Platform;
import de.craftery.craftinghomes.common.api.CommandSenderI;
import de.craftery.craftinghomes.common.api.OfflinePlayerI;
import de.craftery.craftinghomes.common.api.PlayerI;

import java.util.ArrayList;
import java.util.List;

@Command(name = "homes")
@SuppressWarnings("unused")
public class HomesCommand extends AbstractCommand {
    @Argument(required = false)
    private String targetPlayer;

    @Override
    public boolean onCommand(CommandSenderI sender) {
        List<Home> homes;

        if (targetPlayer == null) {
            if (!(sender instanceof PlayerI player)) {
                sender.sendMessage(this.i18n.senderNotPlayer());
                return true;
            }
            homes = Home.getByPlayer(player);

            if (homes.isEmpty()) {
                sender.sendMessage(this.i18n.noHomes());
                return true;
            }

            String homeNames = String.join(", ", homes.stream().map(Home::getName).toList());
            sender.sendMessage(this.i18n.yourHomes(homeNames));
        } else {
            if (!sender.hasPermission("craftinghomes.homes.other")) {
                sender.sendMessage(this.i18n.noPermission());
                return true;
            }

            OfflinePlayerI player = Platform.getServer().getOfflinePlayer(targetPlayer);
            if (player == null) {
                sender.sendMessage(this.i18n.playerNeverOnline(targetPlayer));
                return true;
            }
            homes = Home.getByField("uuid", player.getUniqueId());

            if (homes.isEmpty()) {
                sender.sendMessage(this.i18n.noHomesPlayer(player.getName()));
                return true;
            }

            String homeNames = String.join(", ", homes.stream().map(Home::getName).toList());
            sender.sendMessage(this.i18n.playerHomes(player.getName(), homeNames));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSenderI sender, int argLength) {
        if (argLength == 1 && sender.hasPermission("craftinghomes.homes.other")) {
            return null;
        }
        return new ArrayList<>();
    }
}
