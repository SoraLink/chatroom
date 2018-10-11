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
import fragment.cs522.fragment.contracts.MessageContract;
import fragment.cs522.fragment.entities.ChatMessage;

import static fragment.cs522.fragment.contracts.BaseContract.getId;


public class MessageManager extends Manager<ChatMessage> {

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<ChatMessage> creator = new IEntityCreator<ChatMessage>() {
        @Override
        public ChatMessage create(Cursor cursor) {
            return new ChatMessage(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public void getAllMessagesAsync(QueryBuilder.IQueryListener<ChatMessage> listener) {
        // TODO use QueryBuilder to complete this
        QueryBuilder qb = new QueryBuilder(tag, (Activity)context, MessageContract.CONTENT_URI,LOADER_ID,
                creator, listener) ;
        qb.executeQuery(tag, (Activity)context, MessageContract.CONTENT_URI, LOADER_ID, creator, listener) ;
    }

    public void persistAsync(final ChatMessage message, final IContinue<Uri> callback) {
        // TODO
        ContentValues contentValues = new ContentValues() ;
        message.writeToProvider(contentValues) ;
        contentResolver.insertAsync(MessageContract.CONTENT_URI, contentValues, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri value) {
                message.id = getId(value) ;
                getSyncResolver().notifyChange(value, null) ;
                if(callback != null){
                    callback.kontinue(value) ;
                }
            }
        }) ;
    }

    public void query(final IContinue<TypedCursor> callback){
        AsyncContentResolver asyncContentResolver = getAsyncResolver() ;
        asyncContentResolver.queryAsync(MessageContract.CONTENT_URI, null, null, null, null, new IContinue<Cursor>() {
            @Override
            public void kontinue(Cursor value) {
                if(callback != null){
                    TypedCursor typedCursor = new TypedCursor(value, creator) ;
                    callback.kontinue(typedCursor);
                }
            }
        });
    }

    public void executeQuery(Uri uri, QueryBuilder.IQueryListener<ChatMessage> listener){
        super.executeQuery(uri, listener) ;
    }

}
