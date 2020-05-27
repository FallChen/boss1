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
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.Route;
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
//                    Integer integer = results.get(0).getInteger(1);
                    System.out.println(results);
                    HttpServerResponse response = z.response();
                    Buffer buffer = Buffer.buffer();
                    buffer.setString(0,results.toString());
//                    buffer.setString(0,results+"");
//                        response.write(buffer);
//                    response.setStatusCode(200);
//                    response.putHeader("content-type", "text/plain");
//                        response.putHeader("c",results+"");
//                        response.end("累计访问次数："+integer+"");
                    response.end(buffer);
                }else {
                    z.fail(qry.cause());
                }
            });
//            sqlClient.getConnection(car ->{
//                SQLConnection result = car.result();
//                result.query("SELECT * FROM count_table ", qry -> {
//                    result.close();
//                    if(qry.succeeded()){
//                        ResultSet result1 = qry.result();
//                        List<JsonArray> results = result1.getResults();
//                        Integer integer = results.get(0).getInteger(1);
//                        System.out.println(results);
//                        HttpServerResponse response = z.response();
//                        Buffer buffer = Buffer.buffer();
//                        buffer.setString(0,results+"");
////                        response.write(buffer);
//                        response.setStatusCode(200);
//                        response.putHeader("content-type", "text/plain");
////                        response.putHeader("c",results+"");
////                        response.end("累计访问次数："+integer+"");
//                        response.end(buffer);
//                    }else {
//                        z.fail(qry.cause());
//                    }
//                });
//            });
//            sqlClient.query("SELECT * FROM count_table ", qry -> {
//                if (qry.succeeded()) {
//                    ResultSet result = qry.result();
//                    List<JsonArray> results = result.getResults();
//                    System.out.println(results);
//                    Integer integer = results.get(0).getInteger(1);
//                    System.out.println(integer);
//                    z.response().write("访问次数：" + String.valueOf(integer) + "\n");
//                    z.response().end();
//                } else {
//                    System.out.println("查询数据库出错1");
//                }
//            });
        });


        router.route("/temp/route/com").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "application/json");
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("key","123");
            response.setStatusCode(200);
            response.end(jsonObject.toBuffer());
        });

        httpServer.requestHandler(router).listen(8080);
    }

//    @Override
//    public void start(Future<Void> startFuture) throws Exception {
//        JsonObject c = new JsonObject();
//        //配置数据库连接
//        c.put("username", "root").put("password", "1367356")
//                .put("host","192.168.100.91").put("database", "user");
//
//        sqlClient = MySQLClient.createShared(vertx, c);
//
//        Future<SQLConnection> sqlConnectionFuture = Future.future();
//
//        sqlClient.getConnection(sqlConnectionFuture);
//
//        sqlConnectionFuture.setHandler(connection -> {
//            if (connection.succeeded()) {
//                SQLConnection conn = connection.result();
//
//                Future<SQLRowStream> streamFuture1 = Future.future();
//                Future<List<User>> streamFuture2 = Future.future();
//                Future<List<JsonArray>> future = Future.future();
//
//                //public interface Future<T> extends AsyncResult<T>, Handler<AsyncResult<T>>
//                conn.queryStream("SELECT id, name, t_school_id FROM t_user", streamFuture1);
//                //前面sql语句执行的结果，交到streamFuture1容器里，或者由handler处理,Future<T>是继承了Handler<AsyncResult<T>>的。
//                //T是SQLRowStream，streamFuture1是Future<SQLRowStream>。
//                streamFuture1.compose(sqlRowStream -> {  //处理sqlRowStream
////            System.out.println(sqlRowStream.column("id"));  //0
////            System.out.println(sqlRowStream.column("name"));  //1
//                    List<User> users = new ArrayList<>();
//
//                    sqlRowStream.handler(jsonArray -> {  //sqlRowStream,转换为了jsonArray。["23","lisi","yuhuange"]
//                        System.out.println(jsonArray);
//                        users.add(new User(jsonArray));
//                    });
//
//                    System.out.println("user size: " + users.size()); //1
//                    streamFuture2.complete(users);
//
//                }, streamFuture2)//处理完成，有一个结果Future，继续处理。
//                        .compose(users -> {  //处理users
//                            List<JsonArray> list = new ArrayList<>();
//
//                            JsonArray collect = users.parallelStream()
//                                    .map(User::getSchoolId)
//                                    .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
//
//                            String sql2 = sql += Utils.placeholder("?", collect.size(), ", ") + ")";
//
//                            conn.queryStreamWithParams(sql2, collect, schoolResult -> {
//                                if (schoolResult.failed()){
//                                    schoolResult.cause().printStackTrace();
//                                    return;
//                                }
//                                schoolResult.result().handler(jsonArray1 -> {  //将schoolResult转化为json数据进行处理
//                                    if (jsonArray1 != null && jsonArray1.size() > 0)
//                                        list.add(jsonArray1);
//                                });
//
//                                future.complete(list);
//                            });
//                        }, future);
//
//                future.setHandler(list -> { //异步结果list
//                    conn.close(); // 关闭流
//                    if (list.failed()) {
//                        list.cause().printStackTrace();
//                        return;
//                    }
//                    System.out.println("-----");
//                    list.result().forEach(System.out::println);
//                });
//
//            } else {
//                connection.cause().printStackTrace();
//                System.err.println(connection.cause().getMessage());
//            }
//        });
//        startFuture.complete();
//    }
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(DeftService.class.getName());
        System.out.println("服务已启动！");
    }

}
