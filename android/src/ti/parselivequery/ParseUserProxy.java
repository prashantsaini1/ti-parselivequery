/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePolygon;
import com.parse.ParseUser;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.util.TiConvert;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static ti.parselivequery.ParseDataTypeConverter.toParseDataTypeFrom;
import static ti.parselivequery.ParseDataTypeConverter.toParseDataTypeFromArray;
import static ti.parselivequery.Util.checkExceptionForResult;
import static ti.parselivequery.Util.toDate;

@SuppressWarnings("unused")
@Kroll.proxy(creatableInModule = TiParselivequeryModule.class)
public class ParseUserProxy extends KrollProxy {
    private ParseUser parseUser;

    public ParseUserProxy() {
        this.parseUser = new ParseUser();
    }

    public ParseUserProxy(ParseUser parseUser1) {
        this.parseUser = parseUser1;
    }

    ParseUser getMyParseUser() {
        return this.parseUser;
    }

    void onParseObjectResult(ParseException exc, KrollFunction callback) {
        onParseObjectResult(exc, callback, null);
    }

    void onParseObjectResult(ParseException exc, KrollFunction callback, ParseObject updatedParseObject) {
        if (callback != null) {
            callback.callAsync(krollObject, checkExceptionForResult(exc));
        }
    }

    private void fireCallback(ParseException exc, KrollFunction callback) {
        if (callback != null) {
            callback.callAsync(krollObject, checkExceptionForResult(exc));
        }
    }

    @Override
    public void release() {
        super.release();
        parseUser = null;
    }

    @Kroll.getProperty
    public String getClassName() {
        return parseUser.getClassName();
    }

    @Kroll.getProperty
    public Date getUpdatedAt() {
        return toDate(parseUser.getUpdatedAt());
    }

    @Kroll.getProperty
    public boolean getIsAvailable() {
        // to check whether the parseUser is ready for this instance
        return parseUser != null;
    }

    @Kroll.getProperty
    public Date getCreatedAt() {
        return toDate(parseUser.getCreatedAt());
    }

    @Kroll.getProperty
    public String getObjectId() {
        return parseUser.getObjectId();
    }

    @Kroll.setProperty
    public void setObjectId(String key) {
        parseUser.setObjectId(key);
    }

    @Kroll.method
    public void add(String key, Object object) {
        if (object instanceof Object[]) {
            parseUser.addAll(key, toParseDataTypeFromArray(object));
        } else {
            parseUser.add(key, object);
        }
    }

    @Kroll.method
    public void addUnique(String key, Object object) {
        if (object instanceof Object[]) {
            parseUser.addAllUnique(key, toParseDataTypeFromArray(object));
        } else {
            parseUser.addUnique(key, object);
        }
    }

    @Kroll.method
    public boolean containsKey(String key) {
        return parseUser.containsKey(key);
    }

    @Kroll.method
    public void deleteEventually(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.deleteEventually(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void deleteInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.deleteInBackground(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void fetchInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.fetchInBackground((object, e) -> onParseObjectResult(e, callback, object));
    }

    @Kroll.method
    public void fetchFromLocalDatastoreInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.fetchFromLocalDatastoreInBackground((object, e) -> onParseObjectResult(e, callback, object));
    }

    @Kroll.method
    public void fetchIfNeededInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.fetchIfNeededInBackground((object, e) -> onParseObjectResult(e, callback, object));
    }

    @Kroll.method
    public Object get(String key) {
        return parseUser.get(key);
    }

    @Kroll.method
    public Object[] getJSONArray(String key) {
        JSONArray jsonArray = parseUser.getJSONArray(key);
        if (jsonArray == null) {
            return null;
        }

        return (Object[]) KrollDict.fromJSON(jsonArray);
    }

    @Kroll.method
    public Object getJSONObject(String key) {
        JSONObject jsonObject = parseUser.getJSONObject(key);
        if (jsonObject == null) {
            return null;
        }

        return KrollDict.fromJSON(jsonObject);
    }

    @Kroll.method
    public boolean has(String key) {
        return parseUser.has(key);
    }

    @Kroll.method
    public boolean hasSameId(ParseObjectProxy parseObjectProxy) {
        return parseUser.hasSameId(parseObjectProxy.parseObject);
    }

    @Kroll.method
    public void increment(String key, @Kroll.argument(optional = true) Object amount) {
        if (amount == null) {
            parseUser.increment(key);
        } else {
            parseUser.increment(key, (int) amount);
        }
    }

