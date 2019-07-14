package com.acme.app;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("ruby")
public class RestDataResourceRuby {

    @GET
    @Path("ruby-lambda")
    public Response getLambdaResult() {
        Integer output = 0;
        try (Context context = Context.create()) {
            context.eval("ruby", "print('Hello Ruby!');");
            Value function = context.eval("python", "lambda x: x + 42");
            System.out.println(function.execute(0).asInt());
            output = function.execute(0).asInt();
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }

    @GET
    @Path("ruby-object")
    public Response getObject() {
        String output = "";
        try (Context context = Context.create()) {
            Value result = context.eval("ruby",
                "o ) Struct.new(:id, :text, :arr).new(" +
                "1, " +
                "'two', " +
                "['one', 'two', 'three', 'four']");
            Value array = result.getMember("array");
            output = array.getArrayElement(3) + " " + array.getArrayElement(1);
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }
}
