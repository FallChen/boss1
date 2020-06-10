package com.cy.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
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
        Promise<Void> promise = Promise.promise();
        HttpServer server = vertx.createHttpServer();   // <1>

        Router router = Router.router(vertx);   // <2>
        router.get("/commn").handler(this::autoIncrement);
        router.get("/commn").handler(this::indexHandler);
        router.get("/html").handler(this::targetHtml);
        StaticHandler staticHandler = StaticHandler.create();
        staticHandler.setWebRoot("static");
        router.route("/static/*").handler(staticHandler);
//        router.route("/static/test.html").handler(this::staticHtml);
        router.route("/fileUpload").handler(this::fileUpload);
//        router.route("/static/js/*").handler(this::jsTest);
//        router.route("/css/*").handler(this::cssTest);

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

    private void staticHtml(RoutingContext context) {
        System.out.println("imok");
        context.reroute(HttpMethod.GET, "/static/test.html");
    }

    private void fileUpload(RoutingContext context) {
        System.out.println("iscon");
        Set<FileUpload> fileUploads = context.fileUploads();
        fileUploads.forEach(x->{
            String s = x.fileName();
            System.out.println(s);
        });
        context.response().end();
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

    private Future<Void> getConntion() {
        Promise<Void> promise = Promise.promise();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("username", "root")
                .put("password", "chen123")
//                .put("password","Chen19951129]")
                .put("database", "test");
        sqlClient = MySQLClient.createShared(vertx, jsonObject);
        promise.complete();
        return promise.future();
    }


    private void autoIncrement(RoutingContext context){
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
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.put("key",pages);
//                        context.response().putHeader("Content-Type", "text/html");
                        context.response().putHeader("Content-Type", "application/json;charset=UTF-8");
                        context.response().end(jsonObject.toBuffer());  // <4>
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
        try (InputStream stream = MoreRouteService.class.getClassLoader().getResourceAsStream("static/test.html");
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
            String temp ="";
            StringBuilder all = new StringBuilder();
            while ((temp=bufferedReader.readLine())!=null){
                all.append(temp+"\r\n");
            }
            HttpServerResponse response = context.response();
            context.response().putHeader("Content-Type", "text/html");
            response.end(all.toString());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            context.fail(e);
        }

    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MoreRouteService.class.getName());
    }
}
