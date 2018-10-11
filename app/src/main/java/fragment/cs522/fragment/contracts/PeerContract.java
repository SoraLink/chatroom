package fragment.cs522.fragment.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.Date;

import fragment.cs522.fragment.util.DateUtils;


public class PeerContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Peer");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    // TODO define column names, getters for cursors, setters for contentvalues

    public static final String ID = "_id" ;

    public static final String NAME = "name" ;

    public static final String TIMESTAMP = "timestamp" ;

    public static final String LONGITUDE = "longitude" ;

    public static final String LATITUDE = "latitude" ;


    /********************************* ID Column ***************************************/
    private static int idColumn = -1 ;

    public static long getId(Cursor cursor){
        if(idColumn < 0){
            idColumn = cursor.getColumnIndexOrThrow(ID) ;
        }
        return cursor.getLong(idColumn) ;
    }
    public static void putId(ContentValues out, String id){
        out.put(ID, id) ;
    }

    /********************************** Name Column ************************************/
    private static int nameColumn = -1 ;

    public static String getName(Cursor cursor){
        if(nameColumn < 0){
            nameColumn = cursor.getColumnIndexOrThrow(NAME) ;
        }
        return cursor.getString(nameColumn) ;
    }


    public static void putName(ContentValues out, String name){
        out.put(NAME, name) ;
    }

    /******************************* Timestamp Column ****************************************/
    private static int timestampColumn = -1 ;

    public static String getTimestamp(Cursor cursor){
        if(timestampColumn < 0){
            timestampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP) ;
        }
        return cursor.getString(nameColumn) ;
    }

    public static void putTimestamp(ContentValues out, Date timestamp){
        DateUtils.putDate(out, PeerContract.TIMESTAMP, timestamp);
    }

    /***************************** Longitude Column *****************************************/
    private static int longitudeColumn = -1 ;

    public static double getLongitude(Cursor cursor){
        if(longitudeColumn < 0){
            longitudeColumn = cursor.getColumnIndexOrThrow(LONGITUDE) ;
        }
        return cursor.getDouble(longitudeColumn) ;
    }

    public static void putLongitude(ContentValues out, double longitude){
        out.put(LONGITUDE, longitude) ;
    }

    /**************************** Latitude Column ***************************************/
    private static int latitudeColumn = -1 ;

    public static double getLatitude(Cursor cursor){
        if(latitudeColumn < 0){
            latitudeColumn = cursor.getColumnIndexOrThrow(LATITUDE) ;
        }
        return cursor.getDouble(latitudeColumn) ;
    }

    public static void putLatitude(ContentValues out, double latitude){
        out.put(LATITUDE, latitude) ;
    }

}
