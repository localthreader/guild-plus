package com.armacraft.clans.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import com.armacraft.clans.ArmaClans;
import com.armacraft.clans.api.event.post.ClanPostCreateEvent;
import com.armacraft.clans.api.event.pre.ClanPreCreateEvent;
import com.armacraft.clans.controller.ClanController;
import com.armacraft.clans.controller.instance.SingleClanController;
import com.armacraft.clans.model.Clan;
import com.armacraft.clans.model.enums.Office;
import com.armacraft.clans.utils.CommonVerifiers;
import com.armacraft.clans.utils.Cooldown;
import net.milkbowl.vault.economy.Economy;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ClanCreateCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        String tag = commandContext.getArgumentAs("tag", String.class);
        String name = commandContext.getArgumentAs("nome", String.class);
        FileConfiguration config = ArmaClans.instance().getConfig();
        ClanController clanController = SingleClanController.INSTANCE;

        if(CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("ja_tem_clan", ArmaClans.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        int minutesCooldown = config.getInt("clan.cooldown.clan_create");
        if(Cooldown.PLAYER_COOLDOWN.containsKey(player.getUniqueId())) {
            new Message.Builder()
                    .addLine(Message.Util.of("criar_cooldown", ArmaClans.instance()))
                    .addVariable("tempo", minutesCooldown)
                    .build().send(player);
            return CommandResult.builder().result(Result.FAILURE).build();
        }

        if(config.getInt("clan_create_cost") > 0) {
            double cost = config.getDouble("clan.create.cost");
            Economy economy = ArmaClans.instance().getEconomy();
            if(economy.getBalance(player) <= cost) {
                return CommandResult.builder().message(Message.Util.of("sem_dinheiro", ArmaClans.instance())).result(Result.FAILURE).build();
            }
            economy.withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), cost);
        }

        if(name.length() > config.getInt("clan.create.max_name_size")) {
            new Message.Builder()
                    .addLine(Message.Util.of("nome_grande", ArmaClans.instance()))
                    .addVariable("size", config.getInt("clan.create.max_name_size"))
                    .build().send(player);
            return CommandResult.builder().result(Result.FAILURE).build();
        }

        if(tag.length() > config.getInt("clan.create.max_tag_size")) {
            new Message.Builder()
                    .addLine(Message.Util.of("tag_grande", ArmaClans.instance()))
                    .addVariable("size", config.getInt("clan.create.max_tag_size"))
                    .build().send(player);
            return CommandResult.builder().result(Result.FAILURE).build();
        }

        String rawTag = ChatColor.stripColor(tag);

        if(clanController.getClans().values().stream().anyMatch(clan -> ChatColor.stripColor(clan.getTag()).equalsIgnoreCase(rawTag))) {
            return CommandResult.builder().message(Message.Util.of("tag_existe", ArmaClans.instance())).result(Result.FAILURE).build();
        }

        if(clanController.getClans().values().stream().anyMatch(clan -> clan.getName().equalsIgnoreCase(name))) {
            return CommandResult.builder().message(Message.Util.of("nome_existe", ArmaClans.instance())).result(Result.FAILURE).build();
        }

        ClanPreCreateEvent preCreateEvent = new ClanPreCreateEvent(name, tag, player);
        Bukkit.getPluginManager().callEvent(preCreateEvent);
        if(!preCreateEvent.isCancelled()) {
            Clan clan = clanController.registerClan(player, tag, name);
            Cooldown.putInCooldown(player, Cooldown.Type.CLAN_CREATE, minutesCooldown*60);
            ClanPostCreateEvent postCreateEvent = new ClanPostCreateEvent(clan);
            Bukkit.getPluginManager().callEvent(postCreateEvent);
        }

        return CommandResult.builder().message(Message.Util.of("clan_criado", ArmaClans.instance())).result(Result.SUCCESS).build();
    }
}
