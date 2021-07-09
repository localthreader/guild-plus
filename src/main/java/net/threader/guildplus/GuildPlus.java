package net.threader.guildplus;

import com.armacraft.armalib.api.util.db.SQLUtils;
import net.threader.guildplus.command.ClanAcceptCommand;
import net.threader.guildplus.command.ClanAllyAcceptCommand;
import net.threader.guildplus.command.ClanAllyRequestCommand;
import net.threader.guildplus.command.ClanChangeNameCommand;
import net.threader.guildplus.command.ClanChangeTagCommand;
import net.threader.guildplus.command.ClanChatCommand;
import net.threader.guildplus.command.ClanChestCommand;
import net.threader.guildplus.command.ClanCommand;
import net.threader.guildplus.command.ClanCommandsCommand;
import net.threader.guildplus.command.ClanConfirmCommand;
import net.threader.guildplus.command.ClanCreateCommand;
import net.threader.guildplus.command.ClanDeclineCommand;
import net.threader.guildplus.command.ClanDemoteCommand;
import net.threader.guildplus.command.ClanDisbandCommand;
import net.threader.guildplus.command.ClanHomeCommand;
import net.threader.guildplus.command.ClanInviteCommand;
import net.threader.guildplus.command.ClanKickCommand;
import net.threader.guildplus.command.ClanLeaderChestCommand;
import net.threader.guildplus.command.ClanLeaveCommand;
import net.threader.guildplus.command.ClanMembersCommand;
import net.threader.guildplus.command.ClanPromoteCommand;
import net.threader.guildplus.command.ClanSetHomeCommand;
import net.threader.guildplus.command.ClanSetRankCommand;
import net.threader.guildplus.controller.instance.SingleClanController;
import net.threader.guildplus.controller.instance.SingleClanRankController;
import net.threader.guildplus.controller.instance.SingleMemberController;
import net.threader.guildplus.db.SQLiteDatabase;
import net.threader.guildplus.listener.ChatMessageListener;
import net.threader.guildplus.listener.DominationListeners;
import net.threader.guildplus.listener.EntityDamagedByEntityListener;
import net.threader.guildplus.listener.EntityDeathListener;
import net.threader.guildplus.listener.NametagDefineBusListener;
import net.milkbowl.vault.economy.Economy;
import net.threadly.commandexpress.CommandExpress;
import net.threadly.commandexpress.CommandSpec;
import net.threadly.commandexpress.args.GenericArguments;
import net.threadly.commandexpress.result.CommandResult;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Logger;

public class GuildPlus extends JavaPlugin {

    public static SQLiteDatabase DATABASE_ACESSOR;
    private static final Logger log = Logger.getLogger("Minecraft");
    private static GuildPlus instance;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        DATABASE_ACESSOR = new SQLiteDatabase();
        DATABASE_ACESSOR.connect();

