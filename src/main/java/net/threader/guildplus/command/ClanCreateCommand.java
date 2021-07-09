package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.post.ClanPostCreateEvent;
import net.threader.guildplus.api.event.pre.ClanPreCreateEvent;
import net.threader.guildplus.controller.ClanController;
import net.threader.guildplus.controller.instance.SingleClanController;
import net.threader.guildplus.model.Clan;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threader.guildplus.utils.Cooldown;
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
        FileConfiguration config = GuildPlus.instance().getConfig();
        ClanController clanController = SingleClanController.INSTANCE;

        if(CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("ja_tem_clan", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        int minutesCooldown = config.getInt("clan.cooldown.clan_create");
        if(Cooldown.PLAYER_COOLDOWN.containsKey(player.getUniqueId())) {
            new Message.Builder()
                    .addLine(Message.Util.of("criar_cooldown", GuildPlus.instance()))
                    .addVariable("tempo", minutesCooldown)
                    .build().send(player);
            return CommandResult.builder().result(Result.FAILURE).build();
        }

        if(config.getInt("clan_create_cost") > 0) {
            double cost = config.getDouble("clan.create.cost");
            Economy economy = GuildPlus.instance().getEconomy();
            if(economy.getBalance(player) <= cost) {
                return CommandResult.builder().message(Message.Util.of("sem_dinheiro", GuildPlus.instance())).result(Result.FAILURE).build();
            }
            economy.withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), cost);
        }

        if(name.length() > config.getInt("clan.create.max_name_size")) {
            new Message.Builder()
                    .addLine(Message.Util.of("nome_grande", GuildPlus.instance()))
                    .addVariable("size", config.getInt("clan.create.max_name_size"))
                    .build().send(player);
            return CommandResult.builder().result(Result.FAILURE).build();
        }

        if(tag.length() > config.getInt("clan.create.max_tag_size")) {
            new Message.Builder()
                    .addLine(Message.Util.of("tag_grande", GuildPlus.instance()))
                    .addVariable("size", config.getInt("clan.create.max_tag_size"))
                    .build().send(player);
            return CommandResult.builder().result(Result.FAILURE).build();
        }

        String rawTag = ChatColor.stripColor(tag);

        if(clanController.getClans().values().stream().anyMatch(clan -> ChatColor.stripColor(clan.getTag()).equalsIgnoreCase(rawTag))) {
            return CommandResult.builder().message(Message.Util.of("tag_existe", GuildPlus.instance())).result(Result.FAILURE).build();
        }

        if(clanController.getClans().values().stream().anyMatch(clan -> clan.getName().equalsIgnoreCase(name))) {
            return CommandResult.builder().message(Message.Util.of("nome_existe", GuildPlus.instance())).result(Result.FAILURE).build();
        }

        ClanPreCreateEvent preCreateEvent = new ClanPreCreateEvent(name, tag, player);
        Bukkit.getPluginManager().callEvent(preCreateEvent);
        if(!preCreateEvent.isCancelled()) {
            Clan clan = clanController.registerClan(player, tag, name);
            Cooldown.putInCooldown(player, Cooldown.Type.CLAN_CREATE, minutesCooldown*60);
            ClanPostCreateEvent postCreateEvent = new ClanPostCreateEvent(clan);
            Bukkit.getPluginManager().callEvent(postCreateEvent);
        }

        return CommandResult.builder().message(Message.Util.of("clan_criado", GuildPlus.instance())).result(Result.SUCCESS).build();
    }
}
