package net.threader.guildplus.command;

import com.armacraft.armalib.api.util.SoundPlayer;
import com.armacraft.armalib.api.util.message.Message;
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

public class ClanChangeTagCommand implements CommandRunner {
    @Override
    public CommandResult execute(CommandContext commandContext, String[] strings) {
        Player player = (Player) commandContext.getSender();
        String tag = commandContext.getArgumentAs("tag", String.class);

        if(!CommonVerifiers.HAS_CLAN.test(player.getUniqueId())) {
            player.sendMessage(Message.Util.of("sem_clan", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        Member member = Member.of(player.getUniqueId()).get();

        if(!CommonVerifiers.HAS_OFFICE.test(player.getUniqueId(), Office.LEADER)) {
            player.sendMessage(Message.Util.of("cargo_insuficiente", GuildPlus.instance()));
            SoundPlayer.ERROR_SOUND.accept(player);
            return CommandResult.builder().build();
        }

        int minutes = GuildPlus.instance().getConfig().getInt("clan.cooldown.change_name_or_tag");
        if(Cooldown.CLAN_COOLDOWN.containsKey(member.getClan().getUniqueId())) {
            if(Cooldown.CLAN_COOLDOWN.get(member.getClan().getUniqueId()) == Cooldown.Type.CLAN_CREATE) {
                new Message.Builder().fromConfig("change_cooldown", GuildPlus.instance())
                        .addVariable("tempo", minutes).build().send(player);
                SoundPlayer.ERROR_SOUND.accept(player);
                return CommandResult.builder().build();
            }
        }

        member.getClan().setTag(tag);
        Cooldown.putInCooldown(member.getClan(), Cooldown.Type.CHANGE_SOMETHING, minutes*60);
        Bukkit.getScheduler().runTaskAsynchronously(GuildPlus.instance(), () -> {
            try(PreparedStatement st = GuildPlus.DATABASE_ACESSOR.getConnection().prepareStatement(
                    "UPDATE Clans SET Tag=? WHERE UniqueID=?")) {
                st.setString(1, tag);
                st.setString(2, member.getClan().getUniqueId().toString());
                st.executeUpdate();
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        return CommandResult.builder().result(Result.SUCCESS).message("Tag do clan atualizada com sucesso!").build();
    }
}