        SQLUtils.executeScript(new BufferedReader
                        (new InputStreamReader(Objects.requireNonNull(GuildPlus.class.getResourceAsStream("/db/sql/setup.sql")))),
                DATABASE_ACESSOR.getConnection());
        SingleClanController.INSTANCE.downloadClans();
        SingleMemberController.INSTANCE.downloadMembers();
        SingleClanRankController.INSTANCE.startUpdateTask();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new ChatMessageListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamagedByEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new DominationListeners(), this);
        Bukkit.getPluginManager().registerEvents(new NametagDefineBusListener(), this);

        CommandSpec clanCreateCommand = CommandSpec.builder().executor(new ClanCreateCommand())
                .arguments(GenericArguments.string("tag"), GenericArguments.joinString("nome"))
                .build();

        CommandSpec clanInviteCommand = CommandSpec.builder().executor(new ClanInviteCommand())
                .arguments(GenericArguments.string("player")).build();

        CommandSpec clanKickCommand = CommandSpec.builder().executor(new ClanKickCommand())
                .arguments(GenericArguments.string("player")).build();

        CommandSpec clanLeaveCommand = CommandSpec.builder().executor(new ClanLeaveCommand()).build();

        CommandSpec clanDisbandCommand = CommandSpec.builder().executor(new ClanDisbandCommand()).build();

        CommandSpec clanPromoteCommand = CommandSpec.builder().executor(new ClanPromoteCommand())
                .arguments(GenericArguments.string("player")).build();

        CommandSpec clanDemoteCommand = CommandSpec.builder().executor(new ClanDemoteCommand())
                .arguments(GenericArguments.string("player")).build();

        CommandSpec clanDeclineCommand = CommandSpec.builder().executor(new ClanDeclineCommand()).build();

        CommandSpec clanAcceptCommand = CommandSpec.builder().executor(new ClanAcceptCommand()).build();

        CommandSpec clanCommandsCommand = CommandSpec.builder().executor(new ClanCommandsCommand()).build();

        CommandSpec clanMembersCommand = CommandSpec.builder().executor(new ClanMembersCommand()).build();

        CommandSpec clanChestLeaderCommand = CommandSpec.builder().executor(new ClanLeaderChestCommand()).playerOnly().build();

        CommandSpec clanSetHomeCommand = CommandSpec.builder().executor(new ClanSetHomeCommand()).playerOnly().build();

        CommandSpec clanHomeCommand = CommandSpec.builder().executor(new ClanHomeCommand()).playerOnly().build();

        CommandSpec clanChestCommand = CommandSpec.builder().executor(new ClanChestCommand()).playerOnly()
                .child(clanChestLeaderCommand, "lider").build();

        CommandSpec clanSetRank = CommandSpec.builder().executor(new ClanSetRankCommand()).playerOnly()
                .arguments(GenericArguments.string("player"), GenericArguments.string("rank")).build();

        CommandSpec clanSetTag = CommandSpec.builder().executor(new ClanChangeTagCommand())
                .playerOnly().arguments(GenericArguments.string("tag")).build();

        CommandSpec clanSetName = CommandSpec.builder().executor(new ClanChangeNameCommand())
                .playerOnly().arguments(GenericArguments.string("name")).build();

        CommandSpec clanSetCommand = CommandSpec.builder()
                .executor((context, args) -> CommandResult.builder().build())
                .child(clanSetTag, "name")
                .child(clanSetName, "tag")
                .build();

        CommandSpec clanConfirmCommand = CommandSpec.builder().executor(new ClanConfirmCommand()).build();

        CommandSpec configReloadCommand = CommandSpec.builder().executor((a,b) -> {
            GuildPlus.instance().reloadConfig();
            return CommandResult.builder().build();
        }).permission("armalib.adm").build();

        CommandSpec clanAllyAdd = CommandSpec.builder().arguments(GenericArguments.string("key"))
                .executor(new ClanAllyRequestCommand())
                .build();

        CommandSpec clanAllyAccept = CommandSpec.builder().executor(new ClanAllyAcceptCommand()).build();

        CommandSpec clanAllyCommand = CommandSpec.builder()
                .child(clanAllyAccept, "aceitar", "accept")
                .child(clanAllyAdd, "add", "adicionar", "convidar", "request")
                .build();

        CommandSpec clanCommand = CommandSpec.builder().executor(new ClanCommand()).playerOnly()
                .child(clanCreateCommand, "criar", "create")
                .child(clanCommandsCommand, "cmd", "comandos", "help", "?", "ajuda")
                .child(clanDeclineCommand, "recusar", "decline")
                .child(clanAcceptCommand, "aceitar", "accept")
                .child(clanMembersCommand, "members", "membros")
                .child(clanInviteCommand, "convidar", "invite")
                .child(clanKickCommand, "kick", "expulsar")
                .child(clanPromoteCommand, "promover", "promote")
                .child(clanDemoteCommand, "demote", "rebaixar")
                .child(clanLeaveCommand, "leave", "sair")
                .child(clanDisbandCommand, "disband", "deletar", "disbandar")
                .child(clanChestCommand, "chest")
                .child(clanSetHomeCommand, "sethome")
                .child(clanHomeCommand, "home")
                .child(clanConfirmCommand, "confirm", "confirmar")
                .child(configReloadCommand, "reload")
                .child(clanSetRank, "setrank")
                .child(clanSetCommand, "set")
                .child(clanAllyCommand, "ally", "alianca", "alliance")
                .build();

        CommandSpec clanChatCommand = CommandSpec.builder().executor(new ClanChatCommand())
                .arguments(GenericArguments.joinString("message")).build();

        CommandExpress.registerCommandEntryPoint(this, clanChatCommand, ".");
        CommandExpress.registerCommandEntryPoint(this, clanCommand, "clan");
    }

    @Override
    public void onDisable() {
        instance = null;
        try {
            DATABASE_ACESSOR.getConnection().close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Economy getEconomy() {
        return economy;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static GuildPlus instance() {
        return instance;
    }
}
