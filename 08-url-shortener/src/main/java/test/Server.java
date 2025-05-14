package test;

import com.google.gson.Gson;
import io.seruco.encoding.base62.Base62;

import static spark.Spark.*;

public class Server {
    public static void main(String[] args) {
        post("/shortener/api/v1", (req, res) -> {
            URL url = new Gson().fromJson(req.body(), URL.class);
            System.out.println("url " + url.getUrlValue());
            Base62 instance = Base62.createInstance();
            byte[] encode = instance.encode(url.getUrlValue().getBytes());
            System.out.println("encode " + new String(encode));

//            gson.toJson(url);
            return req.url() + "/" + encode;
        });
//        get("/hello/:url", (req,res)->{
//            res.redirect("/new-url", 301);
//            return "";
//        });
    }
}