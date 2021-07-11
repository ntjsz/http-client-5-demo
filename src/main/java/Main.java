import org.apache.hc.client5.http.async.methods.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
    public static CloseableHttpAsyncClient client;

    public static void main(String[] args) throws Exception {
        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(Timeout.ofMilliseconds(250))
                .setSelectInterval(TimeValue.ofMilliseconds(50))
                .build();

        PoolingAsyncClientConnectionManager build = PoolingAsyncClientConnectionManagerBuilder.create()
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.LAX)
                .setMaxConnPerRoute(6).build();

        Main.client = HttpAsyncClients.custom()
                .setIOReactorConfig(ioReactorConfig)
                .setConnectionManager(build)
                .disableAutomaticRetries()
                .build();

        client.start();

        int sec = 100;
        start("http://127.0.0.1:8080/greeting", sec);
        Thread.sleep(1000);
        start("http://127.0.0.1:8080/greeting", sec);
       /* start("http://127.0.0.1:8080/greeting", sec);
        start("http://127.0.0.1:8080/greeting", sec);
        start("http://127.0.0.1:8080/greeting", sec);
        start("http://127.0.0.1:8080/greeting", sec);
        start("http://127.0.0.1:8080/greeting", sec);
        start("http://127.0.0.1:8080/greeting", sec);
        start("http://127.0.0.1:8080/greeting", sec);
        start("http://127.0.0.1:8080/greeting", sec);
        start("http://127.0.0.1:8080/greeting", sec);
        start("http://127.0.0.1:8080/greeting", sec);
        start("http://127.0.0.1:8080/greeting", sec);
        start("http://127.0.0.1:8080/greeting", sec);*/
        Thread.sleep(100000);
    }

    public static void start(String url, int milli) throws Exception {
        long now = System.currentTimeMillis();
        SimpleHttpRequest httpRequest = SimpleHttpRequest.create(Method.GET.name(), url);
        final SimpleHttpRequest request = SimpleRequestBuilder.copy(httpRequest)
                .addParameter("name", "124")
                .build();
        RequestConfig config = RequestConfig.copy(RequestConfig.DEFAULT)
                .setConnectTimeout(150, TimeUnit.MILLISECONDS)
                .setConnectionRequestTimeout(200, TimeUnit.MILLISECONDS)
                .setResponseTimeout(milli, TimeUnit.MILLISECONDS).build();
        request.setConfig(config);

        final Future<SimpleHttpResponse> future = client.execute(
                SimpleRequestProducer.create(request),
                SimpleResponseConsumer.create(),
                new FutureCallback<SimpleHttpResponse>() {

                    @Override
                    public void completed(final SimpleHttpResponse response) {
                        System.out.println(request + "->" + new StatusLine(response));
                        System.out.println(response.getBody());
                    }

                    @Override
                    public void failed(final Exception ex) {
                        System.out.println(request + "->" + ex);
                    }

                    @Override
                    public void cancelled() {
                        System.out.println(request + " cancelled");
                    }

                });
/*        try {
            future.get();
        } catch (Exception e){
            System.out.println(e);
        }*/
        System.out.println("send");
    }
}
