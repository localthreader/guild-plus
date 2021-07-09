package net.threader.guildplus.db;

import net.threader.guildplus.GuildPlus;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLiteDatabase {
    private Connection connection;

    public void connect() {
        File file = new File(GuildPlus.instance().getDataFolder(), "database.db");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            GuildPlus.instance().getLogger().log(Level.INFO, "Estabelecendo conexão com o banco...");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            GuildPlus.instance().getLogger().log(Level.INFO, "Conexão com o banco estabelecida!");
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
            GuildPlus.instance().getLogger().log(Level.SEVERE, "Erro durante conexão com o banco, desativando plugin.");
            GuildPlus.instance().getPluginLoader().disablePlugin(GuildPlus.instance());
        }
    }

    public Connection getConnection() {
        if(this.connection == null) {
            connect();
        }
        return connection;
    }
}

