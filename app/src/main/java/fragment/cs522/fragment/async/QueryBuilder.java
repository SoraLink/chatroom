package fragment.cs522.fragment.async;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import fragment.cs522.fragment.contracts.ChatroomContract;
import fragment.cs522.fragment.contracts.MessageContract;
import fragment.cs522.fragment.contracts.PeerContract;
import fragment.cs522.fragment.managers.TypedCursor;


public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks {

    private static final int MESSAGE_LOADER_ID = 1 ;

    private static final int PEER_LOADER_ID = 2 ;

    public static final int CHATROOM_LOADER_ID = 3;

    private IEntityCreator<T> creator ;

    private  IQueryListener<T> listener ;

    private String tag ;

    private Context context ;

    private Uri uri ;

    private int loaderID ;

    public static interface IQueryListener<T> {

        public void handleResults(TypedCursor<T> results);

        public void closeResults();

    }

    //Constructor for QueryBuilder
    public QueryBuilder(String tag, Context context, Uri uri, int loaderID, IEntityCreator<T> creator,
                        IQueryListener<T> listener){
        this.tag = tag ;
        this.context = context ;
        this.uri = uri ;
        this.loaderID = loaderID ;
        this.creator = creator ;
        this.listener = listener ;
    }

    public static <T> void executeQuery(String tag, Activity context, Uri uri, int loaderID,
                                        IEntityCreator<T> creator, IQueryListener<T> listener){
        QueryBuilder<T> qb = new QueryBuilder<>(tag, context, uri, loaderID, creator, listener) ;
        LoaderManager lm = context.getLoaderManager() ;
        lm.initLoader(loaderID, null, qb) ;
    }

    public static <T> void executeQuery(String tag, Activity context, Uri uri, int loaderID,
                                        String[] projection, String selection, String[] selectionArgs,
                                        IEntityCreator<T> creator, IQueryListener<T> listener){

    }

    public static <T> void reexecuteQuery(String tag, Activity context, Uri uri, int loaderID,
                                          String[] projection, String selection, String[] selectionArgs,
                                          IEntityCreator<T> creator, IQueryListener<T> listener){

    }

    // TODO complete the implementation of this

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if(id == MESSAGE_LOADER_ID){
            String[] projection = new String[]{MessageContract.ID, MessageContract.SEQUENCE_NUMBER,
                    MessageContract.MESSAGE_TEXT, MessageContract.CHAT_ROOM, MessageContract.TIMESTAMP,
                    MessageContract.LONGITUDE, MessageContract.LATITUDE, MessageContract.SENDER} ;
            return new CursorLoader(context, uri, projection, null, null, null) ;
        }else if(id == PEER_LOADER_ID){
            String[] projection = new String[]{PeerContract.ID, PeerContract.NAME,
                    PeerContract.TIMESTAMP,/*,PeerContract.ADDRESS, PeerContract.PORT*/
                    PeerContract.LONGITUDE, PeerContract.LATITUDE} ;
            return new CursorLoader(context, uri, projection, null, null, null) ;
        }else if(id == CHATROOM_LOADER_ID){
            String[] projection = new String[]{ChatroomContract.ID, ChatroomContract.NAME} ;
            return new CursorLoader(context, uri, projection, null, null, null) ;
        }
        else{
            return null ;
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if(loader.getId() == loaderID){
            listener.handleResults(new TypedCursor<T>((Cursor)data, creator));
        }else{
            throw new IllegalStateException("Unexpected loader callback") ;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if(loader.getId() == loaderID){
            listener.closeResults() ;
        }else{
            throw new IllegalStateException("Unexpected loader callback") ;
        }
    }
}
