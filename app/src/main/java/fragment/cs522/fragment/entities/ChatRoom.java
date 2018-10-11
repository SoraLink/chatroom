package fragment.cs522.fragment.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import fragment.cs522.fragment.contracts.ChatroomContract;


public class ChatRoom implements Parcelable {

    // Primary key in the database
    public long id;

    // Name of the chat room
    public String name;

    public ChatRoom() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public ChatRoom(Cursor cursor) {
        // TODO
        id = ChatroomContract.getId(cursor) ;
        name = ChatroomContract.getName(cursor) ;
    }

    public void writeToProvider(ContentValues values) {
        // TODO
        ChatroomContract.putName(values, name) ;
    }

    protected ChatRoom(Parcel in) {
        id = in.readLong();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatRoom> CREATOR = new Creator<ChatRoom>() {
        @Override
        public ChatRoom createFromParcel(Parcel in) {
            return new ChatRoom(in);
        }

        @Override
        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };

}
