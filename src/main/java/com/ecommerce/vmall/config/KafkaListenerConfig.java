package com.ecommerce.vmall.config;
/*
import java.util.List;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.ecommerce.vmall.pojo.Order;
import com.ecommerce.vmall.webcontroller.SubmitOrderController;

@Component
public class KafkaListenerConfig {

	@Autowired
	SubmitOrderController soc;
	
	@KafkaListener(id = "order3", topics = "handleorder3",containerFactory="batch")
    public void listen6(List<ConsumerRecord<String,Order>> si, Acknowledgment ack, Consumer<?, ?> consumer) {
		si.forEach(x->{
        	Order order = x.value();
        	soc.submitOrder(order);
            
        	//TODO:写回Kafka,宣告订单处理完毕，返回给客户;这是流式处理
          });
        
        consumer.commitAsync();
        
    }
}*/
