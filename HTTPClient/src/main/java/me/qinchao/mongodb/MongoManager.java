package me.qinchao.mongodb;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoManager {
	private final static String HOST = "localhost";// 端口
	private final static int PORT = 27017;// 端口
	private static MongoClient mongoClient = null;

	private MongoManager() {
	}

	static {
		intializeMongoClient();
	}

	public static MongoDatabase getMongoDatabase(String dbName) {
		return mongoClient.getDatabase(dbName);
	}
	
	
	public static MongoCollection<?> getMongoCollection(String dbName,String collectionName) {
		return getMongoDatabase(dbName).getCollection(collectionName);
	}
	
	
	
	public static DBCollection getCollection(String dbName,String collectionName) {
		return mongoClient.getDB(dbName).getCollection(collectionName);
	}

	/**
	 * 初始化连接池
	 */
	private static void intializeMongoClient() {
		mongoClient = new MongoClient(HOST, PORT);
	}
}
