package com.cy.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.time.LocalTime;
import java.util.Scanner;

public class FirstClient extends AbstractVerticle {
	private static NetSocket result ;
	@Override
	public void start() throws Exception {
		Future<Void> voidFuture = mainClient();
	}

	private Future<Void> mainClient(){
		Promise<Void> promise = Promise.promise();
		NetClient netClient = vertx.createNetClient();
		netClient.connect(8088,"localhost",context ->{
			if (context.succeeded()) {
				System.out.println("客户端连接服务端成功");
				result = context.result();
				// 向服务器写数据
				result.write(Buffer.buffer("Hello Vertx from Client!"));
				// 读取服务器的响应数据
				result.handler(buffer -> {
					System.out.println("接收到的数据为：" + buffer.toString()+ LocalTime.now().toString());
//					netClient.close();
				});
			} else {
				System.out.println("连接服务器异常");
			}
		});
		return promise.future();
	}

	public void insert(String value){
		result.write(Buffer.buffer(value));
	}

	public static void main(String[] args) {
		FirstClient firstClient = new FirstClient();
		Vertx.vertx().deployVerticle(firstClient);
		while (true){
			Scanner scanner = new Scanner(System.in);
			String s = scanner.nextLine();
			firstClient.insert(s);
		}
	}
}
