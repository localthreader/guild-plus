package net.threader.guildplus.command;

import net.threader.guildplus.GuildPlus;
import net.threader.guildplus.model.Member;
import net.threader.guildplus.model.enums.Office;
import net.threader.guildplus.utils.CommonVerifiers;
import net.threader.guildplus.utils.Cooldown;
import net.threadly.commandexpress.CommandRunner;
import net.threadly.commandexpress.args.CommandContext;
import net.threadly.commandexpress.result.CommandResult;
import net.threadly.commandexpress.result.Result;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import net.threader.guildplus.utils.SoundPlayer;
import net.threader.guildplus.utils.message.Message;

public class GuildChangeNameCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        String name = commandContext.getArgumentAs("name", String.class);

        if(!CommonVerifiers.HAS_GUILD.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_guild", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(!CommonVerifiers.HAS_OFFICE.test(player.getUniqueId(), Office.LEADER)) {
            player.sendMessage(Message.Util.of("cargo_insuficiente", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        int minutes = GuildPlus.instance().getConfig().getInt("guild.cooldown.change_name_or_tag");
        if(Cooldown.GUILD_COOLDOWN.containsKey(member.getGuild().getUniqueId())) {
            if(Cooldown.GUILD_COOLDOWN.get(member.getGuild().getUniqueId()) == Cooldown.Type.GUILD_CREATE) {
                new Message.Builder().fromConfig("change_cooldown", GuildPlus.instance())
                        .addVariable("tempo", minutes).build().send(player);
                SoundPlayer.ERROR_SOUND.accept(player);
                return CommandResult.builder().build();
            }
        }

        member.getGuild().setName(name);
        Cooldown.putInCooldown(member.getGuild(), Cooldown.Type.CHANGE_SOMETHING, minutes*60);
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
            try(PreparedStatement st = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                    "UPDATE guilds SET name=? WHERE unique_id=?")) {
                st.setString(1, name);
                st.setString(2, member.getGuild().getUniqueId().toString());
                st.executeUpdate();
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        return CommandResult.builder().result(Result.SUCCESS).message("Nome da guilda atualizado com sucesso!").build();
    }
}
