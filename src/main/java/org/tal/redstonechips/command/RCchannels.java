
package org.tal.redstonechips.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.tal.redstonechips.channel.BroadcastChannel;
import org.tal.redstonechips.channel.ReceivingCircuit;
import org.tal.redstonechips.channel.TransmittingCircuit;
import org.tal.redstonechips.util.BitSetUtils;

/**
 *
 * @author Tal Eisenberg
 */
public class RCchannels extends RCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (rc.broadcastChannels.isEmpty()) {
            sender.sendMessage(rc.getPrefs().getInfoColor() + "There are no active broadcast channels.");
        } else {
            if (args.length>0 && rc.broadcastChannels.containsKey(args[0])) {
                printChannelInfo(sender, args[0]);
            } else {
                String[] lines = new String[rc.broadcastChannels.size()];
                int idx = 0;
                for (BroadcastChannel channel : rc.broadcastChannels.values()) {
                    lines[idx] = ChatColor.YELLOW + channel.name + ChatColor.WHITE + " - " + channel.getLength() + " bits, " + channel.getTransmitters().size() + " transmitters, " + channel.getReceivers().size() + " receivers.";
                    idx++;
                }

                sender.sendMessage("");
                CommandUtils.pageMaker(sender, "Active wireless broadcast channels", command.getName(), lines, rc.getPrefs().getInfoColor(), rc.getPrefs().getErrorColor(), CommandUtils.MaxLines - 1);
                sender.sendMessage("Use " + ChatColor.YELLOW + "/rcchannels <channel name>" + ChatColor.WHITE + " for more info about it.");
            }
        }

        return true;
    }

    private void printChannelInfo(CommandSender sender, String channelName) {
        ChatColor infoColor = rc.getPrefs().getInfoColor();
        ChatColor errorColor = rc.getPrefs().getErrorColor();
        ChatColor extraColor = ChatColor.YELLOW;

        BroadcastChannel channel = rc.broadcastChannels.get(channelName);
        if (channel==null) {
            sender.sendMessage(errorColor + "Channel " + channelName + " doesn't exist.");
        } else {
            String sTransmitters = "";
            for (TransmittingCircuit t : channel.getTransmitters()) {
                String range = "[";
                if (t.getChannelLength()>1)
                    range += "bits " + t.getStartBit() + "-" + (t.getChannelLength()+t.getStartBit()-1) + "]";
                else range += "bit " + t.getStartBit() + "]";

                sTransmitters += t.getCircuitClass() + " (" + t.id + ") " + range + ", ";
            }

            String sReceivers = "";
            for (ReceivingCircuit r : channel.getReceivers()) {
                String range = "[";
                if (r.getChannelLength()>1)
                    range += "bits " + r.getStartBit() + "-" + (r.getChannelLength()+r.getStartBit()-1) + "]";
                else range += "bit " + r.getStartBit() + "]";
                sReceivers += r.getCircuitClass() + " (" + r.id + ") " + range + ", ";
            }

            sender.sendMessage("");
            sender.sendMessage(extraColor + channel.name + ":");
            sender.sendMessage(extraColor + "----------------------");

            sender.sendMessage(infoColor + "last broadcast: " + extraColor + BitSetUtils.bitSetToBinaryString(channel.bits, 0, channel.getLength()) + infoColor + " length: " + extraColor + channel.getLength());

            if (!sTransmitters.isEmpty())
                sender.sendMessage(infoColor + "transmitters: " + extraColor + sTransmitters.substring(0, sTransmitters.length()-2));
            if (!sReceivers.isEmpty())
                sender.sendMessage(infoColor + "receivers: " + extraColor + sReceivers.substring(0, sReceivers.length()-2));

        }
    }

}
