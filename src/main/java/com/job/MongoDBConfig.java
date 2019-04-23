package com.job;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"com.job"})
@EnableMongoAuditing
public class MongoDBConfig extends AbstractMongoConfiguration {

    @Value("${mongodb.connectionString}")
    private String connectionString;

    @Value("${mongodb.dbname}")
    private String dbname;

    @Override
    public String getDatabaseName() {
        return dbname;
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(connectionString, MongoClientOptions.builder()
                .socketTimeout(3000)
                .minHeartbeatFrequency(25)
                .heartbeatSocketTimeout(3000)
        ));
    }

}
