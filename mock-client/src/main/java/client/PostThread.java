package client;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class PostThread extends Thread {
	private final CloseableHttpClient httpClient;
    private final HttpContext context;
    private final HttpPost httpPost;
    //逃逸
	GenOrder genOrder = new GenOrder();

    public PostThread(final CloseableHttpClient httpClient, final HttpPost httpPost) {
        this.httpClient = httpClient;
        this.context = new BasicHttpContext();
        this.httpPost = httpPost;
    }

    /**
     * Executes the GetMethod
     */
    @Override
    public void run() {
    	for(int i=1000;i>0;i--) {
	  	    String jsonOrder = genOrder.genJsonOrder();
	  	    HttpEntity stringEntity = new StringEntity(jsonOrder,ContentType.APPLICATION_JSON);
		    httpPost.setEntity(stringEntity);
	        try {
	            try (CloseableHttpResponse response = httpClient.execute(httpPost, context)) {
	                 //处理返回
	                }
	        }catch (final Exception e) {
	    		e.printStackTrace();
	
	        }
	    }
    }

}

