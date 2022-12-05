package net.gegy1000.statue.server.command;

import mcp.MethodsReturnNonnullByDefault;
import net.gegy1000.statue.Statue;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.gegy1000.statue.server.message.AnimationMessage;
import net.gegy1000.statue.server.message.FunctionPacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.NetworkRegistry;

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
        return "/animate <animation> <x> <y> <z> <loop count> [<command>] [server/player]";
    }

    @Override
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 5 || args.length > 7) {
            throw new WrongUsageException(getUsage(sender));
        }
        try {
            String animation = args[0];
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            int z = Integer.parseInt(args[3]);
            BlockPos pos = new BlockPos(x, y, z);
            TileEntity te = sender.getEntityWorld().getTileEntity(pos);
            if (!(te instanceof StatueBlockEntity)) {
                throw new WrongUsageException("There is no Statue at this coordinates");
            }
            int loops = Integer.parseInt(args[4]);
            FunctionPacket.Type commandType = FunctionPacket.Type.PLAYER;
            String command = "";
            if (args.length > 5) {
                command = args[5];
                if (args.length > 6) {
                    switch (args[6]) {
                        case "server":
                            commandType = FunctionPacket.Type.SERVER;
                            break;
                        case "player":
                            break;
                        default:
                            sender.sendMessage(
                                    new TextComponentString("Invalid command type " + args[4] + ", setting PLAYER"));
                    }
                }
            }
            Statue.WRAPPER.sendToAllAround(new AnimationMessage(animation, pos, commandType, command, loops),
                    new NetworkRegistry.TargetPoint(sender.getEntityWorld().provider.getDimension(), x, y, z, 128));
        } catch (NumberFormatException e) {
            throw new WrongUsageException("Animation coordinates should be integers!");
        }
    }
}
