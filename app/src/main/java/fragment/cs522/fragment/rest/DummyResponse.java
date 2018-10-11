package fragment.cs522.fragment.rest;

import android.os.Parcel;
import android.os.Parcelable;

import fragment.cs522.fragment.util.EnumUtils;


public class DummyResponse extends Response implements Parcelable {

    public boolean isValid() {
        return true;
    }

    public DummyResponse(long id) {
        super(id, "", 200, "OK");
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        EnumUtils.writeEnum(out, ResponseType.DUMMY);
        super.writeToParcel(out, flags);
    }

    public DummyResponse(Parcel in) {
        super(in);
    }

    public static final Creator<DummyResponse> CREATOR = new Creator<DummyResponse>() {
        public DummyResponse createFromParcel(Parcel in) {
            EnumUtils.readEnum(ResponseType.class, in);
            return new DummyResponse(in);
        }

        public DummyResponse[] newArray(int size) {
            return new DummyResponse[size];
        }
    };

}

