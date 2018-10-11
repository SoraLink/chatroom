package fragment.cs522.fragment.managers;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import fragment.cs522.fragment.async.AsyncContentResolver;
import fragment.cs522.fragment.async.IContinue;
import fragment.cs522.fragment.async.IEntityCreator;
import fragment.cs522.fragment.async.QueryBuilder;
import fragment.cs522.fragment.contracts.PeerContract;
import fragment.cs522.fragment.entities.Peer;

import static fragment.cs522.fragment.contracts.BaseContract.getId;


public class PeerManager extends Manager<Peer> {

    private static final int LOADER_ID = 2;

    private static final IEntityCreator<Peer> creator = new IEntityCreator<Peer>() {
        @Override
        public Peer create(Cursor cursor) {
            return new Peer(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public PeerManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public void getAllPeersAsync(QueryBuilder.IQueryListener<Peer> listener) {
        // TODO use QueryBuilder to complete this
        QueryBuilder qb = new QueryBuilder(tag, (Activity)context, PeerContract.CONTENT_URI, LOADER_ID,
                creator, listener) ;
        qb.executeQuery(tag, (Activity)context, PeerContract.CONTENT_URI, LOADER_ID, creator, listener) ;
    }

    public void getPeerAsync(long id, final IContinue<Peer> callback) {
        // TODO need to check that peer is not null (not in database)
        AsyncContentResolver asyncContentResolver = getAsyncResolver() ;
        asyncContentResolver.queryAsync(PeerContract.CONTENT_URI, null, PeerContract.ID + " = " + id, null, null,
                new IContinue<Cursor>() {
                    @Override
                    public void kontinue(Cursor value) {
                        if(callback != null){
                            callback.kontinue(new TypedCursor<Peer>(value, creator).getEntity()) ;
                        }
                    }
                });
    }

    public void persistAsync(final Peer peer, final IContinue<Uri> callback) {
        // TODO need to ensure the peer is not already in the database
        final ContentValues contentValues = new ContentValues() ;
        peer.writeToProvider(contentValues) ;
        contentResolver.insertAsync(PeerContract.CONTENT_URI, contentValues, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri value) {
                peer.id = getId(value) ;
                getSyncResolver().notifyChange(value, null);
                if(callback != null){
                    callback.kontinue(value) ;
                }
            }
        });
    }

}