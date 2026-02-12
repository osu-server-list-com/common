package de.marcandreher.common.models;

import java.sql.Timestamp;

import de.marcandreher.fusionkit.core.database.Column;
import lombok.Data;

@Data
public class Incident {
    @Column("id")
    private int id;

    @Column("srv_id")
    private int serverId;

    @Column("timestamp")
    private Timestamp timestamp;

    @Column("url")
    private String url;

    @Column("message")
    private String message;

    @Column("response_code")
    private int responseCode;

    @Column("active")
    private boolean active;
}