    @Kroll.method
    public boolean isDataAvailable(@Kroll.argument(optional = true) Object key) {
        if (key == null) {
            return parseUser.isDataAvailable();
        } else {
            return parseUser.isDataAvailable(key.toString());
        }
    }

    @Kroll.method
    public boolean isDirty(@Kroll.argument(optional = true) Object key) {
        if (key == null) {
            return parseUser.isDirty();
        } else {
            return parseUser.isDirty(key.toString());
        }
    }

    @Kroll.method
    public String[] keySet() {
        Set<String> keySet = parseUser.keySet();
        return keySet == null ? null : TiConvert.toStringArray(keySet.toArray());
    }

    @Kroll.method
    public void pinInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.pinInBackground(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void pinInBackground(String key, @Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.pinInBackground(key, e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void put(String key, Object value) {
        parseUser.put(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void remove(String key, @Kroll.argument(optional = true) Object values) {
        if (values == null) {
            parseUser.remove(key);
        } else {
            parseUser.removeAll(key, toParseDataTypeFromArray(values));
        }
    }

    @Kroll.method
    public void revert(@Kroll.argument(optional = true) String key) {
        if (key == null) {
            parseUser.revert();
        } else {
            parseUser.revert(key);
        }
    }

    @Kroll.method
    public void saveEventually(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.saveEventually(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void saveInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.saveInBackground(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void unpinInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.unpinInBackground(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void unpinInBackground(String key, @Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.unpinInBackground(key, e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public ParseFileProxy getParseFile(String key) {
        ParseFile parseFile = parseUser.getParseFile(key);

        if (parseFile != null) {
            ParseFileProxy parseFileProxy = new ParseFileProxy();
            parseFileProxy.setParseFile(parseFile);
            return parseFileProxy;
        }

        return null;
    }

    @Kroll.method
    public ParseGeoPointProxy getParseGeoPoint(String key) {
        ParseGeoPoint parseGeoPoint = parseUser.getParseGeoPoint(key);

        if (parseGeoPoint != null) {
            ParseGeoPointProxy parseGeoPointProxy = new ParseGeoPointProxy();
            parseGeoPointProxy.createParseGeoPoint(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());
            return parseGeoPointProxy;
        }

        return null;
    }

    @Kroll.method
    public ParsePolygonProxy getParsePolygon(String key) {
        ParsePolygon parsePolygon = parseUser.getParsePolygon(key);

        if (parsePolygon != null) {
            // get coordinates list from ParsePolygon
            List<ParseGeoPoint> parseGeoPoints = parsePolygon.getCoordinates();

            // create ParseGeoPointProxy list from above coordinates
            ArrayList<ParseGeoPointProxy> parseGeoPointProxies = new ArrayList<>();
            for (ParseGeoPoint parseGeoPoint : parseGeoPoints) {
                ParseGeoPointProxy parseGeoPointProxy = new ParseGeoPointProxy();
                parseGeoPointProxy.createParseGeoPoint(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());

                parseGeoPointProxies.add(parseGeoPointProxy);
            }

            // create ParsePolygonProxy instance now
            if (!parseGeoPointProxies.isEmpty()) {
                ParsePolygonProxy parsePolygonProxy = new ParsePolygonProxy();
                parsePolygonProxy.setParseGeoPointList(parseGeoPointProxies.toArray());
                return parsePolygonProxy;
            }
        }

        return null;
    }

    @Kroll.method
    public ParseUserProxy getParseUser(String key) {
        ParseUser parseUserPointer = parseUser.getParseUser(key);

        if (parseUserPointer != null) {
            return new ParseUserProxy(parseUserPointer);
        }

        return null;
    }

    @Kroll.method
    public void setUsername(String value) {
        parseUser.setUsername(value);
    }

    @Kroll.method
    public void setEmail(String value) {
        parseUser.setEmail(value);
    }

    @Kroll.method
    public void setPassword(String value) {
        parseUser.setPassword(value);
    }

    @Kroll.method
    public String getUsername() {
        return parseUser.getUsername();
    }

    @Kroll.method
    public String getEmail() {
        return parseUser.getEmail();
    }

    @Kroll.method
    public String getSessionToken() {
        return parseUser.getSessionToken();
    }

    @Kroll.method
    public boolean isNew() {
        return parseUser.isNew();
    }

    @Kroll.method
    public boolean isAuthenticated() {
        return parseUser.isAuthenticated();
    }

    @Kroll.method
    public void signUpInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUser.signUpInBackground(exc -> fireCallback(exc, callback));
    }
}
