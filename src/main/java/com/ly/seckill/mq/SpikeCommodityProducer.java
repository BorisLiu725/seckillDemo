package com.ly.seckill.mq;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by BorisLiu on 2019/11/25
 *
 *  Direct Exchange :
 *  直连型交换机，根据消息携带的路由键将消息投递给对应队列。
 *
 * 大致流程，有一个队列绑定到一个直连交换机上，同时赋予一个路由键 routing key 。
 * 然后当一个消息携带着路由值为X，这个消息通过生产者发送给交换机时，交换机就会根据这个路由值X去寻找绑定值也是X的队列。
 *
 * Fanout Exchange
 * 扇型交换机，这个交换机没有路由键概念，就算你绑了路由键也是无视的。 这个交换机在接收到消息后，会直接转发到绑定到它上面的所有队列。
 *
 *Topic Exchange
 * 主题交换机，这个交换机其实跟直连交换机流程差不多，但是它的特点就是在它的路由键和绑定键之间是有规则的。
 * 简单地介绍下规则：
 *
 * *  (星号) 用来表示一个单词 (必须出现的)
 * #  (井号) 用来表示任意数量（零个或多个）单词
 * 通配的绑定键是跟队列进行绑定的，举个小例子
 * 队列Q1 绑定键为 *.TT.*          队列Q2绑定键为  TT.#
 * 如果一条消息携带的路由键为 A.TT.B，那么队列Q1将会收到；
 * 如果一条消息携带的路由键为TT.AA.BB，那么队列Q2将会收到
 *
 */
@Component
@Slf4j
public class SpikeCommodityProducer implements RabbitTemplate.ConfirmCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    public void send(JSONObject jsonObject){
        String jsonString = jsonObject.toJSONString();
        System.out.println("jsonString==>"+jsonString);
        String messAgeId = UUID.randomUUID().toString().replace("-", "");
        //封装消息
        Message message = MessageBuilder.withBody(jsonString.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON).setContentEncoding("utf-8").setMessageId(messAgeId)
                .build();
        //构建回调返回的数据（消息id）
        /**
         * 当mandatory标志位设置为true时
         * 如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息
         * 那么broker会调用basic.return方法将消息返还给生产者
         * 当mandatory设置为false时，出现上述情况broker会直接将消息丢弃
         */
        this.rabbitTemplate.setMandatory(true);
        this.rabbitTemplate.setConfirmCallback(this);
        //给每一条信息添加一个dataId，放在CorrelationData，这样在RabbitConfirmCallback返回失败的时候可以知道是哪个消息失败
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(jsonString);
        rabbitTemplate.convertAndSend(RabbitmqConfig.MODIFY_EXCHANGE_NAME,RabbitmqConfig.MODIFY_ROUTING_KEY,message,correlationData);

    }

    /**
     * 生产者确认机制，生产者在服务器端发送消息的时候，采用应答机制
     * */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String jsonString = correlationData.getId();
        System.out.println("消息id"+correlationData.getId());
        if (ack){
            log.info("==>使用MQ消息确认机制确保消息一定投递到MQ中成功！");
            return;
        }
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //生产者消息投递失败的话，采用递归重试机制
        send(jsonObject);
        log.info("==>使用MQ消息确认机制投递到MQ中失败！");
    }
}
