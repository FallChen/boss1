package com.cy.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class MoreRouteService extends AbstractVerticle {


    private SQLClient sqlClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(MoreRouteService.class);


    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Future<Void> compose = getConntion().compose(v -> mainRoute());
        compose.setHandler(startPromise);
    }

    private Future<Void> mainRoute() {
        System.out.println(2);
        Promise<Void> promise = Promise.promise();
        HttpServer server = vertx.createHttpServer();   // <1>

        Router router = Router.router(vertx);   // <2>
        router.get("/commn").handler(this::autoIncrement);
        router.get("/commn").handler(this::indexHandler);

//        router.get("/").handler(this::indexHandler);
//        router.get("/wiki/:page").handler(this::pageRenderingHandler); // <3>
//        router.post().handler(BodyHandler.create());  // <4>
//        router.post("/save").handler(this::pageUpdateHandler);
//        router.post("/create").handler(this::pageCreateHandler);
//        router.post("/delete").handler(this::pageDeletionHandler);

//        templateEngine = FreeMarkerTemplateEngine.create(vertx);

        server
                .requestHandler(router)   // <5>
                .listen(8080, ar -> {   // <6>
                    if (ar.succeeded()) {
                        LOGGER.info("HTTP server running on port 8080");
                        promise.complete();
                    } else {
                        LOGGER.error("Could not start a HTTP server", ar.cause());
                        promise.fail(ar.cause());
                    }
                });

        return promise.future();
    }

    private Future<Void> getConntion() {
        System.out.println(1);
        Promise<Void> promise = Promise.promise();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("username", "root")
//                .put("password", "chen123")
                .put("password","Chen19951129]").put("database", "test");
        sqlClient = MySQLClient.createShared(vertx, jsonObject);
        promise.complete();
        return promise.future();
    }


    private void autoIncrement(RoutingContext context){
        System.out.println(4);
        sqlClient.getConnection(con ->{
           if(con.succeeded()){
               SQLConnection conn = con.result();
               conn.update("UPDATE count_table SET `count` = `count`+1 WHERE id = 1 ",res ->{
                  conn.close();
                  if(!res.succeeded()){
                      context.fail(res.cause());
                  }else {
                      context.next();
                  }
               });
           }
        });
    }
    private void indexHandler(RoutingContext context) {
        System.out.println(3);
        sqlClient.getConnection(car -> {
            if (car.succeeded()) {
                SQLConnection connection = car.result();
                connection.query("SELECT id,`count` FROM count_table ", res -> {
                    connection.close();
                    if (res.succeeded()) {
                        List<String> pages = res.result() // <1>
                                .getResults()
                                .stream()
                                .map(json -> json.getString(1))
                                .sorted()
                                .collect(Collectors.toList());

                        StringBuilder content = new StringBuilder();
                        for (String str:pages
                             ) {
                            content.append(str+"<br/>");
                        }
                        System.out.println(content.toString());
//                        context.put("title", "Wiki home");  // <2>
//                        context.put("pages", pages);
                        context.response().putHeader("Content-Type", "text/html");
                        context.response().end("<html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Title</title></head><body>本网页已经累计访问过："+content.toString()+"次了！<script></script></body></html>");  // <4>

                    } else {
                        context.fail(res.cause());  // <5>
                    }
                });
            } else {
                context.fail(car.cause());
            }
        });
    }


    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(MoreRouteService.class.getName());
    }
}
