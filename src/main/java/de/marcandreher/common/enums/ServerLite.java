package de.marcandreher.common.enums;

import java.time.LocalDateTime;

import de.marcandreher.fusionkit.core.database.Column;
import lombok.Data;

@Data
public class ServerLite {
    @Column("id")
    private int id;

    @Column("name")
    private String name;

    @Column("players")
    private int players;

    @Column("votes")
    private int votes;

    @Column("logo_loc")
    private String logoLoc;

    @Column("online")
    private boolean online;

    @Column("featured")
    private boolean featured;

    @Column("created")
    private LocalDateTime created;

    @Column("categories")
    private String categories;

    public String[] getCategories() {
        if (categories != null && !categories.isEmpty()) {
            return categories.split(", ");
        }
        return new String[0];
    }

    public String getSafeName() {
        return name.toLowerCase().replaceAll("[^\\p{Ll}\\p{N}!]", "-");
    }
}
