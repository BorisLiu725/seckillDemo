package com.ly.seckill.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;



/**
 * Created by BorisLiu on 2019/11/25
 */
@Component
public class RabbitmqConfig {

    //添加修改库存队列
    public static final String MODIFY_INVENTORY_QUERY = "modify_inventory_queue";
    //交换机名称
    public static final String MODIFY_EXCHANGE_NAME = "modify_exchange_name";
    //路由键
    public static final String MODIFY_ROUTING_KEY = "modifyRoutingKey";

    /**
     * 更新库存队列
     * */
    @Bean
    public Queue directModifyInventoryQuery(){
        return new Queue(MODIFY_INVENTORY_QUERY);
    }
    @Bean
    public DirectExchange directModifyExchange(){
        return new DirectExchange(MODIFY_EXCHANGE_NAME);
    }

    @Bean
    Binding bindingExchangeintegralDicQueue(){
        return BindingBuilder.bind(directModifyInventoryQuery()).to(directModifyExchange()).with(MODIFY_ROUTING_KEY);
    }
}
