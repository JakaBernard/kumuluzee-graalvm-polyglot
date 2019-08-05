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
    @Path("simple-data")
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
    @Path("custom-string")
    public Response getCustomString() {
        String output = "";
        try (Context context = Context.create()) {
            context.eval("js", "print('Hello JavaScript!'); console.log('what does console.log do?');");
            Value result = context.eval("js", 
            "(JSON.stringify({ " +
            "question: `${'foo' + 42}, what could this be?`" +
            "}))");
            System.out.println(result.asString());
            output = result.asString();
        }
        
        return Response.ok(output).build();
    }

    // doesn't work (array of object keys)
    @GET
    @Path("object-keys")
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
    @Path("object-stringify")
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
    @Path("lambda-array-functions")
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

    // doesn't work (array functions on array cast to javascript array)
    @GET
    @Path("lambda-array-functions-from")
    public Response getArrayFromToString() {
        String output = "";
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            MyArrayObject obj = new MyArrayObject();
            Value result = context.eval("js", 
            "arr => Array.from(arr).map(val => `I have a value ${val}`).join(', ')");
            System.out.println(result.execute(obj.stringArray).asString());
            System.out.println(result.execute(obj.intArray).asString());
            output = result.execute(obj.stringArray).asString() + " + " + result.execute(obj.intArray).asString();
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }

    // checks if provided data is an array
    @GET
    @Path("lambda-is-array")
    public Response getIsArray() {
        String output = "";
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            MyArrayObject obj = new MyArrayObject();
            Value result = context.eval("js", 
            "arr => `${Array.isArray(arr)}`");
            System.out.println(result.execute(obj.stringArray).asString());
            output = "Is value array?: " + result.execute(obj.stringArray).asString();
        }
        
        return Response.ok(new SimpleStringObject(output).stringify()).build();
    }

    // doesn't work
    @GET
    @Path("lambda-reduce")
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
    @Path("own-array")
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

    @GET
    @Path("lambda")
    public Response getLambdaResult() {
        long start = System.currentTimeMillis();
        Integer output = 0;
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            // context.eval("js", "print('Hello Javascript!');");
            Value function = context.eval("js", "x => x + 42");
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
            Value result = context.eval("js",
                "({ " +
                "id  : 1, " +
                "text: 'two', " +
                "array: ['one', 'two', 'three', 'four'] })");
            Value array = result.getMember("array");
            output = array.getArrayElement(3).asString() + " " + array.getArrayElement(1).asString();
        }
        long duration = System.currentTimeMillis() - start;
        return Response.ok(new SimpleStringObject(output, duration).stringify()).build();
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
    long duration;
    String message;

    SimpleStringObject(String data, long duration) {
        this.result = data;
        this.duration = duration;
    }

    SimpleStringObject(Integer data, long duration) {
        this.result = String.valueOf(data);
        this.duration = duration;
    }

    SimpleStringObject(Integer data, String message) {
        this.result = String.valueOf(data);
        this.message = message;
    }

    SimpleStringObject(String data, String message) {
        this.result = data;
        this.message = message;
    }

    SimpleStringObject(String data) {
        this(data, 0l);
    }

    SimpleStringObject(Integer data) {
        this(data, 0l);
    }

    String stringify() {
        return "{\"result\": \"" + this.result + "\", \"duration\": \"" + String.valueOf(this.duration) +" ms\"}";
    }

    String stringifyMessage() {
        return "{\"result\": \"" + this.result + "\", \"duration\": \"" + String.valueOf(this.message) +"\"}";
    }
}
