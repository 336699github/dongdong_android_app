package com.cs646.luliu.dongdong00.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.cs646.luliu.dongdong00.R;
import com.cs646.luliu.dongdong00.model.NotificationItem;
import com.cs646.luliu.dongdong00.model.Question;
import com.cs646.luliu.dongdong00.ui.MainActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.SyncTree;
import com.firebase.client.core.view.Event;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luliu on 4/16/16.
 */
public class Utils {
    /**
     * Format the timestamp with SimpleDateFormat
     */
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private Context mContext = null;


    /**
     * Public constructor that takes mContext for later use
     */
    public Utils(Context con) {
        mContext = con;
    }


    /**
     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


    /**
     * Adds values to a pre-existing HashMap for updating a property for all of the Question copies.
     * The HashMap can then be used with {@link Firebase#updateChildren(Map)} to update the property
     * for all Question copies.
     */
    public static HashMap<String, Object> updateMapForAllWithValue(final String questionId, final String owner, HashMap<String, Object> mapToUpdate, String propertyToUpdate, Object valueToUpdate) {

        mapToUpdate.put("/" + Constants.FIREBASE_LOCATION_QUESTIONS_LIST + "/" + questionId, valueToUpdate);

        return mapToUpdate;
    }

    /**
     * Once an update is made to a Question, this method is repsonsible for updating the
     * reversed timestamp to be equal to the negation of the current timestamp. This comes after
     * the updateMapWithTimestampChanged because ServerValue.TIMESTAMP must be resolved to a long
     * value.
     */
    public static void updateTimestampReversed(FirebaseError firebaseError, final String logTagFromActivity, final String questionId, final String owner) {
        if (firebaseError != null) {
            Log.e(logTagFromActivity, "Error updating timestamp:" + firebaseError.getMessage());
        } else {
            final Firebase firebaseRef = new Firebase(Constants.FIREBASE_URL);
            firebaseRef.child(Constants.FIREBASE_LOCATION_QUESTIONS_LIST).child(owner)
                    .child(questionId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Question question = dataSnapshot.getValue(Question.class);
                    if (question != null) {
                        long timeReverse = -(question.getTimestampLastChangedLong());
                        String timeReverseLocation = Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED_REVERSE
                                + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP;

                        /**
                         * Create map and fill it in with deep path multi write operations list
                         */
                        HashMap<String, Object> updatedQuestionData = new HashMap<String, Object>();
                        updatedQuestionData.put(timeReverseLocation, timeReverse);

                        firebaseRef.updateChildren(updatedQuestionData);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(logTagFromActivity, "Error updating data: " + firebaseError.getMessage());
                }
            });

        }
    }

    public static void sendNotificationToOwner(final Context context, String sender_encoded_email, String sender_user_name, String receiver_encoded_email, String question_id, String answer_id, final String action_type) {
        final String action_message;

        Firebase notificationListFirebaseRef = new Firebase(Constants.FIREBASE_URL_NOTIFICATION_LIST + "/" + receiver_encoded_email);
        //Create a new location reference for the Notification
        Firebase newNotificationRef = notificationListFirebaseRef.push();
        //Save notificationId to maintain same random Id
        final String notification_id = newNotificationRef.getKey();

        //Set raw version of data to the ServerValue.TIMESTAMP value and save into timestampCreatedMap
        HashMap<String, Object> timestampCreated = new HashMap<>();
        timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        action_message = getActionMessage(action_type);

        NotificationItem newNotificationItem = new NotificationItem(sender_user_name, sender_encoded_email, action_type, action_message, question_id, answer_id, timestampCreated);
        HashMap<String, Object> newNotificationItemMap = (HashMap<String, Object>) new ObjectMapper().convertValue(newNotificationItem, Map.class);

        notificationListFirebaseRef.child(notification_id).updateChildren(newNotificationItemMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                //send notification to main screen

                if (!action_type.equals("answer")) {

                    showNotification(context, action_message);
                }

                Log.i("add Notification", "Completed");

            }
        });
    }




    private static String getActionMessage(String action_type) {
        switch (action_type) {
            case "upvote":
            case "downvote":
                return "voted on your answer";
            case "save":
                return "saved your answer";
            case "comment":
                return "commented your answer";
            case "thanks":
                return "gave you a thanks";
            case "watch":
                return "started watching your question";
            case "follow":
                return "started following you";
            case "answer":
                return "answered your question";
            default:
                return "something happened";
        }
    }

    public static void showNotification(Context context, String action_message) {
        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(context.getResources().getString(R.string.notification_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText("someone " + action_message)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

}