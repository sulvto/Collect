package me.qinchao.mongodb;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.Set;

public class Test {
	public static void main(String[] args) {

		MongoOperations mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new Mongo(), "qinchao"));
		Set<String> collectionNames = mongoOps.getCollectionNames();
		System.out.println(JSON.toJSONString(collectionNames));

//
//		DBCollection collection = MongoManager.getCollection("qinchao", "test");
//
//
//
//		final BasicDBObject basicDBObject = new BasicDBObject("name", "qinchao");
//		basicDBObject.append("age", 19);
////		collection.insert(basicDBObject);
//
//		final DBCursor cursor = collection.find();
//		while (cursor.hasNext()) {
//			System.out.println(cursor.next());
//		}
//		cursor.close();
//
//
//
//
//		MongoCollection<?> mongoCollection = MongoManager.getMongoCollection("qinchao", "test");
//		FindIterable<?> find = mongoCollection.find();
//		final MongoCursor<?> iterator = find.iterator();
//		while (iterator.hasNext()) {
//			System.out.println(iterator.next());
//		}



	}
}
