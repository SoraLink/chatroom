package fragment.cs522.fragment.rest;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import fragment.cs522.fragment.entities.ChatMessage;
import fragment.cs522.fragment.settings.Settings;
import fragment.cs522.fragment.util.DateUtils;


/**
 * Created by dduggan.
 */

public class ChatHelper {

    public static final String DEFAULT_CHAT_ROOM = "_default";

    private Context context;

    public ChatHelper(Context context) {
        this.context = context;
    }

    // TODO provide a result receiver that will display a toast message upon completion
    public void register (String chatName, ResultReceiver receiver) {
        if (chatName != null && !chatName.isEmpty()) {
            Settings.saveChatName(context, chatName);
            RegisterRequest request = new RegisterRequest(chatName);
            addRequest(request, receiver) ;
        }
    }

    // TODO provide a result receiver that will display a toast message upon completion
    public void postMessage (String chatRoom, String text, ResultReceiver receiver) {
        if (text != null && !text.isEmpty()) {
            if (chatRoom == null || chatRoom.isEmpty()) {
                chatRoom = DEFAULT_CHAT_ROOM;
            }
            ChatMessage message = new ChatMessage();
            message.chatRoom = chatRoom;
            message.messageText = text;
            message.sender = Settings.getChatName(context);
            message.timestamp = DateUtils.now();
            message.latitude = 28.63;
            message.longitude = 74.92;
            PostMessageRequest request = new PostMessageRequest(message);
            addRequest(request, receiver) ;
        }
    }

    private void addRequest(Request request, ResultReceiver receiver) {
        context.startService(createIntent(context, request, receiver));
    }

    private void addRequest(Request request) {
        addRequest(request, null);
    }

    /**
     * Use an intent to send the request to a background service. The request is included as a Parcelable extra in
     * the intent. The key for the intent extra is in the RequestService class.
     */
    public static Intent createIntent(Context context, Request request, ResultReceiver receiver) {
        Intent requestIntent = new Intent(context, RequestService.class);
        requestIntent.putExtra(RequestService.SERVICE_REQUEST_KEY, request);

        if (receiver != null) {
            requestIntent.putExtra(RequestService.RESULT_RECEIVER_KEY, receiver);
        }
        return requestIntent;
    }

    public static Intent createIntent(Context context, Request request) {
        return createIntent(context, request, null);
    }

}
