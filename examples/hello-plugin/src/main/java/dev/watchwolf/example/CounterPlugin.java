package dev.watchwolf.example;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class CounterPlugin extends JavaPlugin {
    private int count;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2 && "add".equalsIgnoreCase(args[0])) {
            try {
                int amount = Integer.parseInt(args[1]);
                if (amount < 0) {
                    sender.sendMessage("sample-counter: amount must be nonnegative");
                    return true;
                }
                count += amount;
            } catch (NumberFormatException exception) {
                sender.sendMessage("sample-counter: amount must be a number");
                return true;
            }
        } else if (args.length != 1 || !"get".equalsIgnoreCase(args[0])) {
            sender.sendMessage("sample-counter: use get or add <amount>");
            return true;
        }

        sender.sendMessage("sample-counter=" + count);
        return true;
    }
}
