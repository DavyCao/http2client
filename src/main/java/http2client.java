
import okhttp3.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import static java.lang.Thread.sleep;

public class http2client {

    protected static void run(String url, OkHttpClient client) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        try {
            assert(response.protocol() == Protocol.HTTP_2);
        } catch(AssertionError ex) {
            System.err.println("Wrong protocol!");
        }

        System.out.println(response.protocol());
        response.body().string();
    }

    public static void main(String[] args) throws Exception {

        int i = 1;
        int maxConnections = 1;
        int keepAliveDuration = 16000;

        ConnectionPool cp = new ConnectionPool(maxConnections, keepAliveDuration, TimeUnit.MILLISECONDS);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(cp)
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .build();

        final long startTime = System.currentTimeMillis();
        while (i <= 5) {
            run("https://cywings.com/test.txt", client);
            sleep(1000);
            i += 1;
        }
        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time: " + (endTime - startTime) );
    }
}