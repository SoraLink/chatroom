package fragment.cs522.fragment.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by dduggan.
 */

public class ResultReceiverWrapper extends ResultReceiver {

    public ResultReceiverWrapper(Handler handler) {
        super(handler);
    }

    public interface IReceive {
        public void onReceiveResult(int resultCode, Bundle data);
    }

    protected IReceive receiver;

    public void setReceiver(IReceive receiver) {
        this.receiver = receiver;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, data);
        }
    }
}