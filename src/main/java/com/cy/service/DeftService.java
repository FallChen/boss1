package com.cy.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

public class DeftService extends AbstractVerticle{


    @Override
    public void start() throws Exception {

        Vertx vertx = Vertx.vertx();
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route("/temp/route/").handler(x ->{

            HttpServerResponse response = x.response();
            response.setChunked(true);
            response.write("coom on 1\n");

            x.vertx().setTimer(5000,tid->{
                x.next();
            });
        });

        router.route("/temp/route/").handler(y->{
            HttpServerResponse response = y.response();
            response.write("coom on 2\n");
            y.vertx().setTimer(5000,tid->y.next());
        });

        router.route("/temp/route/").handler(z ->{
            HttpServerResponse response = z.response();
            response.write("coom on 3\n");
            z.response().end();
        });

        httpServer.requestHandler(router::accept).listen(8080);

    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(DeftService.class.getName());
        System.out.println("服务已启动！");
    }

}
