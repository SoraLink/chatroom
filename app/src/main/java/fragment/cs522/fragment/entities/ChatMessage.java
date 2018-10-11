package fragment.cs522.fragment.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import fragment.cs522.fragment.contracts.MessageContract;
import fragment.cs522.fragment.util.DateUtils;


public class ChatMessage implements Parcelable {

    // Primary key in the database
    public long id;

    // Global id provided by the server
    public long seqNum;

    public String messageText;

    public String chatRoom;

    // When and where the message was sent
    public Date timestamp;

    public double longitude;

    public double latitude;

    // Sender username and FK (in local database)
    public String sender;

    public ChatMessage() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public ChatMessage(Cursor cursor) {
        // TODO
        id = MessageContract.getId(cursor) ;
        seqNum = MessageContract.getSequenceNumber(cursor) ;
        messageText = MessageContract.getMessageText(cursor) ;
        chatRoom = MessageContract.getChatRoom(cursor) ;
        timestamp = DateUtils.getDate(cursor, 4) ;
        longitude = MessageContract.getLongitude(cursor) ;
        latitude = MessageContract.getLatitude(cursor) ;
        sender = MessageContract.getSender(cursor) ;
    }

    public ChatMessage(Parcel in) {
        id = in.readLong();
        seqNum = in.readLong();
        messageText = in.readString();
        chatRoom = in.readString();
        timestamp = DateUtils.readDate(in) ;
        longitude = in.readDouble() ;
        latitude = in.readDouble() ;
        sender = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(seqNum);
        dest.writeString(messageText);
        dest.writeString(chatRoom);
        DateUtils.writeDate(dest, timestamp) ;
        dest.writeDouble(longitude) ;
        dest.writeDouble(latitude) ;
        dest.writeString(sender);
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    public void writeToProvider(ContentValues values) {
        // TODO
        MessageContract.putSequenceNumberColumn(values, seqNum) ;
        MessageContract.putMessageText(values, messageText) ;
        MessageContract.putChatRoom(values, chatRoom);
        DateUtils.putDate(values, MessageContract.TIMESTAMP, timestamp) ;
        MessageContract.putLongitude(values, longitude) ;
        MessageContract.putLatitude(values, latitude) ;
        MessageContract.putSender(values, sender) ;
        //MessageContract.putForeignKey(values, sender) ;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
