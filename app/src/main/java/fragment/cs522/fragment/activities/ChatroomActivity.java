/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package fragment.cs522.fragment.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.Date;

import fragment.cs522.fragment.R;
import fragment.cs522.fragment.async.QueryBuilder;
import fragment.cs522.fragment.contracts.ChatroomContract;
import fragment.cs522.fragment.dialog.SendMessage;
import fragment.cs522.fragment.entities.ChatMessage;
import fragment.cs522.fragment.entities.ChatRoom;
import fragment.cs522.fragment.managers.ChatroomManager;
import fragment.cs522.fragment.managers.MessageManager;
import fragment.cs522.fragment.managers.PeerManager;
import fragment.cs522.fragment.managers.TypedCursor;
import fragment.cs522.fragment.rest.ChatHelper;
import fragment.cs522.fragment.rest.ServiceManager;
import fragment.cs522.fragment.settings.Settings;
import fragment.cs522.fragment.util.ResultReceiverWrapper;


public class ChatroomActivity extends AppCompatActivity implements IIndexManager<ChatRoom>, ChatFragment.IChatListener, SendMessage.IMessageSender, ResultReceiverWrapper.IReceive {

    private final static String TAG = ChatroomActivity.class.getCanonicalName();

    private final static String SHOWING_CHATROOMS_TAG = "INDEX-FRAGMENT";

    private final static String SHOWING_MESSAGES_TAG = "CHAT-FRAGMENT";

    private final static String ADDING_MESSAGE_TAG = "ADD-MESSAGE-DIALOG";

    /*
     * Managers
     */
    private ChatroomManager chatroomManager;

    private MessageManager messageManager;

    private PeerManager peerManager;

    private ServiceManager serviceManager;

    private SendMessage sendMessage ;

    /**
     * Fragments
     */
    private boolean isTwoPane;

    private IIndexManager.Callback<ChatRoom> indexFragment;

    private ChatFragment chatFragment;


    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when message is sent.
     */
    private ResultReceiverWrapper sendResultReceiver;

    IndexFragment Fragment;

    FragmentTransaction fragmentTrans ;

    private EditText messageText;

    /*
	 * Called when the activity is first created. 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chatrooms);

        isTwoPane = getResources().getBoolean(R.bool.is_two_pane);

        if (!isTwoPane) {
            // TODO add an index fargment as the fragment in the frame layout
            Fragment = new IndexFragment() ;
            fragmentTrans = getFragmentManager().beginTransaction() ;

            fragmentTrans.add(R.id.fragment_container, Fragment) ;
            fragmentTrans.commit() ;
        }

        // TODO create the message and peer and chatroom managers
        chatroomManager = new ChatroomManager(this) ;
        chatroomManager.getAllChatroomsAsync(chatroomQueryListener) ;
        messageManager = new MessageManager(this) ;
        peerManager = new PeerManager(this) ;

        // TODO instantiate helper for service
        helper = new ChatHelper(this) ;

        // TODO initialize sendResultReceiver and serviceManager
        sendResultReceiver = new ResultReceiverWrapper(new Handler()) ;
        sendResultReceiver.setReceiver(this) ;
        serviceManager = new ServiceManager(this) ;
        serviceManager.scheduleBackgroundOperations() ;


        /**
         * Initialize settings to default values.
         */
        if (!Settings.isRegistered(this)) {
            Settings.getClientId(this);
            // TODO launch registration activity
            startActivity(new Intent(this, RegisterActivity.class));

        }

