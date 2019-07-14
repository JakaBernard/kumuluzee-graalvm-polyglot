package com.acme.app;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("python")
public class RestDataResourcePython {

    @GET
    @Path("python-lambda")
    public Response getLambdaResult() {
        Integer output = 0;
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            context.eval("python", "print('Hello Python!');");
            Value function = context.eval("python", "lambda x: x + 42");
            System.out.println(function.execute(0).asInt());
            output = function.execute(0).asInt();
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }

    @GET
    @Path("python-object")
    public Response getObject() {
        String output = "";
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            Value result = context.eval("python",
                "type('obj', (object,), {" +
                "'id'  : 1, " +
                "'text': 'two', " +
                "'array': ['one', 'two', 'three', 'four']");
            Value array = result.getMember("array");
            output = array.getArrayElement(3) + " " + array.getArrayElement(1);
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }
}
