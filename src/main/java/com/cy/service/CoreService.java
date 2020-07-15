package com.cy.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class CoreService extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Future<Void> compose = getStart().compose(v -> getConntion());
        compose.setHandler(startPromise);
    }

    private Future<Void> getStart(){
        Promise<Void> promise = Promise.promise();
        promise.complete();
        return promise.future();
    }

    private Future<Void> getConntion(){
        Promise<Void> promise = Promise.promise();
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route("/other").handler(this::mainCore);
        httpServer.requestHandler(router).listen(8081,ar->{
            if(ar.succeeded()){
                promise.complete();
            }else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    private void mainCore(RoutingContext context) {
        HttpServerResponse response = context.response();
        context.response().putHeader("Content-Type", "text/html");
        response.end("yes");
    }
}
