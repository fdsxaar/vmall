package client;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ClientMultiThreadedExecution {
	public void postOrder() {
        // Create an HttpClient with the PoolingHttpClientConnectionManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);

        try (final CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .build()) {
            // create an array of URIs to perform POSTs on
            final String[] urisToGet = {
                    "http://localhost:8080/vmall/submitorder/",
                    "http://localhost:8080/vmall/submitorder/"
            };

            // create a thread for each URI
            final PostThread[] threads = new PostThread[urisToGet.length];
            for (int i = 0; i < threads.length; i++) {
                final HttpPost httpPost = new HttpPost(urisToGet[i]);
                threads[i] = new PostThread(httpclient, httpPost);
            }

            // start the threads
            for (final PostThread thread : threads) {
                thread.start();
            }

            // join the threads
            for (final PostThread thread : threads) {
                thread.join();
            }

        }catch(Exception e) {
        	e.printStackTrace();
        }
    }
}
