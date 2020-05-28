package com.cy.service;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class MoreRouteService extends AbstractVerticle {


    private SQLClient sqlClient;
//    private FreeMarkerTemplateEngine templateEngine;

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
        router.get("/html").handler(this::targetHtml);
        router.route("/js/*").handler(this::jsTest);
        router.route("/css/*").handler(this::cssTest);
//        router.get("/js/t.js").handler(this::targetJs);

//        router.get("/").handler(this::indexHandler);
//        router.get("/wiki/:page").handler(this::pageRenderingHandler); // <3>
//        router.post().handler(BodyHandler.create());  // <4>
//        router.post("/save").handler(this::pageUpdateHandler);
//        router.post("/create").handler(this::pageCreateHandler);
//        router.post("/delete").handler(this::pageDeletionHandler);

//        templateEngine = FreeMarkerTemplateEngine.create(vertx);

        server.requestHandler(router)   // <5>
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

    private void cssTest(RoutingContext context) {
        String path = context.request().path();
        String substring = path.substring(1, path.length());
        try (InputStream stream = MoreRouteService.class.getClassLoader().getResourceAsStream(substring);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
            String temp ="";
            StringBuilder all = new StringBuilder();
            while ((temp=bufferedReader.readLine())!=null){
                all.append(temp+"\r\n");
            }
            HttpServerResponse response = context.response();
            context.response().putHeader("Content-Type", "text/css");
            response.end(all.toString());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            context.fail(e);
        }
    }

    private void jsTest(RoutingContext context) {
        String path = context.request().path();
        String substring = path.substring(1, path.length());
        try (InputStream stream = MoreRouteService.class.getClassLoader().getResourceAsStream(substring);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
            String temp ="";
            StringBuilder all = new StringBuilder();
            while ((temp=bufferedReader.readLine())!=null){
                all.append(temp+"\r\n");
            }
            HttpServerResponse response = context.response();
            context.response().putHeader("Content-Type", "application/javascript;charset=UTF-8");
            response.end(all.toString());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            context.fail(e);
        }
//        context.response().end();
    }

    private void targetJs(RoutingContext context) {


        try (InputStream stream = MoreRouteService.class.getClassLoader().getResourceAsStream("js/t.js");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
            String temp ="";
            StringBuilder all = new StringBuilder();
            while ((temp=bufferedReader.readLine())!=null){
                all.append(temp);
            }
            HttpServerResponse response = context.response();
            context.response().putHeader("Content-Type", "application/javascript;charset=UTF-8");
            response.end(all.toString()+"\r\n");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            context.fail(e);
        }


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

    private void targetHtml(RoutingContext context){
        try (InputStream stream = MoreRouteService.class.getClassLoader().getResourceAsStream("test.html");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
            String temp ="";
            StringBuilder all = new StringBuilder();
            while ((temp=bufferedReader.readLine())!=null){
                all.append(temp+"\r\n");
            }
            HttpServerResponse response = context.response();
            context.response().putHeader("Content-Type", "text/html");
            ;
            response.end(all.toString());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            context.fail(e);
        }

    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(MoreRouteService.class.getName());
    }
}
