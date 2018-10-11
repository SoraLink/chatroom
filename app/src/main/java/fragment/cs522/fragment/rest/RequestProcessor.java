package fragment.cs522.fragment.rest;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fragment.cs522.fragment.entities.ChatMessage;
import fragment.cs522.fragment.entities.Peer;
import fragment.cs522.fragment.managers.RequestManager;
import fragment.cs522.fragment.managers.TypedCursor;
import fragment.cs522.fragment.util.StringUtils;


public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    private RequestManager requestManager;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod =  new RestMethod(context);
        this.requestManager = new RequestManager(context);
    }

    public Response process(Request request) {
        return request.process(this);
    }

    public Response perform(RegisterRequest request) {
        return restMethod.perform(request);
    }

    public Response perform(PostMessageRequest request) {
        // We will just insert the message into the database, and rely on background sync to upload
        // return restMethod.perform(request)
        requestManager.persist(request.message);
        return request.getDummyResponse();
    }

    public Response perform(SynchronizeRequest request) {
        RestMethod.StreamingResponse response = null;
        final TypedCursor<ChatMessage> messages = requestManager.getUnsentMessages();
        messages.moveToFirst() ;
        final int numMessagesReplaced = messages.getCount();
        try {
            RestMethod.StreamingOutput out = new RestMethod.StreamingOutput() {
                @Override
                public void write(final OutputStream os) throws IOException {
                    try {
                        JsonWriter wr = new JsonWriter(new OutputStreamWriter(new BufferedOutputStream(os)));
                        wr.beginArray();
                        /*
                         * TODO stream unread messages to the server:
                         * {
                         *   chatroom : ...,
                         *   timestamp : ...,
                         *   latitude : ...,
                         *   longitude : ....,
                         *   text : ...
                         * }
                         */
                        for(int i=0; i<numMessagesReplaced; i++){
                            ChatMessage replacedMessage = messages.getEntity() ;
                            wr.beginObject() ;
                            wr.name("chatroom") ;
                            wr.value(replacedMessage.chatRoom) ;
                            wr.name("timestamp") ;
                            wr.value(replacedMessage.timestamp.getTime()) ;
                            wr.name("latitude") ;
                            wr.value(replacedMessage.latitude) ;
                            wr.name("longitude") ;
                            wr.value(replacedMessage.longitude) ;
                            wr.name("text") ;
                            wr.value(replacedMessage.messageText) ;
                            wr.endObject() ;
                            messages.moveToNext() ;
                        }

                        wr.endArray();
                        wr.flush();
                    } finally {
                        messages.close();
                    }
                }
            };
            response = restMethod.perform(request, out);

            JsonReader rd = new JsonReader(new InputStreamReader(new BufferedInputStream(response.getInputStream()), StringUtils.CHARSET));
            // TODO parse data from server (messages and peers) and update database
            // See RequestManager for operations to help with this.
            /************************* read peers *********************************************/
            List<Peer> peerList = new ArrayList<>() ;
            rd.beginObject() ;
            String firstTypeName = rd.nextName() ;
            if(firstTypeName.equals("clients")){
                rd.beginArray() ;

                while(rd.hasNext()) {
                    rd.beginObject();
                    Peer newPeer = new Peer();

                    while (rd.hasNext()) {
                        String attribute = rd.nextName();
                        switch (attribute) {

                            case "username":
                                newPeer.name = rd.nextString();
                                break;

                            case "timestamp":
                                newPeer.timestamp = new Date(rd.nextLong());
                                break;

                            case "longitude":
                                newPeer.longitude = rd.nextDouble();
                                break;

                            case "latitude":
                                newPeer.latitude = rd.nextDouble();
                                break;

                        }
                    }
                    rd.endObject();

                    boolean exist = false;
                    for (int i = 0; i < peerList.size(); i++) {
                        if (newPeer.name.equals(peerList.get(i).name)) {
                            if (peerList.get(i).timestamp.after(newPeer.timestamp)) {
                                newPeer.timestamp = peerList.get(i).timestamp;
                                exist = true;
                            }
                        }
                    }

                    if (!exist) {
                        peerList.add(newPeer);
                    }
                }
            }
            rd.endArray();

            /**************************** Read Messages ********************************************/
            List<ChatMessage> messageList = new ArrayList<>() ;
            String secondTypeName = rd.nextName() ;
            rd.beginArray() ;
            if(secondTypeName.equals("messages")){
                while(rd.hasNext()){
                    rd.beginObject() ;
                    ChatMessage chatMessage = new ChatMessage() ;
                    while (rd.hasNext()){
                        String attribute = rd.nextName() ;
                        switch (attribute){

                            case "chatroom": chatMessage.chatRoom = rd.nextString() ; break ;

                            case "timestamp": chatMessage.timestamp = new Date(rd.nextLong()) ; break ;

                            case "longitude": chatMessage.longitude = rd.nextDouble() ; break ;

                            case "latitude": chatMessage.latitude = rd.nextDouble() ; break ;

                            case "seqnum": chatMessage.seqNum = rd.nextLong() ; break ;

                            case "sender": chatMessage.sender = rd.nextString() ; break ;

                            case "text": chatMessage.messageText = rd.nextString(); break ;
                        }
                    }
                    rd.endObject() ;
                    messageList.add(chatMessage) ;
                }
            }
            rd.endArray();
            rd.endObject() ;

            /******************************** delete all peers in database ***************************/
            requestManager.deletePeers() ;

            /********************************* insert messages into database *************************/
            requestManager.syncMessages(messageList.size(), messageList) ;

            /********************************* insert peers into database ****************************/
            for(Peer curPeer: peerList){
                requestManager.persist(curPeer) ;
            }

            return response.getResponse();

        } catch (IOException e) {
            return new ErrorResponse(request.id, e);

        } finally {
            if (response != null) {
                response.disconnect();
            }
        }
    }

}
