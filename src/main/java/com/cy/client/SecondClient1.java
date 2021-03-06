package com.cy.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

import java.util.Scanner;

public class SecondClient1 extends AbstractVerticle {

	private static NetSocket result1;


	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		this.mainStart();
	}

	public void insert(String value) {
		result1.write(Buffer.buffer(value));
	}

	private Future<Void> mainStart() {
		Promise<Void> promise = Promise.promise();

		NetServer netServer = vertx.createNetServer();
		NetClient netClient = vertx.createNetClient();

		netServer.connectHandler(context -> {
			context.handler(y -> {
				String s = y.toString();
				System.out.println("service in:" + s);
			});
		});


		netServer.listen(8887, res -> {
			boolean succeeded = res.succeeded();
			if (succeeded) {
				System.out.println("8887 服务已启动");
				netClient.connect(8889, "localhost", result -> {
					if (result.succeeded()) {
						System.out.println("8889 已连接");
						result1 = result.result();
						result1.write("ready:8887");
					}
				});
			}
		});


		return promise.future();
	}

	public static void main(String[] args) {

		SecondClient1 firstClient = new SecondClient1();
		Vertx.vertx().deployVerticle(firstClient);
		while (true) {
			Scanner scanner = new Scanner(System.in);
			String s = scanner.nextLine();
			firstClient.insert("8888:" + s);
		}
	}

}
