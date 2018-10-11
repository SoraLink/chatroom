package fragment.cs522.fragment.rest;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

import static android.app.Activity.RESULT_OK;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RequestService extends IntentService {

    public static final String SERVICE_REQUEST_KEY = "fragment.cs522.fragment.extra.REQUEST";

    public static final String RESULT_RECEIVER_KEY = "fragment.cs522.fragment.rest.extra.RECEIVER";

    private RequestProcessor processor;

    public RequestService() {
        super("RequestService");
    }

    @Override
    public void onCreate() {

        super.onCreate() ;

        processor = new RequestProcessor(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Request request = intent.getParcelableExtra(SERVICE_REQUEST_KEY);

        ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER_KEY);

        if(request != null){
            Response response = processor.process(request);
        }

        if (receiver != null) {
            // TODO UI should display a toast message on completion of the operation
            receiver.send(RESULT_OK, null) ;
        }
    }

}
