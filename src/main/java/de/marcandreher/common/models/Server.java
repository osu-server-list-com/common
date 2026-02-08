package de.marcandreher.common.models;

import java.time.LocalDateTime;

import de.marcandreher.fusionkit.core.database.Column;
import lombok.Data;

@Data
public class Server {

    @Column("id")
    private int id;

    @Column("name")
    private String name;

    @Column("players")
    private int players;

    @Column("votes")
    private int votes;

    @Column("expired")
    private int expired;

    @Column("ping")
    private int ping;

    @Column("url")
    private String url;

    @Column("devserver")
    private String devserver;

    @Column("apikey")
    private String apikey;

    @Column("logo_loc")
    private String logoLoc;

    @Column("online")
    private boolean online;

    @Column("featured")
    private boolean featured;

    @Column("created")
    private LocalDateTime created;

    @Column("visible")
    private boolean visible;

    @Column("locked")
    private boolean locked;

    @Column("discord_url")
    private String discordUrl;

    @Column("banner_url")
    private String bannerUrl;

    @Column("description")
    private String description;

    public String getSafeName() {
        return name.toLowerCase().replaceAll("[^\\p{Ll}\\p{N}!]", "-");
    }

}
