package fragment.cs522.fragment.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import fragment.cs522.fragment.R;
import fragment.cs522.fragment.async.QueryBuilder;
import fragment.cs522.fragment.contracts.PeerContract;
import fragment.cs522.fragment.entities.Peer;
import fragment.cs522.fragment.managers.PeerManager;
import fragment.cs522.fragment.managers.TypedCursor;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener, QueryBuilder.IQueryListener<Peer> {

    /*
     * TODO See ChatActivity for example of what to do, query peers database instead of messages database.
     */

    private PeerManager peerManager;

    private SimpleCursorAdapter peerAdapter;

    private ListView listView ;

    private Cursor peerCursor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // TODO initialize peerAdapter with empty cursor (null)

        listView = (ListView)findViewById(R.id.peerList) ;
        peerManager = new PeerManager(this);
        peerManager.getAllPeersAsync(this);

        String[] from = new String[]{PeerContract.NAME} ;
        int[] to = new int[]{R.id.peer_name} ;
        peerAdapter = new SimpleCursorAdapter(this, R.layout.view_peers_row, peerCursor, from, to) ;
        listView.setAdapter(peerAdapter) ;

        listView.setOnItemClickListener(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            Intent intent = new Intent(this, ViewPeerActivity.class);
            Peer peer = new Peer(cursor);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: "+position);
        }
    }

    @Override
    public void handleResults(TypedCursor<Peer> results) {
        // TODO
        peerAdapter.swapCursor(results.getCursor()) ;
    }

    @Override
    public void closeResults() {
        // TODO
        peerAdapter.swapCursor(null) ;
    }
}
