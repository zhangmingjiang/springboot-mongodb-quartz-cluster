package com.job.schedule.repository;

import com.job.MongoDBConfig;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;


/**
 * Simplest possible implementation for accessing the MongoDB
 * to persist job history documents.
 * 
 * @author
 */
@Repository
public class JobRecordRepository {

    @Autowired
    private MongoDBConfig mongo;

    @Autowired
    MongoTemplate mt;

    public void add(Map<String, Object> keys) {
        System.out.println("Job is done :" + keys);
        MongoClient mc = mongo.mongoClient();
        MongoDatabase md = mc.getDatabase("jobs_demo");
        MongoCollection<Document> doc = md.getCollection("job_record");
        if (doc == null) {
            doc = mt.createCollection("job_record");
        }
        doc.insertOne(new Document(keys));
    }

}
