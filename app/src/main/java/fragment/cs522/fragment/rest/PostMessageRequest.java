package fragment.cs522.fragment.rest;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;

import fragment.cs522.fragment.entities.ChatMessage;

public class PostMessageRequest extends Request {

    public ChatMessage message;

    public PostMessageRequest(ChatMessage message) {
        super();
        this.message = message;
    }

    @Override
    public String getRequestEntity() throws IOException {
        StringWriter wr = new StringWriter();
        JsonWriter jw = new JsonWriter(wr);
        // TODO write a JSON message of the form:
        // { "room" : <chat-room-name>, "message" : <message-text> }
        jw.beginObject();
        jw.name("chatroom");
        jw.value(message.chatRoom);
        jw.name("text");
        jw.value(message.messageText);
        jw.endObject();

        return wr.toString();
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        throw new IllegalStateException("PostMessage request should only return dummy response");
    }

    public Response getDummyResponse() {
        return new DummyResponse(id);
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
        dest.writeParcelable(message, flags) ;
//        message.writeToParcel(dest, flags);
    }

    public PostMessageRequest() {
        super();
    }

    public PostMessageRequest(Parcel in) {
        super(in);
        // TODO
//        message = new ChatMessage(in) ;
        message = in.readParcelable(ChatMessage.class.getClassLoader());
    }

    public static Parcelable.Creator<PostMessageRequest> CREATOR = new Parcelable.Creator<PostMessageRequest>() {
        @Override
        public PostMessageRequest createFromParcel(Parcel source) {
            return new PostMessageRequest(source);
        }

        @Override
        public PostMessageRequest[] newArray(int size) {
            return new PostMessageRequest[size];
        }
    };

}
