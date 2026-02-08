package de.marcandreher.common.models;

import de.marcandreher.fusionkit.core.database.Column;
import lombok.Data;

@Data
public class Endpoint {

    @Column("id")
    private int id;

    @Column("type")
    private String type;

    @Column("apitype")
    private String apiType;

    @Column("endpoint")
    private String url;

    @Column("dcbot")
    private boolean enabledDiscordBot;

    @Column("srv_id")
    private int serverId;

}