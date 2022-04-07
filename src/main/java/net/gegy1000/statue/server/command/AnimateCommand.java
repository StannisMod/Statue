package net.gegy1000.statue.server.command;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AnimateCommand extends CommandBase {
    @Override
    public String getName() {
        return "animate";
    }

    @Override
    public String getUsage(final ICommandSender sender) {
        return "/animate <animation> <x> <y> <z>";
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length != 4) {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }
        String animation = args[0];
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int z = Integer.parseInt(args[3]);

    }
}
