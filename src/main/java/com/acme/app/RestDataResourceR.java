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
    @Path("r-lambda")
    public Response getLambdaResult() {
        Integer output = 0;
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            context.eval("R", "print('Hello R!');");
            Value function = context.eval("R", "function (x) x + 42");
            System.out.println(function.execute(0).asInt());
            output = function.execute(0).asInt();
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }

    @GET
    @Path("r-object")
    public Response getObject() {
        String output = "";
        try (Context context = Context.create()) {
            Value result = context.eval("R",
                "list(" +
                "id = 1, " +
                "text = 'two', " +
                "array = c('one', 'two', 'three', 'four')");
            Value array = result.getMember("array");
            output = array.getArrayElement(3) + " " + array.getArrayElement(1);
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }
}
