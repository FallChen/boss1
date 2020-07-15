package com.cy.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

import java.util.HashMap;
import java.util.Map;

public class ThirdService extends AbstractVerticle {

	private Map<String,NetSocket> map = new HashMap<>();

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		mainStart();
	}

	public Future<Void> mainStart(){
		Promise<Void> promise = Promise.promise();
		NetServer netServer = vertx.createNetServer();

		netServer.connectHandler(res ->{
			res.handler(buffer -> {
				System.out.println(buffer.toString());
				JsonObject jsonObject = new JsonObject(buffer.toString());
				String port = jsonObject.getString("port");
				String id = jsonObject.getString("id");
				String oper = jsonObject.getString("operation");
				if("create".equals(oper)){
					 map.put(id, res);
				}else if ("push".equals(oper)){
					NetSocket netSocket = map.get(id);
					if(null!=netSocket) {
						netSocket.write(buffer);
					}else {
						res.write("对方没有上线！");
					}
				}
			});
		});
		netServer.listen(8888,rest ->{
			boolean succeeded = rest.succeeded();
			if(succeeded){
				System.out.println("服务启动！");
			}
		});
		return promise.future();
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(ThirdService.class.getName());
	}
}
