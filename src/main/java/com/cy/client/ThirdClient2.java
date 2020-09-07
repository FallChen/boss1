package com.cy.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.util.Scanner;


public class ThirdClient2 extends AbstractVerticle {

	private  NetSocket result3;
	private final String ID = "Client2";

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		mainStart();
	}

	private Future<Void> mainStart() {
		Promise<Void> promise = Promise.promise();
		NetClient netClient = vertx.createNetClient();
		netClient.connect(8888, "localhost", result -> {
			boolean succeeded = result.succeeded();
			if (succeeded) {
				System.out.println("2已启动");
				result3 = result.result();
				JsonObject jsonObject = new JsonObject();
				jsonObject.put("operation", "create");
				jsonObject.put("id", ID);
				Buffer buffer = Buffer.buffer(jsonObject.toString());
				result3.write(buffer);
				result3.handler(ctx -> {
					JsonObject jsonObject1 = new JsonObject(ctx.toString());
					System.out.println(jsonObject1.getString("id")+":"+jsonObject1.getString("context"));
				});
			}
		});
		return promise.future();
	}

	public void insert(String value) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.put("id","Client1");
		jsonObject.put("context",value);
		jsonObject.put("operation","push");
		result3.write(Buffer.buffer(jsonObject.toString()));
	}

	public static void main(String[] args) {
		ThirdClient2 thirdClient = new ThirdClient2();
		Vertx.vertx().deployVerticle(thirdClient);
		while (true) {
			Scanner scanner = new Scanner(System.in);
			String s = scanner.nextLine();
			thirdClient.insert(s);
		}
	}
}
