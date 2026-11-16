package com.example.cs460signinsignoutlab4.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Firebase Cloud Messaging events
 */
public class MessagingService extends FirebaseMessagingService {

    /**
     * Called whenever Firebase refreshes the device's messaging token.
     *
     * @param token The token used for sending messages to this application instance.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Token: "+ token);
    }

    /**
     * Logs whether a notification or data message was received
     *
     * @param message Remote message that has been received.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("FCM", "Message: "+message.getNotification().getBody());
    }

}
