package it.smartcommunitylab.goodtables.mqtt;

import java.net.URLDecoder;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import it.smartcommunitylab.goodtables.model.BucketNotificationEvent;

public class MqttMessageHandler implements IMqttMessageListener {
    private final static Logger _log = LoggerFactory.getLogger(MqttMessageHandler.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final BucketNotificationEvent notification;

    public MqttMessageHandler(BucketNotificationEvent notification,
            ApplicationEventPublisher applicationEventPublisher) {
        super();
        this.applicationEventPublisher = applicationEventPublisher;
        this.notification = notification;
    }

    public BucketNotificationEvent getNotification() {
        return notification;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        _log.debug("message arrived for topic " + topic);

        // check match or discard
        if (topic.endsWith(notification.getTopic())) {
            // message is JSON
            String payload = new String(message.getPayload(), "UTF-8");
            JSONObject json = new JSONObject(payload);
            _log.trace("dump message " + json.toString(1));

            // extract records
            JSONArray records = json.getJSONArray("Records");
            for (int i = 0; i < records.length(); i++) {
                JSONObject record = records.getJSONObject(i);
                String action = record.getString("eventName");

                JSONObject s3 = record.getJSONObject("s3");
                // string are urlencoded
                String bucket = URLDecoder.decode(s3.getJSONObject("bucket").getString("name"), "UTF-8");
                String key = URLDecoder.decode(s3.getJSONObject("object").getString("key"), "UTF-8");

                _log.debug("receive event " + action + " for bucket " + bucket + " key " + key);

                // check object
                if (notification.getBucket().equals(bucket) && key.endsWith(notification.getType())) {
                    // build event
                    BucketNotificationEvent event = new BucketNotificationEvent(this,
                            bucket, key, notification.getType(),
                            notification.getNotificationId(), notification.getTopic(), "trigger");

                    applicationEventPublisher.publishEvent(event);
                }
            }

        }
    }

}
