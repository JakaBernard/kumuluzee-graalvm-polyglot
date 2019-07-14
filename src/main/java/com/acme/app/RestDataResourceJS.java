package com.acme.app;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("js")
public class RestDataResourceJS {

    @GET
    @Path("js-simple-data")
    public Response getSimpleData() {
        String output = "";
        try (Context context = Context.create()) {
            context.eval("js", "print('Hello JavaScript!'); console.log('what does console.log do?');");
            Value result = context.eval("js", 
            "(JSON.stringify({ " +
            "framework: 'KumuluzEE'" +
            "}))");
            System.out.println(result.asString());
            output = result.asString();
        }
        
        return Response.ok(output).build();
    }

    @GET
    @Path("js-custom-string")
    public Response getCustomString() {
        String output = "";
        try (Context context = Context.create()) {
            context.eval("js", "print('Hello JavaScript!'); console.log('what does console.log do?');");
            Value result = context.eval("js", 
            "(JSON.stringify({ " +
            "question: `${'foo'+7}, what could this be?`" +
            "}))");
            System.out.println(result.asString());
            output = result.asString();
        }
        
        return Response.ok(output).build();
    }

    // doesn't work (array of object keys)
    @GET
    @Path("js-object-keys")
    public Response getObjectKeys() {
        String output = "";
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            context.getBindings("js").putMember("javaObj", new MyObject());
            Value result = context.eval("js", 
            "(JSON.stringify({ data: Object.keys(javaObj).map(" +
            "   key => { " +
                "var textString = ''; "+
                    "if ( Array.isArray(javaObj[key])) { " +
                        "textString += javaObj[key].join('');" +
                    "} else {"+
                        "textString += javaObj[key];"+
                    "}"+
                " console.log(textString);" +
                "}"+
            ").join(' ')}))");
            System.out.println(result.asString());
            output = result.asString();
        }
        
        return Response.ok(output).build();
    }

    // doesn't work (stryingifying and object)
    @GET
    @Path("js-object-stringify")
    public Response getStringifiedObject() {
        String output = "";
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            context.getBindings("js").putMember("javaObj", new MyObject());
            Value result = context.eval("js", 
            "(JSON.stringify({data: JSON.stringify(javaObj) }))");
            System.out.println(result.asString());
            output = result.asString();
        }
        
        return Response.ok(output).build();
    }

    // doesn't work (array functions)
    @GET
    @Path("js-lambda-array-functions")
    public Response getArrayToString() {
        String output = "";
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            MyArrayObject obj = new MyArrayObject();
            Value result = context.eval("js", 
            "arr => arr.map(val => `I have a value ${val}`).join(', ')");
            System.out.println(result.execute(obj.stringArray).asString());
            System.out.println(result.execute(obj.intArray).asString());
            output = result.execute(obj.stringArray).asString() + " + " + result.execute(obj.intArray).asString();
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }

    // checks if provided data is an array
    @GET
    @Path("js-lambda-is-array")
    public Response getIsArray() {
        String output = "";
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            MyArrayObject obj = new MyArrayObject();
            Value result = context.eval("js", 
            "arr => Array.isArray(arr)");
            System.out.println(result.execute(obj.stringArray).asString());
            output = "Is value array?: " + result.execute(obj.stringArray).asString();
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }

    // doesn't work
    @GET
    @Path("js-lambda-reduce")
    public Response getReduce() {
        String output = "";
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            MyArrayObject obj = new MyArrayObject();
            Value result = context.eval("js", 
            "arr => arr.reduce((accumulator, currentValue) => accumulator + currentValue)");
            System.out.println(result.execute(obj.intArray).asString());
            output = "Does reducer work (should return array sum): " + result.execute(obj.intArray).asString();
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }

    // works
    @GET
    @Path("js-own-array")
    public Response getOwnArray() {
        String output = "";
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            MyArrayObject obj = new MyArrayObject();
            Value result = context.eval("js", 
            "[1, 2, 3, 4].map(val => `+${val}`).join(', ')");
            System.out.println(result.asString());
            output = "Own array: " + result.asString();
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }

}

class MyObject {

    public int value = 42;
    public String textValue = "someRandomText";
    public int[] intArray = new int[]{1, 2, 3, 4};
    public String text2 = "I random text number 2!";

}

class MyArrayObject {

    public String[] stringArray = new String[]{"one", "two", "three", "four"};
    public int[] intArray = new int[]{1, 2, 3, 4};

}

class SimpleStringObject {
    String result;

    SimpleStringObject(String data) {
        this.result = data;
    }

    SimpleStringObject(Integer data) {
        this.result = String.valueOf(data);
    }

    String stringify() {
        return "{\"result\": \"" + this.result + "\"}";
    }
}
