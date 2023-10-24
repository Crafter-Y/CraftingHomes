package de.craftery.craftinghomes.common;

import de.craftery.craftinghomes.common.api.CommandSenderI;
import de.craftery.craftinghomes.common.api.PlayerI;

import java.util.ArrayList;
import java.util.List;

public abstract class PlayerOnlyCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSenderI sender) {
        if (!(sender instanceof PlayerI)) {
            sender.sendMessage(this.i18n.senderNotPlayer());
            return true;
        }
        PlayerI player = (PlayerI) sender;
        return this.onCommand(player);
    }


    @Override
    public List<String> onTabComplete(CommandSenderI sender, int argLength) {
        if (!(sender instanceof PlayerI)) {
            return new ArrayList<>();
        }
        PlayerI player = (PlayerI) sender;
        return this.onTabComplete(player, argLength);
    }

    public abstract boolean onCommand(PlayerI player);
    public abstract List<String> onTabComplete(PlayerI player, int argLength);
}
