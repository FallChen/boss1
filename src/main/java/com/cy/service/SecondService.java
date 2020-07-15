package com.cy.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

import java.util.HashMap;
import java.util.Map;

public class SecondService extends AbstractVerticle {

	private static Map<String, NetSocket> map = new HashMap<>();

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		this.mainStart();
	}


	private Future<Void> mainStart() {
		Promise<Void> promise = Promise.promise();

		NetServer netServer = vertx.createNetServer();
		NetClient netClient = vertx.createNetClient();

		netServer.connectHandler(context -> {
			context.handler(result -> {
				String s = result.toString();
				System.out.println("service in:" + s);
				String[] split = s.split(":");
				String s1 = split[0];
				if ("ready".equals(s1)) {
					netClient.connect(Integer.parseInt(split[1]), "localhost", result2 -> {
						if (result2.succeeded()) {
							System.out.println(split[1] + "已连接");
							NetSocket result1 = result2.result();
							map.put(split[1], result1);
						}
					});
				} else {
					NetSocket netSocket = map.get(split[0]);
					netSocket.write(split[1]);
				}
			});
		});


		netServer.listen(8889, res -> {
			boolean succeeded = res.succeeded();
			if (succeeded) {
				System.out.println("8889 服务已启动");
			}
		});


		return promise.future();
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(SecondService.class.getName());
	}
}
