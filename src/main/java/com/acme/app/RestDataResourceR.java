package com.acme.app;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("r")
public class RestDataResourceR {

    @GET
    @Path("lambda")
    public Response getLambdaResult() {
        long start = System.currentTimeMillis();
        Integer output = 0;
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            // context.eval("R", "print('Hello R!');");
            Value function = context.eval("R", "function (x) x + 42");
            // System.out.println(function.execute(0).asInt());
            output = function.execute(0).asInt();
        }
        long duration = System.currentTimeMillis() - start;
        return Response.ok(new SimpleStringObject(output, duration).stringify()).build();
    }

    @GET
    @Path("object")
    public Response getObject() {
        long start = System.currentTimeMillis();
        String output = "";
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            Value result = context.eval("R",
                "list(" +
                "id = 1, " +
                "text = 'two', " +
                "array = c('one', 'two', 'three', 'four')" +
                ")");
            Value array = result.getMember("array");
            output = array.getArrayElement(3).asString() + " " + array.getArrayElement(1).asString();
        }
        long duration = System.currentTimeMillis() - start;
        return Response.ok(new SimpleStringObject(output, duration).stringify()).build();
    }
}
