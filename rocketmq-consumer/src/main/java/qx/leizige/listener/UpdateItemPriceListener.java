package qx.leizige.listener;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import qx.leizige.common.MqConstants;
import qx.leizige.module.Item;
import qx.leizige.module.UpdateItem;

import java.nio.charset.StandardCharsets;

/**
 * 监听指定topic和tag的消息
 */
@Component
@RocketMQMessageListener(consumerGroup = MqConstants.UPDATE_ITEM_CUSTOMER_GROUP,
        topic = MqConstants.ITEM_TOPIC, selectorType = SelectorType.TAG,
        selectorExpression = MqConstants.UPDATE_ITEM_TAG)
public class UpdateItemPriceListener implements RocketMQListener<MessageExt> {

    Logger log = LoggerFactory.getLogger(AddItemListener.class);

    @Override
    public void onMessage(MessageExt message) {
        try {
            String jsonStr = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("consumerGroup:{},topic:{},tag:{},getMessage:{}",
                    MqConstants.UPDATE_ITEM_CUSTOMER_GROUP,
                    MqConstants.ITEM_TOPIC, MqConstants.UPDATE_ITEM_TAG, JSON.parseObject(jsonStr, UpdateItem.class));
        } catch (Exception e) {
            log.error("errorInfo:{}", e.getMessage());
        }
    }

}