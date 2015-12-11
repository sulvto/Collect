package me.qinchao.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class Test {
	public static void main(String[] args) {

		DBCollection collection = MongoManager.getCollection("qinchao", "test");
		
		
		final BasicDBObject basicDBObject = new BasicDBObject("name", "qinchao");
		basicDBObject.append("age", 19);
//		collection.insert(basicDBObject);

		final DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}
		cursor.close();
		
		
		

		MongoCollection<?> mongoCollection = MongoManager.getMongoCollection("qinchao", "test");
		FindIterable<?> find = mongoCollection.find();
		final MongoCursor<?> iterator = find.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());			
		}



	}
}
