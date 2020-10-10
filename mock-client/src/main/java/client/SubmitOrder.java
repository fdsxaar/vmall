package client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class SubmitOrder {
	GenOrder genOrder = new GenOrder();
	public void postOrder() {
	  String jsonOrder = genOrder.genJsonOrder();
		
	   CloseableHttpClient httpclient = HttpClients.createDefault();
       HttpPost httpPost = new HttpPost("http://localhost:8080/submitorder/");
	   HttpEntity stringEntity = new StringEntity(jsonOrder,ContentType.APPLICATION_JSON);
	   httpPost.setEntity(stringEntity);
	   try {
		 httpclient.execute(httpPost);
	  } catch (IOException e) {
		e.printStackTrace();
	  }
	}
	
}
