/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package fragment.cs522.fragment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fragment.cs522.fragment.R;
import fragment.cs522.fragment.rest.ChatHelper;
import fragment.cs522.fragment.settings.Settings;
import fragment.cs522.fragment.util.ResultReceiverWrapper;


public class RegisterActivity extends AppCompatActivity implements OnClickListener, ResultReceiverWrapper.IReceive {

	final static public String TAG = RegisterActivity.class.getCanonicalName();
		
    /*
     * Widgets for dest address, message text, send button.
     */
    private TextView clientIdText;

    private EditText userNameText;

    private EditText serverUriText;

    private Button registerButton;

    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when registered.
     */
    private ResultReceiverWrapper registerResultReceiver;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Initialize settings to default values.
         */
		if (Settings.isRegistered(this)) {
			finish();
            return;
		}

        setContentView(R.layout.register);

        // TODO instantiate helper for service
        helper = new ChatHelper(this);

        // TODO initialize registerResultReceiver
        registerResultReceiver = new ResultReceiverWrapper(new Handler());

        // TODO get references to views

        clientIdText = (TextView) findViewById(R.id.client_id_text);
        clientIdText.setText(Settings.getClientId(this).toString());

        userNameText = (EditText) findViewById(R.id.chat_name_text);

        serverUriText = (EditText) findViewById(R.id.server_uri_text);

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);

    }

	public void onResume() {
        super.onResume();
        registerResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        registerResultReceiver.setReceiver(null);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * Callback for the REGISTER button.
     */
    public void onClick(View v) {
        if (helper != null) {

            String userName;

            String serverUri;

            // TODO get server URI and userName from UI, and use helper to register
            // TODO set registered in settings upon completion

            userName = userNameText.getText().toString();

            serverUri = serverUriText.getText().toString();

            helper.register(userName, registerResultReceiver) ;

            Settings.saveChatName(this, userName);

            Settings.saveServerUri(this, serverUri);

            // End todo

            Log.i(TAG, "Registered: " + userName);

            //finish();

        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case RESULT_OK:
                Settings.setRegistered(this, true);
                // TODO show a success toast message
                Toast.makeText(getApplicationContext(), "Register successfully", Toast.LENGTH_LONG).show() ;
                userNameText.setText("") ;
                Intent toChatIntent = new Intent(this, ChatroomActivity.class) ;
                startActivity(toChatIntent) ;
                break;
            default:
                // TODO show a failure toast message
                Toast.makeText(getApplicationContext(), "Register unsuccessfully", Toast.LENGTH_LONG).show() ;
                userNameText.setText("") ;
                break;
        }
    }

}