package com.example.mqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MainActivity extends AppCompatActivity {
    String host = "ssl://mr2xg4fgthgmv.messaging.solace.cloud:8883";
    String Username = "solace-cloud-client";
    String password = "mos9bagc51fv70u1ejk7l6rt28";
    String Mqtt_topic_pub = "mqtt/pub";
    String Mqtt_topic_sub = "mqtt/sub";
    MqttAndroidClient client;
    TextView subTextView;
    TextView command;
    MqttConnectOptions options;
    //option
    Vibrator vibrator;
    Ringtone myRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subTextView = findViewById(R.id.subTextView);
        command = findViewById(R.id.command);

        Button button_Connect = findViewById(R.id.button_connect);
        Button button_Disconnect = findViewById(R.id.button_Disconnect);

        //option
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        myRingtone = RingtoneManager.getRingtone(getApplicationContext(), uri);

        String clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(this.getApplicationContext(), host,
                        clientId);
        options = new MqttConnectOptions();
        options.setUserName(Username);
        options.setPassword(password.toCharArray());
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this,"connected",Toast.LENGTH_SHORT).show();
                    setSub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this,"connected failed",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                subTextView.setText(new String(message.getPayload()));

                //option
                vibrator.vibrate(500);
                myRingtone.play();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        button_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Toast.makeText(MainActivity.this,"connected",Toast.LENGTH_SHORT).show();
                            setSub();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Toast.makeText(MainActivity.this,"connected failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        button_Disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    IMqttToken token = client.disconnect();
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            Toast.makeText(MainActivity.this,"Disconnected",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Toast.makeText(MainActivity.this,"Disconnected failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public void publish(View v) {
        String topic = Mqtt_topic_pub;
//        String messenger = "LAB_606";
        String Commend = command.getText().toString();
        try {
            client.publish(topic, Commend.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    private void setSub(){
        try {
            client.subscribe(Mqtt_topic_sub,0);
        }catch (MqttException e) {
            e.printStackTrace();
        }
    }
}