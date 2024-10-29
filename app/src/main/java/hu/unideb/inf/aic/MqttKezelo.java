package hu.unideb.inf.aic;

import static android.system.Os.connect;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class MqttKezelo {

    private MqttAndroidClient mqttAndroidClient;
    private final String brokerUrl = "CHANGE_ME"; // A broker URL-je
    private final String clientId = "AndroidClient";

    // A különböző topicok, amelyekre feliratkozik az alkalmazás
    private final String tempHumTopic = "zigbee2mqtt/Temp-Hum";
    private final String soilMoistureTopic = "zigbee2mqtt/SoilS";
    private final String valveTopic = "zigbee2mqtt/Valve";

    private double temperature;
    private double humidity;
    private double soilMoisture;
    private String valveState;

    private DBkezelo DBkezelo;

    public void MqttService(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, brokerUrl, clientId);
        DBkezelo = new DBkezelo(context);

        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                TopicFeliratkoz();
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d("MQTTconnection", "MQTT connection get lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());

                if (topic.equals(tempHumTopic)) {
                    // A raspberry pi irányából a topicban ilyen formában érkeznek az adatok: {"temperature":23.5, "humidity":60.2}
                    JSONObject jsonObject = new JSONObject(payload);
                    temperature = jsonObject.getDouble("temperature");
                    humidity = jsonObject.getDouble("humidity");
                } else if (topic.equals(soilMoistureTopic)) {
                    JSONObject jsonObject = new JSONObject(payload);
                    soilMoisture = jsonObject.getDouble("soil_moisture");
                } else if (topic.equals(valveTopic)) {
                    JSONObject jsonObject = new JSONObject(payload);
                    valveState = jsonObject.getString("state");
                    if (valveState.equals("ON")) {
                        valveState = "Nyitva";
                    } else {
                        valveState = "Zárva";
                    }
                }

                DBmentes();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d("MQTTdelivery", "Delivery completed.");
            }
        });

        connect();
    }

    private void connect() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    TopicFeliratkoz();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MQTTconnecOptions", "MQTT connection failure");
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }

    private void TopicFeliratkoz() {
        try {
            mqttAndroidClient.subscribe(tempHumTopic, 0);
            mqttAndroidClient.subscribe(soilMoistureTopic, 0);
            mqttAndroidClient.subscribe(valveTopic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void DBmentes() {
        boolean isInserted = DBkezelo.adatBe(temperature, humidity, soilMoisture, valveState);
        if (isInserted) {
            Log.d("MqttService", "Data saved to database successfully.");
        } else {
            Log.d("MqttService", "Failed to save data to database.");
        }
    }
}
