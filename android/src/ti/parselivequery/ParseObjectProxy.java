/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePolygon;

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

import static ti.parselivequery.Constant.PROPERTY_CLASS_NAME;
import static ti.parselivequery.ParseDataTypeConverter.toParseDataTypeFrom;
import static ti.parselivequery.ParseDataTypeConverter.toParseDataTypeFromArray;
import static ti.parselivequery.Util.checkExceptionForResult;
import static ti.parselivequery.Util.toDate;

@SuppressWarnings("unused")
@Kroll.proxy(creatableInModule = TiParselivequeryModule.class)
public class ParseObjectProxy extends KrollProxy {
    ParseObject parseObject;

    public ParseObjectProxy(ParseObject parseObject) {
        this.parseObject = parseObject;
    }

    void onParseObjectResult(ParseException exc, KrollFunction callback) {
        onParseObjectResult(exc, callback, null);
    }

    void onParseObjectResult(ParseException exc, KrollFunction callback, ParseObject updatedParseObject) {
        if (updatedParseObject != null) {
            parseObject = updatedParseObject;
        }

        if (callback != null) {
            callback.callAsync(krollObject, checkExceptionForResult(exc));
        }
    }

    @Override
    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);

        if (dict.containsKeyAndNotNull(PROPERTY_CLASS_NAME)) {
            this.parseObject = new ParseObject(dict.getString(PROPERTY_CLASS_NAME));
        }
    }

    @Override
    public void release() {
        super.release();
        parseObject = null;
    }

    @Kroll.getProperty
    public String getClassName() {
        return parseObject.getClassName();
    }

    @Kroll.getProperty
    public Date getUpdatedAt() {
        return toDate(parseObject.getUpdatedAt());
    }

    @Kroll.getProperty
    public boolean getIsAvailable() {
        // to check whether the parseObject is ready for this instance
        return parseObject != null;
    }

    @Kroll.getProperty
    public Date getCreatedAt() {
        return toDate(parseObject.getCreatedAt());
    }

    @Kroll.getProperty
    public String getObjectId() {
        return parseObject.getObjectId();
    }

    @Kroll.setProperty
    public void setObjectId(String key) {
        parseObject.setObjectId(key);
    }

    @Kroll.method
    public void add(String key, Object object) {
        if (object instanceof Object[]) {
            parseObject.addAll(key, toParseDataTypeFromArray(object));
        } else {
            parseObject.add(key, object);
        }
    }

    @Kroll.method
    public void addUnique(String key, Object object) {
        if (object instanceof Object[]) {
            parseObject.addAllUnique(key, toParseDataTypeFromArray(object));
        } else {
            parseObject.addUnique(key, object);
        }
    }

    @Kroll.method
    public boolean containsKey(String key) {
        return parseObject.containsKey(key);
    }

    @Kroll.method
    public void deleteEventually(@Kroll.argument(optional = true) KrollFunction callback) {
        parseObject.deleteEventually(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void deleteInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseObject.deleteInBackground(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void fetchInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseObject.fetchInBackground((object, e) -> onParseObjectResult(e, callback, object));
    }

    @Kroll.method
    public void fetchFromLocalDatastoreInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseObject.fetchFromLocalDatastoreInBackground((object, e) -> onParseObjectResult(e, callback, object));
    }

    @Kroll.method
    public void fetchIfNeededInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseObject.fetchIfNeededInBackground((object, e) -> onParseObjectResult(e, callback, object));
    }

    @Kroll.method
    public Object get(String key) {
        return parseObject.get(key);
    }

    @Kroll.method
    public Object[] getJSONArray(String key) {
        JSONArray jsonArray = parseObject.getJSONArray(key);
        if (jsonArray == null) {
            return null;
        }

        return (Object[]) KrollDict.fromJSON(jsonArray);
    }

    @Kroll.method
    public Object getJSONObject(String key) {
        JSONObject jsonObject = parseObject.getJSONObject(key);
        if (jsonObject == null) {
            return null;
        }

        return KrollDict.fromJSON(jsonObject);
    }

    @Kroll.method
    public boolean has(String key) {
        return parseObject.has(key);
    }

    @Kroll.method
    public boolean hasSameId(ParseObjectProxy parseObjectProxy) {
        return parseObject.hasSameId(parseObjectProxy.parseObject);
    }

    @Kroll.method
    public void increment(String key, @Kroll.argument(optional = true) Object amount) {
        if (amount == null) {
            parseObject.increment(key);
        } else {
            parseObject.increment(key, (int) amount);
        }
    }

    @Kroll.method
    public boolean isDataAvailable(@Kroll.argument(optional = true) Object key) {
        if (key == null) {
            return parseObject.isDataAvailable();
        } else {
            return parseObject.isDataAvailable(key.toString());
        }
    }

    @Kroll.method
    public boolean isDirty(@Kroll.argument(optional = true) Object key) {
        if (key == null) {
            return parseObject.isDirty();
        } else {
            return parseObject.isDirty(key.toString());
        }
    }

    @Kroll.method
    public String[] keySet() {
        Set<String> keySet = parseObject.keySet();
        return keySet == null ? null : TiConvert.toStringArray(keySet.toArray());
    }

    @Kroll.method
    public void pinInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseObject.pinInBackground(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void pinInBackground(String key, @Kroll.argument(optional = true) KrollFunction callback) {
        parseObject.pinInBackground(key, e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void put(String key, Object value) {
        parseObject.put(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void remove(String key, @Kroll.argument(optional = true) Object values) {
        if (values == null) {
            parseObject.remove(key);
        } else {
            parseObject.removeAll(key, toParseDataTypeFromArray(values));
        }
    }

    @Kroll.method
    public void revert(@Kroll.argument(optional = true) String key) {
        if (key == null) {
            parseObject.revert();
        } else {
            parseObject.revert(key);
        }
    }

    @Kroll.method
    public void saveEventually(@Kroll.argument(optional = true) KrollFunction callback) {
        parseObject.saveEventually(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void saveInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseObject.saveInBackground(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void unpinInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseObject.unpinInBackground(e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public void unpinInBackground(String key, @Kroll.argument(optional = true) KrollFunction callback) {
        parseObject.unpinInBackground(key, e -> onParseObjectResult(e, callback));
    }

    @Kroll.method
    public ParseFileProxy getParseFile(String key) {
        ParseFile parseFile = parseObject.getParseFile(key);

        if (parseFile != null) {
            ParseFileProxy parseFileProxy = new ParseFileProxy();
            parseFileProxy.setParseFile(parseFile);
            return parseFileProxy;
        }

        return null;
    }

    @Kroll.method
    public ParseGeoPointProxy getParseGeoPoint(String key) {
        ParseGeoPoint parseGeoPoint = parseObject.getParseGeoPoint(key);

        if (parseGeoPoint != null) {
            ParseGeoPointProxy parseGeoPointProxy = new ParseGeoPointProxy();
            parseGeoPointProxy.createParseGeoPoint(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());
            return parseGeoPointProxy;
        }

        return null;
    }

    @Kroll.method
    public ParsePolygonProxy getParsePolygon(String key) {
        ParsePolygon parsePolygon = parseObject.getParsePolygon(key);

        if (parsePolygon != null) {
            // get coordinates list from ParsePolygon
            List<ParseGeoPoint> parseGeoPoints = parsePolygon.getCoordinates();

            // create ParseGeoPointProxy list from above coordinates
            ArrayList<ParseGeoPointProxy> parseGeoPointProxies = new ArrayList<>();
            for (ParseGeoPoint parseGeoPoint: parseGeoPoints) {
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
    public ParseObjectProxy getParseObject(String key) {
        ParseObject parseObjectPointer = parseObject.getParseObject(key);

        if (parseObjectPointer != null) {
            return new ParseObjectProxy(parseObjectPointer);
        }

        return null;
    }
}
