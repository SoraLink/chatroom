package fragment.cs522.fragment.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import fragment.cs522.fragment.contracts.PeerContract;
import fragment.cs522.fragment.util.DateUtils;


public class Peer implements Parcelable {

    public long id;
    // Use as PK
    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public double longitude;

    public double latitude;

    public Peer() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public Peer(Cursor cursor) {
        // TODO
        id = PeerContract.getId(cursor) ;
        name = PeerContract.getName(cursor) ;
        timestamp = DateUtils.getDate(cursor, 2) ;
        longitude = PeerContract.getLongitude(cursor) ;
        latitude = PeerContract.getLatitude(cursor) ;
    }

    protected Peer(Parcel in) {
        id = in.readLong() ;
        name = in.readString();
        timestamp = DateUtils.readDate(in) ;
        longitude = in.readDouble() ;
        latitude = in.readDouble() ;
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {
        @Override
        public Peer createFromParcel(Parcel in) {
            return new Peer(in);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        dest.writeLong(id) ;

        dest.writeString(name);

        DateUtils.writeDate(dest, timestamp) ;

        dest.writeDouble(longitude) ;

        dest.writeDouble(latitude) ;
    }

    public void writeToProvider(ContentValues values){
        PeerContract.putName(values, name) ;

        DateUtils.putDate(values, PeerContract.TIMESTAMP, timestamp) ;

        PeerContract.putLongitude(values, longitude) ;

        PeerContract.putLatitude(values, latitude) ;
    }
}
