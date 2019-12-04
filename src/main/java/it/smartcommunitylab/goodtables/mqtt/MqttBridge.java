package it.smartcommunitylab.goodtables.mqtt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.goodtables.common.InvalidArgumentException;
import it.smartcommunitylab.goodtables.common.SystemException;
import it.smartcommunitylab.goodtables.model.BucketNotification;
import it.smartcommunitylab.goodtables.model.BucketNotificationEvent;
import it.smartcommunitylab.goodtables.service.minio.MinioRegistrationService;

@Component
public class MqttBridge {
    private final static Logger _log = LoggerFactory.getLogger(MqttBridge.class);

    @Value("${mqtt.broker}")
    private String BROKER;

    @Value("${mqtt.username}")
    private String USERNAME;

    @Value("${mqtt.password}")
    private String PASSWORD;

    @Value("${mqtt.identity}")
    private String IDENTITY;

    @Value("${mqtt.topic}")
    private String TOPIC;

    @Value("${mqtt.qos}")
    private int QOS;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    MinioRegistrationService minioService;

    private boolean enabled;
    private IMqttClient client;
    private Map<String, MqttMessageHandler> handlers;

    private MqttCallbackExtended callback = new MqttCallbackExtended() {
        @Override
        public void connectionLost(Throwable t) {
            _log.debug("callback connectionLost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            _log.debug("callback connectComplete");
            if (reconnect) {
                try {
                    // subscribe all handlers
                    for (String id : handlers.keySet()) {
                        MqttMessageHandler handler = handlers.get(id);
                        // fetch topic from saved notification
                        String topic = TOPIC.isEmpty() ? handler.getNotification().getTopic()
                                : TOPIC + "/" + handler.getNotification().getTopic();

                        _log.debug("subscribe to topic " + topic);
                        client.subscribe(topic, QOS, handler);
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }

    };

    /*
     * Lifecycle
     */
    @PostConstruct
    public void init() {
        _log.debug("init mqtt router");
        enabled = false;
        handlers = new HashMap<>();

        if (!BROKER.isEmpty()) {
            try {
                // create client
                _log.debug("create client for " + BROKER);
                client = getClient();

                _log.debug("connect client");
                connect();

                enabled = true;

                // subscribe to ALL topics as defined by minio
                // TODO move to lifecycle event style to decouple
                try {
                    List<BucketNotification> notifications = minioService.listBucketNotification();
                    for (BucketNotification n : notifications) {
                        // build event
                        BucketNotificationEvent event = new BucketNotificationEvent(this,
                                n.getBucket(), "", n.getType(),
                                n.getNotificationId(), n.getTopic(), "subscribe");
                        // direct call
                        subscribe(event);
                    }
                } catch (Exception ex) {
                    // ignore
                    _log.error(ex.getMessage());
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    @PreDestroy
    public void cleanup() throws Exception {
        _log.debug("cleanup mqtt bridge");
        if (client != null) {
            if (client.isConnected()) {
                _log.debug("disconnect client from broker");
                disconnect();
            }
        }
    }

    /*
     * Handlers
     */

    private MqttMessageHandler handler(BucketNotificationEvent notification) {
        String id = notification.getNotificationId();
        if (!handlers.containsKey(id)) {
            _log.debug("build handler for notification " + String.valueOf(id));
            MqttMessageHandler handler = new MqttMessageHandler(notification, applicationEventPublisher);
            // save ref if successful
            handlers.put(id, handler);
        }

        return handlers.get(id);

    }

    /*
     * Methods
     */

    public void subscribe(BucketNotificationEvent notification)
            throws InvalidArgumentException, SystemException, MqttException {
        if (!enabled) {
            throw new SystemException("mqtt not available");
        }

        _log.debug("subscribe mqtt for notification " + notification.toString());

        if (notification.getNotificationId().isEmpty() || notification.getTopic().isEmpty()) {
            throw new InvalidArgumentException("invalid notification");
        }

        MqttMessageHandler handler = handler(notification);

        if (client.isConnected()) {
            // subscribe now
            String topic = TOPIC.isEmpty() ? handler.getNotification().getTopic()
                    : TOPIC + "/" + handler.getNotification().getTopic();
            _log.debug("subscribe to topic " + topic);
            client.subscribe(topic, QOS, handler);
        }

    }

    public void unsubscribe(BucketNotificationEvent notification)
            throws InvalidArgumentException, SystemException, MqttException {

        if (!enabled || !client.isConnected()) {
            throw new SystemException("mqtt not available");
        }

        if (notification.getNotificationId().isEmpty() || notification.getTopic().isEmpty()) {
            throw new InvalidArgumentException("invalid notification");
        }

        String id = notification.getNotificationId();
        if (handlers.containsKey(id)) {
            _log.debug("unsubscribe mqtt for notification " + notification.toString());

            MqttMessageHandler handler = handlers.get(id);
            // fetch topic from saved notification
            String topic = TOPIC.isEmpty() ? handler.getNotification().getTopic()
                    : TOPIC + "/" + handler.getNotification().getTopic();

            _log.debug("unsubscribe from topic " + topic);
            client.unsubscribe(topic);

            // clear
            handlers.remove(id);

        }

    }

    /*
     * Helpers
     */
//    private MqttMessageHandler handler(BucketNotification bn) {
//        long id = bn.getId();
//
//        if (!handlers.containsKey(id)) {
//            _log.debug("build handler for notification " + String.valueOf(id));
//            MqttMessageHandler handler = new MqttMessageHandler(bn, applicationEventPublisher);
//            handlers.put(id, handler);
//        }
//
//        return handlers.get(id);
//    }

    private void connect() throws MqttSecurityException, MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        client.setCallback(callback);
        client.connect(options);
    }

    private void disconnect() throws MqttException {
        client.disconnect();
    }

    private IMqttClient getClient() throws MqttException {
        if (IDENTITY.isEmpty()) {
            // generate with random
            IDENTITY = "goodtables-" + UUID.randomUUID().toString();
        }

        return new MqttClient(BROKER, IDENTITY);

    }
}
