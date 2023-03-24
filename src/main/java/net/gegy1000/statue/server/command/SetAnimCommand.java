package net.gegy1000.statue.server.command;

import mcp.MethodsReturnNonnullByDefault;
import net.gegy1000.statue.server.block.entity.StatueBlockEntity;
import net.gegy1000.statue.server.block.entity.StatueProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SetAnimCommand extends CommandBase {

    @Override
    public String getName() {
        return "setanim";
    }

    @Override
    public String getUsage(final ICommandSender sender) {
        return "/setanim <X> <Y> <Z> <rX> <rY> <rZ> <oX> <oY> <oZ> <sX> <sY> <sZ> <lock>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length != 13) {
            throw new WrongUsageException(getUsage(sender));
        }

        try {
            BlockPos pos = new BlockPos(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            TileEntity tew = sender.getEntityWorld().getTileEntity(pos);
            IBlockState state = sender.getEntityWorld().getBlockState(pos);
            if (!(tew instanceof StatueBlockEntity)) {
                throw new WrongUsageException("There is no Statue at given coordinates");
            }
            StatueBlockEntity te = (StatueBlockEntity) tew;

            te.setProperty(StatueProperty.ROTATION_X, Float.parseFloat(args[3]));
            te.setProperty(StatueProperty.ROTATION_Y, Float.parseFloat(args[4]));
            te.setProperty(StatueProperty.ROTATION_Z, Float.parseFloat(args[5]));

            te.setProperty(StatueProperty.OFFSET_X, Float.parseFloat(args[6]));
            te.setProperty(StatueProperty.OFFSET_Y, Float.parseFloat(args[7]));
            te.setProperty(StatueProperty.OFFSET_Z, Float.parseFloat(args[8]));

            te.setProperty(StatueProperty.SCALE_X, Float.parseFloat(args[9]));
            te.setProperty(StatueProperty.SCALE_Y, Float.parseFloat(args[10]));
            te.setProperty(StatueProperty.SCALE_Z, Float.parseFloat(args[11]));

            te.setLocked(Boolean.parseBoolean(args[12]), false);

            sender.getEntityWorld().notifyBlockUpdate(pos, state, state, 3);

            // /moveanim -8 64 137 -10 64 137

        } catch (NumberFormatException e) {
            throw new WrongUsageException("Parameters should be integers of floats!");
        }
    }
}
