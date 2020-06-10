package com.cy.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.Router;

import java.util.List;

public class DeftService extends AbstractVerticle{


    private SQLClient sqlClient;


    @Override
    public void start() throws Exception {

        JsonObject c = new JsonObject();
        c.put("user","root").put("username","root")
                .put("password","chen123")
//                .put("password","Chen1995129]")
                .put("database","test");
//                .put("","");

        sqlClient=MySQLClient.createShared(vertx,c);


        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route("/temp/route/").blockingHandler(x ->{
            HttpServerResponse response = x.response();
            response.setChunked(true);
            response.write("coom on 1\n");
            x.next();
        });

        router.route("/temp/route/").handler(y->{
            HttpServerResponse response = y.response();
            response.write("coom on 2\n");
            sqlClient.update("UPDATE count_table SET `count` = `count`+1 WHERE id = 1 ",wst->{
                if(wst.succeeded()){
                    UpdateResult result = wst.result();
                    JsonObject entries = result.toJson();
                    System.out.println(entries);
                }
            });
            y.next();
        });

        router.route("/temp/route/cour").handler(z ->{
            System.out.println("iscomming");
//            sqlClient=MySQLClient.createNonShared(vertx,c);
            sqlClient.query("SELECT * FROM count_table ", qry -> {
                if(qry.succeeded()){
                    ResultSet result1 = qry.result();
                    List<JsonArray> results = result1.getResults();
                    HttpServerResponse response = z.response();
                    Buffer buffer = Buffer.buffer();
                    buffer.setString(0,results.toString());
                    response.end(buffer);
                }else {
                    z.fail(qry.cause());
                }
            });
        });


        router.route("/temp/route/com").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json");
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("key","123");
            response.end(jsonObject.toBuffer());
        });

        httpServer.requestHandler(router).listen(8080);
    }


    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(DeftService.class.getName());
        System.out.println("服务已启动！");
    }

}
