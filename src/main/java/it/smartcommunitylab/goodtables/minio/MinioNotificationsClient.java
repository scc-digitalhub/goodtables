package it.smartcommunitylab.goodtables.minio;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.minio.messages.EventType;
import it.smartcommunitylab.goodtables.model.BucketNotification;

@Component
public class MinioNotificationsClient {
    private final static Logger _log = LoggerFactory.getLogger(MinioNotificationsClient.class);

    @Value("${minionotifications.endpoint}")
    private String ENDPOINT;

    @Value("${minionotifications.auth}")
    private String AUTH_MODE;

    @Value("${minionotifications.username}")
    private String USERNAME;

    @Value("${minionotifications.password}")
    private String PASSWORD;

    @Value("${minionotifications.token}")
    private String TOKEN;

    private final String API = "/api/events/";

    public BucketNotification registerNotification(BucketNotification bn) throws MinioException {
        try {
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = headers();

            String bucket = bn.getBucket();
            String extension = "." + bn.getType();
            JSONArray actions = new JSONArray();
            actions.put(EventType.OBJECT_CREATED_ANY.toString());

            JSONObject json = new JSONObject();
            json.put("bucket", bucket);
            json.put("actions", actions);
            json.put("prefix", "");
            json.put("suffix", extension);

            _log.trace("add notification json " + json.toString());

            HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

            // fetch response as String because it may not match the json schema
            ResponseEntity<String> response = template.exchange(ENDPOINT + API + bucket, HttpMethod.POST,
                    entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                // fetch JSON array inside object
                JSONObject re = new JSONObject(response.getBody());
                // update fields
                bn.setNotificationId(re.optString("id", ""));
                bn.setTopic(re.optString("topic", ""));

                return bn;

            } else {
                throw new MinioException("response error code " + response.getStatusCode());
            }

        } catch (RestClientException rex) {
            throw new MinioException("rest error " + rex.getMessage());
        }
    }

    public void unregisterNotification(BucketNotification bn) throws MinioException {
        try {
            RestTemplate template = new RestTemplate();
            HttpHeaders headers = headers();

            String bucket = bn.getBucket();
            String notificationId = bn.getNotificationId();
            String topic = bn.getTopic();

            if (!notificationId.isEmpty() && !topic.isEmpty()) {
                JSONObject json = new JSONObject();
                HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

                // fetch response as String because it contains the remove entity
                ResponseEntity<String> response = template.exchange(
                        ENDPOINT + API + bucket + "/" + notificationId, HttpMethod.DELETE, entity,
                        String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    // success
                } else {
                    throw new MinioException("response error code " + response.getStatusCode());
                }
            }
        } catch (RestClientException rex) {
//            rex.printStackTrace();
            throw new MinioException("rest error " + rex.getMessage());
        }
    }

//    public List<BucketNotification> importNotification(String bucket, List<String> extensions) {
//
//    }

    public void setAuthToken(String token) {
        TOKEN = token;
        // also ensure authmode is set
        AUTH_MODE = "token";
    }

    /*
     * Helpers
     */
    private HttpHeaders headers() throws RestClientException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if ("basic".equals(AUTH_MODE)) {
            String auth = USERNAME + ":" + PASSWORD;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("UTF-8")));
            String authHeader = "Basic " + new String(encodedAuth);

            headers.set("Authorization", authHeader);

        } else if ("token".equals(AUTH_MODE)) {
            // hardcoded "bearer" prefix
            headers.set("Authorization", "Bearer " + TOKEN);
        }

        return headers;
    }
}
