package fragment.cs522.fragment.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.Date;

import fragment.cs522.fragment.contracts.BaseContract;
import fragment.cs522.fragment.contracts.ChatroomContract;
import fragment.cs522.fragment.contracts.MessageContract;
import fragment.cs522.fragment.contracts.PeerContract;
import fragment.cs522.fragment.entities.Peer;
import fragment.cs522.fragment.util.DateUtils;


public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String MESSAGE_CONTENT_PATH_SYNC = MessageContract.CONTENT_PATH_SYNC;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;

    private static final String CHATROOM_CONTENT_PATH = ChatroomContract.CONTENT_PATH;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 1;

    private static final String CHATROOMS_TABLE = "chatrooms";

    private static final String MESSAGES_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";

    private static final String FOREIGNKEY = "peer_fk" ;

    private static final String CHATROOM_NAME_INDEX = "chatroom_name_index";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int MESSAGES_SYNC = 3;
    private static final int PEERS_ALL_ROWS = 4;
    private static final int PEERS_SINGLE_ROW = 5;
    private static final int CHATROOMS_ALL_ROWS = 6;

    public static class DbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE = "create table if not exists " ;

        private static final String INDEX_CREATE = "create index if not exists " ;

        //sql statement for message table
        private static final String CREATE_MESSAGE = DATABASE_CREATE + MESSAGES_TABLE  + "("
                + MessageContract.ID + " Integer Primary Key, "
                + MessageContract.SEQUENCE_NUMBER + " INTEGER, "
                + MessageContract.MESSAGE_TEXT + " Text Not Null, "
                + MessageContract.CHAT_ROOM + " Text Not Null, "
                + MessageContract.TIMESTAMP + " Date, "
                + MessageContract.LATITUDE + " Float, "
                + MessageContract.LONGITUDE + " Float, "
                + MessageContract.SENDER + " INTEGER, "
                + "Foreign Key ("+MessageContract.CHAT_ROOM+") References Chatrooms(name) On Delete Cascade, "
                + "Foreign Key(" + MessageContract.SENDER + ") References " + PEERS_TABLE + "(" + PeerContract.NAME + ") " +
                "On Delete Cascade);" ;

        //sql statement for peer table
        private static final String CREATE_PEER = DATABASE_CREATE + PEERS_TABLE + "("
                + PeerContract.ID + " Integer Primary Key, "
                + PeerContract.NAME + " Text Not Null, "
                + PeerContract.TIMESTAMP + " Date, "
                + PeerContract.LONGITUDE + " Float, "
                + PeerContract.LATITUDE + " Float);" ;

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + CHATROOMS_TABLE + " ("
                    + ChatroomContract.ID + " INTEGER PRIMARY KEY,"
                    + ChatroomContract.NAME + " TEXT NOT NULL"
                    + ");");
            ContentValues values = new ContentValues();
            values.put(ChatroomContract.NAME, "_default");
            db.insert(CHATROOMS_TABLE, null, values);
            // TODO other chatroom names

            // TODO initialize other database tables
            db.execSQL(CREATE_MESSAGE) ;
            db.execSQL(CREATE_PEER) ;
            db.execSQL("Pragma foreign_key = ON") ;
            db.execSQL(INDEX_CREATE + "MessagesPeerIndex ON " + MESSAGES_TABLE + "(" + MessageContract.SENDER + ");") ;
            db.execSQL(INDEX_CREATE + "PeerNameIndex ON " + PEERS_TABLE + "(" + PeerContract.NAME + ");") ;
            db.execSQL(INDEX_CREATE + CHATROOM_NAME_INDEX + " ON " + MESSAGES_TABLE + "("+MessageContract.CHAT_ROOM+");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO upgrade database if necessary
            Log.w("MDA", "Upgrading from version " + oldVersion + "to" + newVersion ) ;

            //drop the old table and create new ones
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE) ;
            db.execSQL("DROP TABLE IF EXISTS " + PEERS_TABLE) ;
            db.execSQL("DROP TABLE IF EXISTS " + CHATROOMS_TABLE) ;
            //create new ones
            onCreate(db) ;
        }
    }

    private DbHelper dbHelper;

    private ContentResolver contentResolver ;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_SYNC, MESSAGES_SYNC);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, CHATROOM_CONTENT_PATH, CHATROOMS_ALL_ROWS);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)){
            case MESSAGES_ALL_ROWS:
                return MESSAGE_CONTENT_PATH ;

            case MESSAGES_SINGLE_ROW:
                return MESSAGE_CONTENT_PATH_ITEM ;

            case MESSAGES_SYNC:
                return MESSAGE_CONTENT_PATH_SYNC ;

            case PEERS_ALL_ROWS:
                return PEER_CONTENT_PATH ;

            case PEERS_SINGLE_ROW:
                return PEER_CONTENT_PATH_ITEM ;

            case CHATROOMS_ALL_ROWS:
                return CHATROOM_CONTENT_PATH ;
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new message.

                long msgRowId = db.insert(MESSAGES_TABLE, null, values) ;

                // Make sure to notify any observers
                if(msgRowId > 0){
                    Uri insertedUri = MessageContract.CONTENT_URI(msgRowId) ;
                    contentResolver = getContext().getContentResolver() ;
                    contentResolver.notifyChange(insertedUri, null) ;
                    return  insertedUri ;
                }
                throw new UnsupportedOperationException("Not yet implemented");
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new peer.

                Object peerName = values.get(PeerContract.NAME) ;
                String[] projection = new String[]{PeerContract.ID, PeerContract.NAME,
                        PeerContract.TIMESTAMP,PeerContract.LONGITUDE, PeerContract.LATITUDE} ;
                Cursor theCursor = db.query(PEERS_TABLE, projection,PeerContract.NAME + " = '" + peerName + "'", null, null, null, null) ;
                if(theCursor.getCount() == 0){
                    long peerRowId = db.insert(PEERS_TABLE, null, values) ;
                    // Make sure to notify any observers
                    if(peerRowId > 0){
                        Uri insertedUriPeer = PeerContract.CONTENT_URI(peerRowId) ;
                        ContentResolver cr = getContext().getContentResolver() ;
                        cr.notifyChange(insertedUriPeer, null);
                        return insertedUriPeer ;
                    }else{
                        throw new UnsupportedOperationException("insert unsuccessfully");
                    }
                }else{
                    theCursor.moveToFirst() ;
                    Peer thePeer = new Peer(theCursor) ;
                    ContentValues cv = new ContentValues() ;
                    long newTime = values.getAsLong(PeerContract.TIMESTAMP) ;
                    DateUtils.putDate(cv, PeerContract.TIMESTAMP, new Date(newTime)) ;
                    db.update(PEERS_TABLE, cv, PeerContract.NAME + " = '" + thePeer.name + "'", null) ;
                    return PeerContract.CONTENT_URI(PeerContract.getId(theCursor)) ;
                }

            case CHATROOMS_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new chatroom.
                // Make sure to notify any observers
                long chatRowId = db.insert(CHATROOMS_TABLE, null, values) ;
                if(chatRowId > 0){
                    Uri chatInsertedUri = MessageContract.CONTENT_URI(chatRowId) ;
                    contentResolver = getContext().getContentResolver() ;
                    contentResolver.notifyChange(chatInsertedUri, null) ;
                    return  chatInsertedUri ;
                }
                throw new UnsupportedOperationException("Not yet implemented");

            case MESSAGES_SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");

            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor theCursor;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle query of all messages.
                contentResolver = getContext().getContentResolver() ;

                theCursor =  db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder) ;

                theCursor.setNotificationUri(contentResolver, uri) ;

                break ;

            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle query of all peers.
                contentResolver = getContext().getContentResolver() ;

                theCursor = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);

                theCursor.setNotificationUri(contentResolver, uri) ;

                break ;

            case CHATROOMS_ALL_ROWS:
                // TODO: Implement this to handle query of all chatrooms.
                contentResolver = getContext().getContentResolver() ;

                theCursor = db.query(CHATROOMS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);

                theCursor.setNotificationUri(contentResolver, uri) ;

                break ;

            case MESSAGES_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific message.
                contentResolver = getContext().getContentResolver() ;

                theCursor =  db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder) ;

                theCursor.setNotificationUri(contentResolver, uri) ;

                break ;


            case PEERS_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific peer.
                contentResolver = getContext().getContentResolver() ;

                theCursor = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);

                theCursor.setNotificationUri(contentResolver, uri) ;

                break ;

            default:
                throw new IllegalStateException("insert: bad case");
        }
        theCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return theCursor ;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // TODO Implement this to handle requests to delete one or more rows.
        switch (uriMatcher.match(uri)){
            case MESSAGES_ALL_ROWS:
                return db.delete(MESSAGES_TABLE, null, null) ;

            case MESSAGES_SINGLE_ROW:
                return db.delete(MESSAGES_TABLE, selection, selectionArgs) ;

            case PEERS_ALL_ROWS:
                db.delete(MESSAGES_TABLE, null, null) ;
                return db.delete(PEERS_TABLE, null, null) ;

            case PEERS_SINGLE_ROW:
                return db.delete(PEERS_TABLE, selection, selectionArgs) ;
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] records) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_SYNC:
                /*
                 * Do all of this in a single transaction.
                 */
                db.beginTransaction();
                try {

                    /*
                     * Delete the first N messages with sequence number = 0, where N = records.length.
                     */
                    int numReplacedMessages = Integer.parseInt(uri.getLastPathSegment());

                    String[] columns = {MessageContract.ID};
                    String selection = MessageContract.SEQUENCE_NUMBER + "=0";
                    Cursor cursor = db.query(MESSAGES_TABLE, columns, selection, null, null, null, MessageContract.TIMESTAMP);
                    try {
                        if (numReplacedMessages > 0 && cursor.moveToFirst()) {
                            do {
                                String deleteSelection = MessageContract.ID + "=" + Long.toString(cursor.getLong(0));
                                db.delete(MESSAGES_TABLE, deleteSelection, null);
                                numReplacedMessages--;
                            } while (numReplacedMessages > 0 && cursor.moveToNext());
                        }
                    } finally {
                        cursor.close();
                    }

                    /*
                     * Insert the messages downloaded from server, which will include replacements for deleted records.
                     */
                    for (ContentValues record : records) {
                        if (db.insert(MESSAGES_TABLE, null, record) < 0) {
                            throw new IllegalStateException("Failure to insert updated chat message record!");
                        };
                    }

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                // TODO Make sure to notify any observers
                getContext().getContentResolver().notifyChange(MessageContract.CONTENT_URI, null);

                break;

            default:
                throw new IllegalStateException("insert: bad case");

        }
        return 0 ;
    }


}
