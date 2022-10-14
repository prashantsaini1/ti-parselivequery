/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

public class Constant {
    // events related to `ParseLiveQueryClient`
    final static String EVENT_CLIENT_CONNECTED = "clientConnected";
    final static String EVENT_CLIENT_DISCONNECTED = "clientDisconnected";
    final static String EVENT_CLIENT_SOCKET_ERROR = "clientSocketError";
    final static String EVENT_CLIENT_ERROR = "clientError";


    // events related to `createQuery`
    final static String EVENT_QUERY_ERROR = "error";
    final static String EVENT_QUERY_SUBSCRIBE = "subscribe";
    final static String EVENT_QUERY_UNSUBSCRIBE = "unsubscribe";
    final static String EVENT_QUERY_EVENT = "event";


    // properties related to module
    final static String PROPERTY_APP_ID = "appId";
    final static String PROPERTY_CLIENT_KEY = "clientKey";
    final static String PROPERTY_SERVER = "server";
    final static String PROPERTY_ENABLE_LOCAL_STORE = "enableLocalStore";
    final static boolean PROPERTY_ENABLE_LOCAL_STORE_DEFAULT = false;

    // properties related to all events
    final static String PROPERTY_CLASS_VALUE = "value";
    final static String PROPERTY_CLASS_MSG = "message";
    final static String PROPERTY_ERROR_CODE = "code";


    // properties related to `createQuery`
    final static String PROPERTY_CLASS_NAME = "className";
    final static String PROPERTY_SUCCESS = "success";
    final static String PROPERTY_PARSE_OBJECT = "parseObject";
    final static String PROPERTY_COUNT = "count";
    final static String PROPERTY_PARSE_OBJECTS = "parseObjects";


    // event properties related to `createQuery`
    final static String PROPERTY_EVENT_TYPE = "parseEventType";

/* -------- v1.1.0 ---------- */
    // properties related to `createParseFile`
    final static String PROPERTY_FILE_NAME = "fileName";
    final static String PROPERTY_FILE_DATA = "fileData";    // type TiBlob in Java, Ti.Blob in JS
    final static String PROPERTY_FILE_PROGRESS = "fileProgress";


    // properties related to `createParseGeoPoint`
    final static String PROPERTY_LATITUDE = "latitude";
    final static String PROPERTY_LONGITUDE = "longitude";


    // properties related to `createParsePolygon`
    final static String PROPERTY_PARSE_GEOPOINT_LIST = "parseGeoPointList";

    // properties related to `createParseUser`
    final static String PROPERTY_PARSE_USER = "parseUser";
    final static String PROPERTY_PARSE_USERS = "parseUsers";
}
