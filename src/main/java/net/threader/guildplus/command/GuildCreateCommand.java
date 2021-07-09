package net.threader.guildplus.command;

import net.milkbowl.vault.economy.Economy;
import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.api.event.post.GuildPostCreateEvent;
import net.threader.guildplus.api.event.pre.GuildPreCreateEvent;
import net.threader.guildplus.controller.GuildController;
import net.threader.guildplus.controller.instance.SingleGuildController;
import net.threader.guildplus.model.Guild;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threader.guildplus.utils.Cooldown;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildCreateCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        String tag = commandContext.getArgumentAs("tag", String.class);
        String name = commandContext.getArgumentAs("nome", String.class);
        FileConfiguration config = GuildPlus.instance().getConfig();
        GuildController guildController = SingleGuildController.INSTANCE;

        if(CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("ja_tem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        int minutesCooldown = config.getInt("guild.cooldown.guild_create");
        if(Cooldown.PLAYER_COOLDOWN.containsKey(player.getUniqueId())) {
            new Message.Builder()
                    .addLine(Message.Util.of("criar_cooldown", GuildPlus.instance()))
                    .addVariable("tempo", minutesCooldown)
                    .build().send(player);
            return CommandResult.builder().result(Result.FAILURE).build();
        }

        if(config.getInt("guild_create_cost") > 0) {
            double cost = config.getDouble("guild.create.cost");
            Economy economy = GuildPlus.instance().getEconomy();
            if(economy.getBalance(player) <= cost) {
                return CommandResult.builder().message(Message.Util.of("sem_dinheiro", GuildPlus.instance())).result(Result.FAILURE).build();
            }
            economy.withdrawPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), cost);
        }

        if(name.length() > config.getInt("guild.create.max_name_size")) {
            new Message.Builder()
                    .addLine(Message.Util.of("nome_grande", GuildPlus.instance()))
                    .addVariable("size", config.getInt("guild.create.max_name_size"))
                    .build().send(player);
            return CommandResult.builder().result(Result.FAILURE).build();
        }

        if(tag.length() > config.getInt("guild.create.max_tag_size")) {
            new Message.Builder()
                    .addLine(Message.Util.of("tag_grande", GuildPlus.instance()))
                    .addVariable("size", config.getInt("guild.create.max_tag_size"))
                    .build().send(player);
            return CommandResult.builder().result(Result.FAILURE).build();
        }

        String rawTag = ChatColor.stripColor(tag);

        if(guildController.getGuilds().values().stream().anyMatch(guild -> ChatColor.stripColor(guild.getTag()).equalsIgnoreCase(rawTag))) {
            return CommandResult.builder().message(Message.Util.of("tag_existe", GuildPlus.instance())).result(Result.FAILURE).build();
        }

        if(guildController.getGuilds().values().stream().anyMatch(guild -> guild.getName().equalsIgnoreCase(name))) {
            return CommandResult.builder().message(Message.Util.of("nome_existe", GuildPlus.instance())).result(Result.FAILURE).build();
        }

        GuildPreCreateEvent preCreateEvent = new GuildPreCreateEvent(name, tag, player);
        Bukkit.getPluginManager().callEvent(preCreateEvent);
        if(!preCreateEvent.isCancelled()) {
            Guild guild = guildController.registerGuild(player, tag, name);
            Cooldown.putInCooldown(player, Cooldown.Type.GUILD_CREATE, minutesCooldown*60);
            GuildPostCreateEvent postCreateEvent = new GuildPostCreateEvent(guild);
            Bukkit.getPluginManager().callEvent(postCreateEvent);
        }

        return CommandResult.builder().message(Message.Util.of("guild_criado", GuildPlus.instance())).result(Result.SUCCESS).build();
    }
}
