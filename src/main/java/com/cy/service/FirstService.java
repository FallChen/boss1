package com.cy.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.impl.NetSocketImpl;

import java.time.LocalTime;

public class FirstService extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		Future<Void> compose = this.mainStart();

	}

	private Future<Void> mainStart(){
		Promise<Void> promise = Promise.promise();
		NetServer netServer = vertx.createNetServer();
		netServer.connectHandler(x->{
			x.handler(y->{
				// 在这里应该解析报文，封装为协议对象，并找到响应的处理类，得到处理结果，并响应
				System.out.println("接收到的数据为：" + y.toString()+ LocalTime.now().toString());
				// 按照协议响应给客户端
				x.write(Buffer.buffer("Hello Vertx from Server!"));
			});
			x.closeHandler(y->{
				System.out.println("链接关闭！");
			});
		});
		netServer.listen(8088,netServerAsyncResult -> {
			if(netServerAsyncResult.succeeded()){
				System.out.println("服务启动成功！");
			}
		});
		return promise.future();
	}

	public static void main(String[] args) {
		Vertx.vertx().deployVerticle(FirstService.class.getName());
	}
}
