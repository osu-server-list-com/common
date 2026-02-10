package de.marcandreher.common.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;

import de.marcandreher.common.enums.APIType;
import de.marcandreher.common.enums.EndpointType;
import de.marcandreher.fusionkit.core.FusionKit;
import de.marcandreher.fusionkit.core.database.Database;

public class EndpointHelper {
    private final String ENDPOINT_SQL = "SELECT `name`, `dcbot` FROM `un_endpoints` LEFT JOIN `un_servers` ON `un_endpoints`.`srv_id` = `un_servers`.`id` WHERE `type` = ? AND `visible` = 1 % ORDER BY `votes` DESC";
    private final Logger logger = FusionKit.getLogger(EndpointHelper.class);

    public String[] getBotSupportedServers(EndpointType type, APIType... endpoints) {
            ArrayList<String> serverList = new ArrayList<>();
            
            String endpointSql = "";
            for (int i = 0; i < endpoints.length; i++) {
                if (i == 0) {
                    endpointSql += " AND (`apitype` = '" + endpoints[i].name() + "'";
                } else {
                    endpointSql += " OR `apitype` = '" + endpoints[i].name() + "'";
                }
            }
            endpointSql += ")";

            if(type == EndpointType.VOTE || type == EndpointType.RECENT || type == EndpointType.BEST) {
                serverList.add("Bancho");
            }

            try (var mysql = Database.getConnection()) {
                ResultSet endpointResult = mysql.query(ENDPOINT_SQL.replaceAll("%", endpointSql), type.name()).executeQuery();
                while (endpointResult.next()) {
                    if (!endpointResult.getBoolean("dcbot"))
                        continue;
                    serverList.add(endpointResult.getString("name"));
                }


                return serverList.toArray(new String[0]);
            } catch (SQLException e) {
                logger.error("Error fetching servers for endpoint " + type.name(), e);
            }

            return new String[0];
        }
}
