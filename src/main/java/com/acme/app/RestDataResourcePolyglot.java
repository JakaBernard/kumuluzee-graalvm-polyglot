package com.acme.app;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.graalvm.polyglot.*;

import com.acme.app.JSChroma;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("polyglot")
public class RestDataResourcePolyglot {

    @GET
    @Path("color-wave/{colorHash}")
    @Produces(MediaType.TEXT_HTML)
    public Response getRainbowSpiral(@PathParam("colorHash") String colorHash) {
        long start = System.currentTimeMillis();
        String output = "";
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            JSChroma chromaInstance = JSChroma.createChromaIfNotExist();
            Value jsChroma = chromaInstance.getJSChroma();
            // klic JS konstruktorja z vrednostjo iz path parametra
            Value hexInstance = jsChroma.execute(colorHash);
            // pridobitev funkcije rgb
            Value member = hexInstance.getMember("rgb");
            // izvedba funkcije rgb (vrne tabelo [R, G, B])
            Value rgb = member.execute();
            // System.out.printf("Color: %s\n", rgb.toString());
            context.eval("R",
            "require(lattice);\n"+
            "tmpFile <- tempfile()\n"+
            "svg(tmpFile)\n"+
            "z <- 1:500\n"+
            "x <- sin(z)\n"+
            "y <- cos(z)\n"+
            "print(cloud(z~x*y, main='Color spiral', col=rgb("+rgb.getArrayElement(0).toString()+","+rgb.getArrayElement(1).toString()+","+rgb.getArrayElement(2).toString()+", maxColorValue = 255)))\n"+
            "dev.off()");
            Value tempFile = context.eval("R", "list(svg=paste0(readLines(tmpFile), collapse = ''))");
            context.eval("R", "file.remove(tmpFile)");
            output = new String(tempFile.getMember("svg").toString().substring(4));
            output = output.substring(1, output.length() - 1);
        }
        long duration = System.currentTimeMillis() - start;
        System.out.printf("Color spiral took %d ms\n", duration);
        return Response.ok(output).build();
    }

    @GET
    @Path("import-export-values/{minNumber}/{maxNumber}")
    @Produces(MediaType.TEXT_HTML)
    public Response getExportImportValues(@PathParam("minNumber") Integer minNumber, @PathParam("maxNumber") Integer maxNumber) {
        long start = System.currentTimeMillis();
        String output = "";
        if (minNumber > maxNumber) {
            output = "Min number (first) must be smaller or equal to max number!";
        } else {
            try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
                context.getPolyglotBindings().putMember("minNumber", minNumber);
                context.getPolyglotBindings().putMember("maxNumber", maxNumber);
                context.eval("js", "Polyglot.export('array10sq', Array(Polyglot.import('maxNumber')-Polyglot.import('minNumber')+1).fill().map((_, idx) => Polyglot.import('minNumber') + idx).map(val => val * val))");
                context.eval("R", "array10sq=t(sqrt(1*import('array10sq')))");
                context.eval("R", "scalar<-drop(array10sq%*%t(array10sq))");
                context.eval("R", "export('scalar', scalar)");
                Value scalar = context.getPolyglotBindings().getMember("scalar");
                output = String.valueOf(scalar.as(Object.class));
            }
        }
        long duration = System.currentTimeMillis() - start;
        System.out.printf("Import export took %d ms\n", duration);
        return Response.ok(output).build();
    }
}
