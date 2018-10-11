package fragment.cs522.fragment.managers;

/**
 * Created by lizheng on 2017/2/24.
 */

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import fragment.cs522.fragment.async.AsyncContentResolver;
import fragment.cs522.fragment.async.IEntityCreator;
import fragment.cs522.fragment.async.QueryBuilder;


public abstract class Manager<T> {

    protected final Context context;

    private final IEntityCreator<T> creator;

    protected final int loaderID;

    protected final String tag;

    protected Manager(Context context,
                      IEntityCreator<T> creator,
                      int loaderID) {
        this.context = context;
        this.creator = creator;
        this.loaderID = loaderID;
        this.tag = this.getClass().getCanonicalName();
    }

    private ContentResolver syncResolver;

    private AsyncContentResolver asyncResolver;

    protected ContentResolver getSyncResolver() {
        if (syncResolver == null)
            syncResolver = context.getContentResolver();
        return syncResolver;
    }

    protected AsyncContentResolver getAsyncResolver() {
        if (asyncResolver == null)
            asyncResolver = new AsyncContentResolver(context.getContentResolver());
        return asyncResolver;
    }

    // TODO Provide operations for executing queries (see lectures)

    protected void executeQuery(Uri uri, QueryBuilder.IQueryListener<T> listener){
        QueryBuilder.executeQuery(tag, (Activity) context, uri, loaderID, creator, listener) ;
    }

    protected void executeQuery(Uri uri, String[] projection, String selection,
                                String[] selectionArgs, QueryBuilder.IQueryListener<T> listener){
        QueryBuilder.executeQuery(tag, (Activity) context, uri, loaderID, projection, selection, selectionArgs,
                creator, listener) ;
    }

    protected void reexecuteQuery(Uri uri, String[] projection, String selection,
                                  String[] selectionArgs, QueryBuilder.IQueryListener<T> listener){
        QueryBuilder.reexecuteQuery(tag, (Activity)context, uri, loaderID, projection,
                selection,selectionArgs,creator,listener);
    }

}
