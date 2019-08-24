package com.acme.app;

import org.graalvm.polyglot.*;
import java.net.*;
import java.nio.file.*;
import java.io.*;

public class JSChroma {
    private Value chromaValue;
    private static JSChroma chromaInstance = null;

    private JSChroma() {
        Context context = Context.newBuilder().allowAllAccess(true).build();
        try {
            URL chromaFileUrl = Thread.currentThread().getContextClassLoader().getResource("chroma.min.js");
            String fileContent = new String(Files.readAllBytes(Paths.get(chromaFileUrl.toURI())));
            context.eval("js", fileContent);
            chromaValue = context.eval("js", "chroma");
        } catch (IOException error) {
            System.out.println("IO Exception!");
            System.out.println(error);
        } catch (URISyntaxException error) {
            System.out.println("URI Exception!");
            System.out.println(error);
        }
    }

    public static JSChroma createChromaIfNotExist() {
        if (chromaInstance == null) {
            chromaInstance = new JSChroma();
        }
        return chromaInstance;
    }

    public Value getJSChroma() {
        return chromaValue;
    }
}