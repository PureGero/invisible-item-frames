package com.github.puregero.invisibleitemframes;

import com.github.puregero.multilib.MultiLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class InvisibleItemFramesCommand implements CommandExecutor, TabCompleter {

    private final InvisibleItemFramesPlugin plugin;

    public InvisibleItemFramesCommand(InvisibleItemFramesPlugin plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("invisibleitemframes")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("invisibleitemframes")).setTabCompleter(this);

        MultiLib.onString(plugin, "invisibleitemframes:toggle", str -> {
            String[] args = str.split("\t");
            UUID uuid = UUID.fromString(args[0]);
            boolean visible = Boolean.parseBoolean(args[1]);

            Entity entity = Bukkit.getEntity(uuid);
            if (!(entity instanceof ItemFrame itemFrame)) return;

            itemFrame.getScheduler().run(plugin, task -> {
                itemFrame.setVisible(visible);
            }, null);
        });
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1 || !args[0].equalsIgnoreCase("toggle")) {
            commandSender.sendMessage(Component.text("Usage: /" + label + " toggle").color(NamedTextColor.RED));
            return false;
        }

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(Component.text("You must be a player to use this command").color(NamedTextColor.RED));
            return false;
        }

        Entity lookingAt = player.getTargetEntity(32);

        if (!(lookingAt instanceof ItemFrame itemFrame)) {
            player.sendMessage(Component.text("You must be looking at an item frame to toggle its visibility").color(NamedTextColor.RED));
            return false;
        }

        // Call an interact event to check the user has access to the item frame

        PlayerInteractAtEntityEvent event = new PlayerInteractAtEntityEvent(player, itemFrame, itemFrame.getLocation().toVector());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            player.sendMessage(Component.text("You do not have permission to toggle this item frame").color(NamedTextColor.RED));
            return false;
        }

        boolean newVisible = !itemFrame.isVisible();
        itemFrame.setVisible(newVisible);

        MultiLib.notify(itemFrame.getChunk(), "invisibleitemframes:toggle", itemFrame.getUniqueId() + "\t" + newVisible);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("toggle");
        }

        return List.of();
    }
}
