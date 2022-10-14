/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;

import static ti.parselivequery.Util.AppContext;
import static ti.parselivequery.Util.checkExceptionForResult;
import static ti.parselivequery.Util.createCompoundQueryOr;
import static ti.parselivequery.Util.fireCallbackForParseUser;
import static ti.parselivequery.Util.getParseObjectList;
import static ti.parselivequery.Util.log;


@SuppressWarnings({"unused", "SpellCheckingInspection"})
@Kroll.module(name = "TiParselivequery", id = "ti.parselivequery")
public class TiParselivequeryModule extends KrollModule {
    private Parse.Configuration parseConfig;

    @Kroll.constant
    public final static int LOG_LEVEL_DEBUG = Parse.LOG_LEVEL_DEBUG;
    @Kroll.constant
    public final static int LOG_LEVEL_ERROR = Parse.LOG_LEVEL_ERROR;
    @Kroll.constant
    public final static int LOG_LEVEL_INFO = Parse.LOG_LEVEL_INFO;
    @Kroll.constant
    public final static int LOG_LEVEL_VERBOSE = Parse.LOG_LEVEL_VERBOSE;
    @Kroll.constant
    public final static int LOG_LEVEL_WARNING = Parse.LOG_LEVEL_WARNING;
    @Kroll.constant
    public final static int LOG_LEVEL_NONE = Parse.LOG_LEVEL_NONE;

    @Kroll.constant
    public final static String IGNORE_CACHE = "IGNORE_CACHE";                // ParseQuery.CachePolicy.IGNORE_CACHE
    @Kroll.constant
    public final static String CACHE_ONLY = "CACHE_ONLY";                    // ParseQuery.CachePolicy.CACHE_ONLY
    @Kroll.constant
    public final static String NETWORK_ONLY = "NETWORK_ONLY";                // ParseQuery.CachePolicy.NETWORK_ONLY
    @Kroll.constant
    public final static String CACHE_ELSE_NETWORK = "CACHE_ELSE_NETWORK";    // ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
    @Kroll.constant
    public final static String NETWORK_ELSE_CACHE = "NETWORK_ELSE_CACHE";    // ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
    @Kroll.constant
    public final static String CACHE_THEN_NETWORK = "CACHE_THEN_NETWORK";    // ParseQuery.CachePolicy.CACHE_THEN_NETWORK

    @Kroll.constant
    public final static String EVENT_TYPE_ENTERED = "CREATE";    // Event.CREATE
    @Kroll.constant
    public final static String EVENT_TYPE_LEFT = "ENTER";        // Event.ENTER
    @Kroll.constant
    public final static String EVENT_TYPE_CREATED = "UPDATE";    // Event.UPDATE
    @Kroll.constant
    public final static String EVENT_TYPE_UPDATED = "LEAVE";     // Event.LEAVE
    @Kroll.constant
    public final static String EVENT_TYPE_DELETED = "DELETE";    // Event.DELETE

    private void onParseObjectResult(ParseException exc, KrollFunction callback) {
        if (callback != null) {
            callback.callAsync(krollObject, checkExceptionForResult(exc));
        }
    }

    @Kroll.method
    public boolean initialize(KrollDict options) {
        if (parseConfig == null) {
            Parse.Configuration.Builder parseBuilder = new Parse.Configuration.Builder(AppContext());
            parseBuilder.applicationId(options.getString(Constant.PROPERTY_APP_ID));
            parseBuilder.clientKey(options.getString(Constant.PROPERTY_CLIENT_KEY));
            parseBuilder.server(options.getString(Constant.PROPERTY_SERVER));

            if (options.optBoolean(Constant.PROPERTY_ENABLE_LOCAL_STORE, Constant.PROPERTY_ENABLE_LOCAL_STORE_DEFAULT)) {
                parseBuilder.enableLocalDataStore();
            }

            parseConfig = parseBuilder.build();
            log("Parse initialized");
        } else {
            log("Parse is already initialized");
        }

        Parse.initialize(parseConfig);
        return true;
    }

    @Kroll.method
    public void destroyParse() {
        Parse.destroy();
    }

    @Kroll.method
    public void setLogLevel(int logLevel) {
        Parse.setLogLevel(logLevel);
    }

    @Kroll.method
    public void setServer(String url) {
        Parse.setServer(url);
    }

    @Kroll.method
    public void fetchAllInBackground(Object values, @Kroll.argument(optional = true) KrollFunction callback) {
        ParseObject.fetchAllInBackground(getParseObjectList(values), (objects, e) -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void pinAllInBackground(Object values, @Kroll.argument(optional = true) KrollFunction callback) {
        ParseObject.pinAllInBackground(getParseObjectList(values), e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void pinAllInBackground(String key, Object values, @Kroll.argument(optional = true) KrollFunction callback) {
        ParseObject.pinAllInBackground(key, getParseObjectList(values), e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void saveAllInBackground(Object values, @Kroll.argument(optional = true) KrollFunction callback) {
        ParseObject.saveAllInBackground(getParseObjectList(values), e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void unpinAllInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        ParseObject.unpinAllInBackground(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void unpinAllInBackground(Object values, @Kroll.argument(optional = true) KrollFunction callback) {
        ParseObject.unpinAllInBackground(getParseObjectList(values), e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void unpinAllInBackground(String key, @Kroll.argument(optional = true) KrollFunction callback) {
        ParseObject.unpinAllInBackground(key, e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void unpinAllInBackground(String key, Object values, @Kroll.argument(optional = true) KrollFunction callback) {
        ParseObject.unpinAllInBackground(key, getParseObjectList(values), e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public QueryProxy createCompoundQuery(Object values) {
        return createCompoundQueryOr(values);
    }

    @Kroll.method
    public ParseUserProxy getCurrentUser() {
        return Util.parseUserProxy(ParseUser.getCurrentUser());
    }

    @Kroll.method
    public void enableAutomaticUser() {
        ParseUser.enableAutomaticUser();
    }

    @Kroll.method
    public boolean isLinked(@Kroll.argument (optional = true) ParseUserProxy parseUserProxy) {
        if (parseUserProxy == null) {
            return ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser());
        } else {
            return ParseAnonymousUtils.isLinked(parseUserProxy.getMyParseUser());
        }
    }

    @Kroll.method
    public void loginInBackground(String username, String password, KrollFunction callback) {
        ParseUser.logInInBackground(username, password, (user, exc) -> fireCallbackForParseUser(user, krollObject, exc, callback));
    }

    @Kroll.method
    public void logOutInBackground(KrollFunction callback) {
        ParseUser.logOutInBackground(exc -> fireCallbackForParseUser(null, krollObject, exc, callback));
    }

    @Kroll.method
    public void anonymousLogIn(KrollFunction callback) {
        ParseAnonymousUtils.logIn((user, exc) -> fireCallbackForParseUser(user, krollObject, exc, callback));
    }

    @Kroll.method
    public void becomeInBackground(String sessionToken, KrollFunction callback) {
        ParseUser.becomeInBackground(sessionToken, (user, exc) -> fireCallbackForParseUser(user, krollObject, exc, callback));
    }
}
