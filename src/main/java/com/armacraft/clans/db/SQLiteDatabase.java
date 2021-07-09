package com.armacraft.clans.db;

import com.armacraft.armalib.ArmaLib;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLiteDatabase {
    private Connection connection;

    public void connect() {
        File file = new File(ArmaLib.instance().getDataFolder(), "database.db");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            ArmaLib.instance().getLogger().log(Level.INFO, "Estabelecendo conexão com o banco...");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            ArmaLib.instance().getLogger().log(Level.INFO, "Conexão com o banco estabelecida!");
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
            ArmaLib.instance().getLogger().log(Level.SEVERE, "Erro durante conexão com o banco, desativando plugin.");
            ArmaLib.instance().getPluginLoader().disablePlugin(ArmaLib.instance());
        }
    }

    public Connection getConnection() {
        if(this.connection == null) {
            connect();
        }
        return connection;
    }
}

