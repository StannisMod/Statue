package net.gegy1000.statue.server.command;

import mcp.MethodsReturnNonnullByDefault;
import net.gegy1000.statue.Statue;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.gegy1000.statue.server.message.StopAnimationMessage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StopAnimCommand extends CommandBase {
    @Override
    public String getName() {
        return "stopanim";
    }

    @Override
    public String getUsage(final ICommandSender sender) {
        return "/stopanim <x> <y> <z>";
    }

    @Override
    public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
        return super.checkPermission(server, sender) || sender instanceof CommandBlockBaseLogic;
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length != 3) {
            throw new WrongUsageException(getUsage(sender));
        }
        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            BlockPos pos = new BlockPos(x, y, z);
            TileEntity te = sender.getEntityWorld().getTileEntity(pos);
            if (!(te instanceof StatueBlockEntity)) {
                throw new WrongUsageException("There is no Statue at this coordinates");
            }
            Statue.WRAPPER.sendToAll(new StopAnimationMessage(pos));
        } catch (NumberFormatException e) {
            throw new WrongUsageException("Animation coordinates should be integers!");
        }
    }
}
