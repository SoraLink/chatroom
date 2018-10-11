package fragment.cs522.fragment.managers;

import android.content.Context;
import android.database.Cursor;

import fragment.cs522.fragment.async.IEntityCreator;
import fragment.cs522.fragment.async.QueryBuilder;
import fragment.cs522.fragment.contracts.ChatroomContract;
import fragment.cs522.fragment.entities.ChatRoom;


public class ChatroomManager extends Manager<ChatRoom> {

    private static final int LOADER_ID = 3;

    private static final IEntityCreator<ChatRoom> creator = new IEntityCreator<ChatRoom>() {
        @Override
        public ChatRoom create(Cursor cursor) {
            return new ChatRoom(cursor);
        }
    };

    public ChatroomManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void getAllChatroomsAsync(QueryBuilder.IQueryListener<ChatRoom> listener) {
        executeQuery(ChatroomContract.CONTENT_URI, listener);
    }


}
