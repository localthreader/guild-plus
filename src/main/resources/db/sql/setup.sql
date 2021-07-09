CREATE TABLE IF NOT EXISTS guilds (
    unique_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(20),
    tag VARCHAR(12),
    points INT,
    experience INT,
    deaths INT,
    kills INT
);

CREATE TABLE IF NOT EXISTS guild_allies(
    guild_id VARCHAR(64),
    ally_id VARCHAR(64),
    FOREIGN KEY (guild_id) REFERENCES guilds(unique_id),
    FOREIGN KEY (ally_id) REFERENCES guilds(unique_id),
    PRIMARY KEY(guild_id, ally_id)
);

CREATE TABLE IF NOT EXISTS members (
    unique_id VARCHAR(64) PRIMARY KEY,
    guild_id VARCHAR(64),
    chat_rank VARCHAR(16),
    office SMALLINT,

    FOREIGN KEY (guild_id) REFERENCES guilds(unique_id)
);

CREATE TABLE IF NOT EXISTS guild_homes (
    guild_id VARCHAR(64) PRIMARY KEY,
    world_id VARCHAR(64),
    X INT,
    Y INT,
    Z INT,

    FOREIGN KEY (guild_id) REFERENCES guilds(unique_id)
);