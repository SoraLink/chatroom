package fragment.cs522.fragment.rest;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.net.HttpURLConnection;


public class SynchronizeResponse extends Response {

    public static final String ID_LABEL = "id";

    public SynchronizeResponse(HttpURLConnection connection) throws IOException {
        super(connection);
    }

    @Override
    public boolean isValid() { return true; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        super.writeToParcel(dest, flags) ;
    }

    public SynchronizeResponse(Parcel in) {
        super(in);
        // TODO
    }

    public static Parcelable.Creator<SynchronizeResponse> CREATOR = new Parcelable.Creator<SynchronizeResponse>() {
        @Override
        public SynchronizeResponse createFromParcel(Parcel source) {
            return new SynchronizeResponse(source);
        }

        @Override
        public SynchronizeResponse[] newArray(int size) {
            return new SynchronizeResponse[size];
        }
    };
}
