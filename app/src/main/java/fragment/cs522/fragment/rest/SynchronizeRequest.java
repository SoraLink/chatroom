package fragment.cs522.fragment.rest;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;

import fragment.cs522.fragment.entities.ChatMessage;


public class SynchronizeRequest extends Request {

    public ChatMessage message;

    // Added by request processor
    public long lastSequenceNumber;

    public SynchronizeRequest(ChatMessage message) {
        super();
        this.message = message;
    }

    @Override
    public String getRequestEntity() throws IOException {
        // We stream output for SYNC, so this always returns null
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        assert rd == null;
        return new SynchronizeResponse(connection);
    }

    @Override
    public Response process(RequestProcessor processor) {
        return processor.perform(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        super.writeToParcel(dest, flags) ;
        message.writeToParcel(dest, flags) ;
        message.writeToParcel(dest, flags) ;
    }

    public SynchronizeRequest() {
        super();
    }

    public SynchronizeRequest(Parcel in) {
        super(in);
        // TODO
       message = new ChatMessage(in) ;

    }

    public static Parcelable.Creator<SynchronizeRequest> CREATOR = new Parcelable.Creator<SynchronizeRequest>() {
        @Override
        public SynchronizeRequest createFromParcel(Parcel source) {
            return new SynchronizeRequest(source);
        }

        @Override
        public SynchronizeRequest[] newArray(int size) {
            return new SynchronizeRequest[size];
        }
    };

}
