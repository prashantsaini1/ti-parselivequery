/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

import com.parse.livequery.LiveQueryException;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.ParseLiveQueryClientCallbacks;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import static ti.parselivequery.Constant.EVENT_CLIENT_CONNECTED;
import static ti.parselivequery.Constant.EVENT_CLIENT_DISCONNECTED;
import static ti.parselivequery.Constant.EVENT_CLIENT_ERROR;
import static ti.parselivequery.Constant.EVENT_CLIENT_SOCKET_ERROR;
import static ti.parselivequery.Constant.PROPERTY_CLASS_VALUE;

@SuppressWarnings("unused")
@Kroll.proxy(creatableInModule = TiParselivequeryModule.class)
public class ParseClientProxy extends KrollProxy implements ParseLiveQueryClientCallbacks {
    private boolean isConnected = false;
    ParseLiveQueryClient parseLiveQueryClient;

    public ParseClientProxy() {
        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        registerListener();
    }

    @Override
    public void release() {
        super.release();
        destroyClient();
    }

    @Kroll.getProperty
    public boolean getIsConnected() {
        return isConnected;
    }

    @Kroll.method
    public void registerListener() {
        parseLiveQueryClient.registerListener(this);
    }

    @Kroll.method
    public void unregisterListener() {
        parseLiveQueryClient.unregisterListener(this);
    }

    @Kroll.method
    public void reconnectClient() {
        parseLiveQueryClient.reconnect();
    }

    @Kroll.method
    public void reconnectClientIfNeeded() {
        parseLiveQueryClient.connectIfNeeded();
    }

    @Kroll.method
    public void disconnectClient() {
        parseLiveQueryClient.disconnect();
    }

    @Kroll.method
    public void destroyClient() {
        disconnectClient();
        unregisterListener();
        parseLiveQueryClient = null;
    }

    private void fireClientEvent(String eventName, Object value) {
        KrollDict result = new KrollDict();
        result.put(PROPERTY_CLASS_VALUE, value);
        fireEvent(eventName, result);
    }

    @Override
    public void onLiveQueryClientConnected(ParseLiveQueryClient client) {
        isConnected = true;
        fireClientEvent(EVENT_CLIENT_CONNECTED, true);
    }

    @Override
    public void onLiveQueryClientDisconnected(ParseLiveQueryClient client, boolean userInitiated) {
        isConnected = false;
        fireClientEvent(EVENT_CLIENT_DISCONNECTED, false);
    }

    @Override
    public void onLiveQueryError(ParseLiveQueryClient client, LiveQueryException reason) {
        fireClientEvent(EVENT_CLIENT_ERROR, reason.getLocalizedMessage());
    }

    @Override
    public void onSocketError(ParseLiveQueryClient client, Throwable reason) {
        isConnected = false;
        fireClientEvent(EVENT_CLIENT_SOCKET_ERROR, reason.getLocalizedMessage());
    }
}