        /** Initiate sendMessage to open a dialog**/
        sendMessage = new SendMessage() ;

    }

    public void onResume() {
        super.onResume();
        sendResultReceiver.setReceiver(this);
        chatroomManager.getAllChatroomsAsync(chatroomQueryListener) ;
        serviceManager.scheduleBackgroundOperations();
    }

    public void onPause() {
        super.onPause();
        sendResultReceiver.setReceiver(null);
        serviceManager.cancelBackgroundOperations();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS and SETTINGS options
        MenuInflater inflater = getMenuInflater() ;
        inflater.inflate(R.menu.chatserver_menu, menu) ;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            // TODO PEERS provide the UI for viewing list of peers
            case R.id.peers:
                Intent peersIntent = new Intent(this, ViewPeersActivity.class) ;
                startActivity(peersIntent);
                break;

            // TODO SETTINGS provide the UI for settings
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;


            default:
        }
        return false;
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case RESULT_OK:
                // TODO show a success toast message
                Toast.makeText(getApplicationContext(), "send successfully", Toast.LENGTH_LONG).show() ;
                break;
            default:
                // TODO show a failure toast message
                Toast.makeText(getApplicationContext(), "Send failure", Toast.LENGTH_LONG).show() ;
                break;
        }
    }

    /**
     * Callbacks for index fragment
     */

    @Override
    public SimpleCursorAdapter getIndexTitles(Callback<ChatRoom> callback) {
        indexFragment = callback;
        indexFragment.setIndexTitle(getString(R.string.chat_rooms_title));
        chatroomManager.getAllChatroomsAsync(chatroomQueryListener);

        String[] from = {ChatroomContract.NAME};
        int[] to = { android.R.id.text1 };
        return new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1, null, from, to, 0);
    }

    @Override
    public void onItemSelected(ChatRoom chatroom) {
        if (isTwoPane) {
            // For two pane, push selection of chatroom to chat fragment, which will then
            // ask the parent activity to query the database.
            chatFragment=(ChatFragment) getFragmentManager().findFragmentById(R.id.chat_fragment);
            chatFragment.setChatroom(chatroom);
            fragmentTrans=getFragmentManager().beginTransaction() ;
            fragmentTrans.addToBackStack(null) ;
            fragmentTrans.commit() ;
        } else {
            // For single pane, replace index fragment with messages fragment
            chatFragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putString(ChatFragment.CHATROOM_KEY, chatroom.name);
            chatFragment.setArguments(args);
            // TODO replace index fragment
            fragmentTrans=getFragmentManager().beginTransaction() ;
            fragmentTrans.replace(R.id.fragment_container, chatFragment, TAG) ;
            fragmentTrans.addToBackStack(null) ;
            fragmentTrans.commit() ;

        }
    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        }
        else if (!isTwoPane) {
            getFragmentManager().popBackStack();
        }
        else {

        }
    }

    /**
     * Callbacks for querying database for chatrooms
     */

    private QueryBuilder.IQueryListener<ChatRoom> chatroomQueryListener = new QueryBuilder.IQueryListener<ChatRoom>() {

        @Override
        public void handleResults(TypedCursor<ChatRoom> results) {
            indexFragment.setTitles(results);
        }

        @Override
        public void closeResults() {
            indexFragment.clearTitles();
        }

    };

    /**
     * Callbacks for chat fragment
     */

    @Override
    public void getMessages(ChatRoom chatroom) {
//        messageManager.getAllMessagesAsync(chatroom, messageQueryListener);
        messageManager.getAllMessagesAsync(messageQueryListener);
    }

    @Override
    public void addMessageDialog(ChatRoom chatroom) {
        SendMessage.launch(this, ADDING_MESSAGE_TAG, chatroom);
    }

    /**
     * Callbacks for querying for messages
     */

    private QueryBuilder.IQueryListener<ChatMessage> messageQueryListener = new QueryBuilder.IQueryListener<ChatMessage>() {

        @Override
        public void handleResults(TypedCursor<ChatMessage> results) {
            if (chatFragment != null) {
                chatFragment.handleResults(results);
            }
        }

        @Override
        public void closeResults() {
            if (chatFragment != null) {
                chatFragment.closeResults();
            }
        }

    };

    /**
     * Callbacks for message posting dialog
     */

    @Override
    public void send(ChatRoom chatroom, String message, Double latitude, Double longitude, Date timestamp) {
        helper.postMessage(chatroom.name, message, sendResultReceiver);
    }
}