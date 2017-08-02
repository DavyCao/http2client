
import okhttp3.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import static java.lang.Thread.sleep;

public class http2client {

    protected static void run(String url, OkHttpClient client) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection","keep-alive")
                .build();

        Response response = client.newCall(request).execute();
        response.body().string();
        System.out.print(response.protocol() + " ");
        try {
            assert(response.protocol() == Protocol.HTTP_2);
        } catch(AssertionError ex) {
            System.err.println("Wrong protocol!");
        }
    }

    public static void main(String[] args) throws Exception {

        int i = 1;
        int maxIdleConnections = 1;
        int keepAliveDuration = 16000;
        int rounds = Integer.parseInt(args[1]);

        ConnectionPool cp = new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MILLISECONDS);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(cp)
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .build();

        final long startTime = System.currentTimeMillis();
        while (i <= rounds) {
            run("https://cywings.com/" + args[0], client);
            sleep(10);
            i += 1;
        }
        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time: " + (endTime - startTime) );
    }
}