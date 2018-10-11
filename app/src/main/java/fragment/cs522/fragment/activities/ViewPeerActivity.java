package fragment.cs522.fragment.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import fragment.cs522.fragment.R;
import fragment.cs522.fragment.entities.Peer;
import fragment.cs522.fragment.managers.PeerManager;


/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_KEY = "peer";

    private PeerManager peerManager ;

    private TextView peerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        // TODO init the UI
        peerView = (TextView) findViewById(R.id.view_user_name) ;
        peerView.setText(peer.name) ;

        peerView = (TextView)findViewById(R.id.view_timestamp) ;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss") ;
        peerView.setText(format.format(peer.timestamp)) ;

        peerView = (TextView)findViewById(R.id.view_longitude) ;
        peerView.setText(String.valueOf(peer.longitude)) ;

        peerView = (TextView)findViewById(R.id.view_latitude) ;
        peerView.setText(String.valueOf(peer.latitude));

    }

}
