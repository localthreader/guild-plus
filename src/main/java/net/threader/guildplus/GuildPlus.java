package net.threader.guildplus;

import net.milkbowl.vault.economy.Economy;
import net.threader.guildplus.command.GuildAcceptCommand;
import net.threader.guildplus.command.GuildAllyAcceptCommand;
import net.threader.guildplus.command.GuildAllyRequestCommand;
import net.threader.guildplus.command.GuildChangeNameCommand;
import net.threader.guildplus.command.GuildChangeTagCommand;
import net.threader.guildplus.command.GuildChatCommand;
import net.threader.guildplus.command.GuildChestCommand;
import net.threader.guildplus.command.GuildCommand;
import net.threader.guildplus.command.GuildCommandsCommand;
import net.threader.guildplus.command.GuildConfirmCommand;
import net.threader.guildplus.command.GuildCreateCommand;
import net.threader.guildplus.command.GuildDeclineCommand;
import net.threader.guildplus.command.GuildDemoteCommand;
import net.threader.guildplus.command.GuildDisbandCommand;
import net.threader.guildplus.command.GuildHomeCommand;
import net.threader.guildplus.command.GuildInviteCommand;
import net.threader.guildplus.command.GuildKickCommand;
import net.threader.guildplus.command.GuildLeaderChestCommand;
import net.threader.guildplus.command.GuildLeaveCommand;
import net.threader.guildplus.command.GuildMembersCommand;
import net.threader.guildplus.command.GuildPromoteCommand;
import net.threader.guildplus.command.GuildSetHomeCommand;
import net.threader.guildplus.command.GuildSetRankCommand;
import net.threader.guildplus.controller.instance.SingleGuildController;
import net.threader.guildplus.controller.instance.SingleGuildRankController;
import net.threader.guildplus.controller.instance.SingleMemberController;
import net.threader.guildplus.db.SQLiteDatabase;
import net.threader.guildplus.listener.ChatMessageListener;
import net.threader.guildplus.listener.EntityDamagedByEntityListener;
import net.threader.guildplus.listener.EntityDeathListener;
import net.threader.guildplus.utils.SQLUtils;
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
        SingleGuildController.INSTANCE.downloadGuilds();
        SingleMemberController.INSTANCE.downloadMembers();
        SingleGuildRankController.INSTANCE.startUpdateTask();

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new ChatMessageListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamagedByEntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathListener(), this);

        CommandSpec guildCreateCommand = CommandSpec.builder().executor(new GuildCreateCommand())
                .arguments(GenericArguments.string("tag"), GenericArguments.joinString("nome"))
                .build();

        CommandSpec guildInviteCommand = CommandSpec.builder().executor(new GuildInviteCommand())
                .arguments(GenericArguments.string("player")).build();

        CommandSpec guildKickCommand = CommandSpec.builder().executor(new GuildKickCommand())
                .arguments(GenericArguments.string("player")).build();

        CommandSpec guildLeaveCommand = CommandSpec.builder().executor(new GuildLeaveCommand()).build();

        CommandSpec guildDisbandCommand = CommandSpec.builder().executor(new GuildDisbandCommand()).build();

        CommandSpec guildPromoteCommand = CommandSpec.builder().executor(new GuildPromoteCommand())
                .arguments(GenericArguments.string("player")).build();

        CommandSpec guildDemoteCommand = CommandSpec.builder().executor(new GuildDemoteCommand())
                .arguments(GenericArguments.string("player")).build();

        CommandSpec guildDeclineCommand = CommandSpec.builder().executor(new GuildDeclineCommand()).build();

        CommandSpec guildAcceptCommand = CommandSpec.builder().executor(new GuildAcceptCommand()).build();

        CommandSpec guildCommandsCommand = CommandSpec.builder().executor(new GuildCommandsCommand()).build();

        CommandSpec guildMembersCommand = CommandSpec.builder().executor(new GuildMembersCommand()).build();

        CommandSpec guildChestLeaderCommand = CommandSpec.builder().executor(new GuildLeaderChestCommand()).playerOnly().build();

        CommandSpec guildSetHomeCommand = CommandSpec.builder().executor(new GuildSetHomeCommand()).playerOnly().build();

        CommandSpec guildHomeCommand = CommandSpec.builder().executor(new GuildHomeCommand()).playerOnly().build();

        CommandSpec guildChestCommand = CommandSpec.builder().executor(new GuildChestCommand()).playerOnly()
                .child(guildChestLeaderCommand, "lider").build();

        CommandSpec guildSetRank = CommandSpec.builder().executor(new GuildSetRankCommand()).playerOnly()
                .arguments(GenericArguments.string("player"), GenericArguments.string("rank")).build();

        CommandSpec guildSetTag = CommandSpec.builder().executor(new GuildChangeTagCommand())
                .playerOnly().arguments(GenericArguments.string("tag")).build();

        CommandSpec guildSetName = CommandSpec.builder().executor(new GuildChangeNameCommand())
                .playerOnly().arguments(GenericArguments.string("name")).build();

        CommandSpec guildSetCommand = CommandSpec.builder()
                .executor((context, args) -> CommandResult.builder().build())
                .child(guildSetTag, "name")
                .child(guildSetName, "tag")
                .build();

        CommandSpec guildConfirmCommand = CommandSpec.builder().executor(new GuildConfirmCommand()).build();

        CommandSpec configReloadCommand = CommandSpec.builder().executor((a,b) -> {
            GuildPlus.instance().reloadConfig();
            return CommandResult.builder().build();
        }).permission("guildplus.adm").build();

        CommandSpec guildAllyAdd = CommandSpec.builder().arguments(GenericArguments.string("key"))
                .executor(new GuildAllyRequestCommand())
                .build();

        CommandSpec guildAllyAccept = CommandSpec.builder().executor(new GuildAllyAcceptCommand()).build();

        CommandSpec guildAllyCommand = CommandSpec.builder()
                .child(guildAllyAccept, "aceitar", "accept")
                .child(guildAllyAdd, "add", "adicionar", "convidar", "request")
                .build();

        CommandSpec guildCommand = CommandSpec.builder().executor(new GuildCommand()).playerOnly()
                .child(guildCreateCommand, "criar", "create")
                .child(guildCommandsCommand, "cmd", "comandos", "help", "?", "ajuda")
                .child(guildDeclineCommand, "recusar", "decline")
                .child(guildAcceptCommand, "aceitar", "accept")
                .child(guildMembersCommand, "members", "membros")
                .child(guildInviteCommand, "convidar", "invite")
                .child(guildKickCommand, "kick", "expulsar")
                .child(guildPromoteCommand, "promover", "promote")
                .child(guildDemoteCommand, "demote", "rebaixar")
                .child(guildLeaveCommand, "leave", "sair")
                .child(guildDisbandCommand, "disband", "deletar", "disbandar")
                .child(guildChestCommand, "chest")
                .child(guildSetHomeCommand, "sethome")
                .child(guildHomeCommand, "home")
                .child(guildConfirmCommand, "confirm", "confirmar")
                .child(configReloadCommand, "reload")
                .child(guildSetRank, "setrank")
                .child(guildSetCommand, "set")
                .child(guildAllyCommand, "ally", "alianca", "alliance")
                .build();

        CommandSpec guildChatCommand = CommandSpec.builder().executor(new GuildChatCommand())
                .arguments(GenericArguments.joinString("message")).build();

        CommandExpress.registerCommandEntryPoint(this, guildChatCommand, ".");
        CommandExpress.registerCommandEntryPoint(this, guildCommand, "guild");
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
