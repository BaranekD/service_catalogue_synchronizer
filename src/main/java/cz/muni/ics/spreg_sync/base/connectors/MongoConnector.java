package cz.muni.ics.spreg_sync.base.connectors;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import cz.muni.ics.spreg_sync.base.properties.MongoConnectorProperties;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MongoConnector {

    private final MongoClient mongoClient;
    private final String dbName;

    @Autowired
    public MongoConnector(MongoConnectorProperties mongoConnectorProperties) {
        String connectionString = mongoConnectorProperties.getConnectionString();
        this.mongoClient = MongoClients.create(connectionString);
        this.dbName = mongoConnectorProperties.getDbName();
    }

}
