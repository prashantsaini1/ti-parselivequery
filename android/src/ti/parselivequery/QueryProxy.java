/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.livequery.SubscriptionHandling;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import java.util.List;

import static ti.parselivequery.Constant.EVENT_QUERY_ERROR;
import static ti.parselivequery.Constant.EVENT_QUERY_EVENT;
import static ti.parselivequery.Constant.EVENT_QUERY_SUBSCRIBE;
import static ti.parselivequery.Constant.EVENT_QUERY_UNSUBSCRIBE;
import static ti.parselivequery.Constant.PROPERTY_CLASS_MSG;
import static ti.parselivequery.Constant.PROPERTY_CLASS_NAME;
import static ti.parselivequery.Constant.PROPERTY_COUNT;
import static ti.parselivequery.Constant.PROPERTY_EVENT_TYPE;
import static ti.parselivequery.Constant.PROPERTY_PARSE_OBJECT;
import static ti.parselivequery.Constant.PROPERTY_PARSE_OBJECTS;
import static ti.parselivequery.ParseDataTypeConverter.toParseDataTypeFrom;
import static ti.parselivequery.ParseDataTypeConverter.toParseDataTypeFromArray;
import static ti.parselivequery.Util.checkException;
import static ti.parselivequery.Util.createParseObjectProxyList;
import static ti.parselivequery.Util.getParseGeoPoint;
import static ti.parselivequery.Util.getParseGeoPoints;
import static ti.parselivequery.Util.log;
import static ti.parselivequery.Util.toStringCollection;


@SuppressWarnings("unused")
@Kroll.proxy(creatableInModule = TiParselivequeryModule.class)
public class QueryProxy extends KrollProxy {
    private SubscriptionHandling<ParseObject> subscriptionHandling;
    private ParseQuery<ParseObject> parseQuery;

    public QueryProxy() {
        super();
    }

    @Override
    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);

        if (dict.containsKeyAndNotNull(PROPERTY_CLASS_NAME)) {
            parseQuery = ParseQuery.getQuery(dict.getString(PROPERTY_CLASS_NAME));
        }
    }

    public QueryProxy setParseQuery(ParseQuery<ParseObject> parseQuery) {
        this.parseQuery = parseQuery;
        return this;
    }

    void sendParseObject(KrollFunction callback, ParseException exc, ParseObject object) {
        if (callback != null) {
            KrollDict result = new KrollDict();
            boolean isSuccess = checkException(exc, result);
            result.put(PROPERTY_PARSE_OBJECT, isSuccess ? new ParseObjectProxy(object) : null);
            callback.callAsync(krollObject, result);
        }
    }

    void sendParseObjects(KrollFunction callback, ParseException exc, List<ParseObject> objects) {
        if (callback != null) {
            KrollDict result = new KrollDict();
            checkException(exc, result);
            result.put(PROPERTY_PARSE_OBJECTS, createParseObjectProxyList(objects).toArray());
            callback.callAsync(krollObject, result);
        }
    }

    void sendParseObjectCount(KrollFunction callback, int count, ParseException exc) {
        if (callback != null) {
            KrollDict result = new KrollDict();
            boolean isSuccess = checkException(exc, result);
            result.put(PROPERTY_COUNT, isSuccess ? count : -1);
            callback.callAsync(krollObject, result);
        }
    }

    ParseQuery<ParseObject> getParseQuery() {
        return parseQuery;
    }

    private void handleEvents() {
        subscriptionHandling.handleSubscribe(query -> {
            log("Query: `" + query.getClassName() + "` subscribed");
            KrollDict d = new KrollDict();
            d.put(PROPERTY_CLASS_MSG, "");
            fireEvent(EVENT_QUERY_SUBSCRIBE, d);
        });

        subscriptionHandling.handleUnsubscribe(query -> {
            log("Query: `" + query.getClassName() + "` unsubscribed");
            subscriptionHandling = null;
            KrollDict d = new KrollDict();
            d.put(PROPERTY_CLASS_MSG, "");
            fireEvent(EVENT_QUERY_UNSUBSCRIBE, d);
        });

        subscriptionHandling.handleError((query, exception) -> {
            log("Query: `" + query.getClassName() + "` error: " + exception.getLocalizedMessage());
            KrollDict d = new KrollDict();
            d.put(PROPERTY_CLASS_MSG, exception.getLocalizedMessage());
            fireEvent(EVENT_QUERY_ERROR, d);
        });

        subscriptionHandling.handleEvents((query, event, object) -> {
            log("Query: `" + query.getClassName() + "` event = " + event.name());
            KrollDict d = new KrollDict();
            String eventType = "";

            switch (event) {
                case CREATE:
                    eventType = TiParselivequeryModule.EVENT_TYPE_CREATED;
                    break;
                case ENTER:
                    eventType = TiParselivequeryModule.EVENT_TYPE_ENTERED;
                    break;
                case UPDATE:
                    eventType = TiParselivequeryModule.EVENT_TYPE_UPDATED;
                    break;
                case LEAVE:
                    eventType = TiParselivequeryModule.EVENT_TYPE_LEFT;
                    break;
                case DELETE:
                    eventType = TiParselivequeryModule.EVENT_TYPE_DELETED;
            }

            d.put(PROPERTY_EVENT_TYPE, eventType);

            if (object != null) {
                d.put(PROPERTY_PARSE_OBJECT, new ParseObjectProxy(object));
            } else {
                d.put(PROPERTY_PARSE_OBJECT, null);
            }

            fireEvent(EVENT_QUERY_EVENT, d);
        });
    }

    @Override
    public void release() {
        super.release();
        subscriptionHandling = null;
    }

    @Kroll.method
    public void subscribe(ParseClientProxy parseClientProxy) {
        subscriptionHandling = parseClientProxy.parseLiveQueryClient.subscribe(parseQuery);
        handleEvents();
    }

    @Kroll.method
    public void unsubscribe(ParseClientProxy parseClientProxy) {
        if (subscriptionHandling != null) {
            parseClientProxy.parseLiveQueryClient.unsubscribe(parseQuery, subscriptionHandling);
        } else {
            parseClientProxy.parseLiveQueryClient.unsubscribe(parseQuery);
        }
    }

    @Kroll.method
    public void findInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseQuery.findInBackground((objects, exc) -> sendParseObjects(callback, exc, objects));
    }

    @Kroll.getProperty
    public String getClassName() {
        return parseQuery.getClassName();
    }

    @Kroll.getProperty
    public boolean getIsRunning() {
        return parseQuery.isRunning();
    }

    @Kroll.method
    public void clear(String key) {
        parseQuery.clear(key);
    }

    @Kroll.method
    public void addAscendingOrder(String key) {
        parseQuery.addAscendingOrder(key);
    }

    @Kroll.method
    public void cancel() {
        parseQuery.cancel();
    }

    @Kroll.method
    public void clearCachedResult() {
        parseQuery.clearCachedResult();
    }

    @Kroll.method
    public void countInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseQuery.countInBackground((count, exc) -> sendParseObjectCount(callback, count, exc));
    }

    @Kroll.method
    public void addDescendingOrder(String key) {
        parseQuery.addDescendingOrder(key);
    }

    @Kroll.method
    public void fromLocalDatastore() {
        parseQuery.fromLocalDatastore();
    }

    @Kroll.method
    public void fromNetwork() {
        parseQuery.fromNetwork();
    }

    @Kroll.method
    public void fromPin(@Kroll.argument(optional = true) String key) {
        if (key == null) {
            parseQuery.fromPin();
        } else {
            parseQuery.fromPin(key);
        }
    }

    @Kroll.method
    public String getCachePolicy() {
        //  IGNORE_CACHE, CACHE_ONLY, NETWORK_ONLY, CACHE_ELSE_NETWORK, NETWORK_ELSE_CACHE, CACHE_THEN_NETWORK
        return parseQuery.getCachePolicy().name();
    }

    @Kroll.method
    public void getFirstInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseQuery.getFirstInBackground((object, exc) -> sendParseObject(callback, exc, object));
    }

    @Kroll.method
    public void getInBackground(String key, @Kroll.argument(optional = true) KrollFunction callback) {
        parseQuery.getInBackground(key, (object, exc) -> sendParseObject(callback, exc, object));
    }

    @Kroll.method
    public int getLimit() {
        return parseQuery.getLimit();
    }

    @Kroll.method
    public int getMaxCacheAge() {
        return (int) parseQuery.getMaxCacheAge();
    }

    @Kroll.method
    public int getSkip() {
        return parseQuery.getSkip();
    }

    @Kroll.method
    public boolean hasCachedResult() {
        return parseQuery.hasCachedResult();
    }

    @Kroll.method
    public void ignoreACLs() {
        parseQuery.ignoreACLs();
    }

    @Kroll.method
    public void include(String key) {
        parseQuery.include(key);
    }

    @Kroll.method
    public void orderByAscending(String key) {
        parseQuery.orderByAscending(key);
    }

    @Kroll.method
    public void orderByDescending(String key) {
        parseQuery.orderByDescending(key);
    }

    @Kroll.method
    public void selectKeys(Object keys) {
        parseQuery.selectKeys(toStringCollection(keys));
    }

    @Kroll.method
    public void setCachePolicy(String key) {
        parseQuery.setCachePolicy(ParseQuery.CachePolicy.valueOf(key));
    }

    @Kroll.method
    public void setLimit(int i) {
        parseQuery.setLimit(i);
    }

    @Kroll.method
    public void setMaxCacheAge(int i) {
        parseQuery.setMaxCacheAge(i);
    }

    @Kroll.method
    public void setSkip(int i) {
        parseQuery.setSkip(i);
    }

    @Kroll.method
    public void setTrace(boolean enable) {
        parseQuery.setTrace(enable);
    }

    @Kroll.method
    public void whereContainedIn(String key, Object values) {
        parseQuery.whereContainedIn(key, toParseDataTypeFromArray(values));
    }

    @Kroll.method
    public void whereContains(String key, String subKey) {
        parseQuery.whereContains(key, subKey);
    }

    @Kroll.method
    public void whereContainsAll(String key, Object values) {
        parseQuery.whereContainsAll(key, toParseDataTypeFromArray(values));
    }

    @Kroll.method
    public void whereContainsAllStartsWith(String key, Object values) {
        parseQuery.whereContainsAllStartsWith(key, toStringCollection(values));
    }

    @Kroll.method
    public void whereDoesNotExist(String key) {
        parseQuery.whereDoesNotExist(key);
    }

    @Kroll.method
    public void whereDoesNotMatchKeyInQuery(String key, String keyInQuery, QueryProxy queryProxy) {
        parseQuery.whereDoesNotMatchKeyInQuery(key, keyInQuery, queryProxy.parseQuery);
    }

    @Kroll.method
    public void whereDoesNotMatchQuery(String key, QueryProxy queryProxy) {
        parseQuery.whereDoesNotMatchQuery(key, queryProxy.parseQuery);
    }

    @Kroll.method
    public void whereEndsWith(String key, String value) {
        parseQuery.whereEndsWith(key, value);
    }

    @Kroll.method
    public void whereEqualTo(String key, Object value) {
        parseQuery.whereEqualTo(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereExists(String key) {
        parseQuery.whereExists(key);
    }

    @Kroll.method
    public void whereFullText(String key, String value) {
        parseQuery.whereFullText(key, value);
    }

    @Kroll.method
    public void whereGreaterThan(String key, Object value) {
        parseQuery.whereGreaterThan(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereGreaterThanOrEqualTo(String key, Object value) {
        parseQuery.whereGreaterThanOrEqualTo(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereLessThan(String key, Object value) {
        parseQuery.whereLessThan(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereLessThanOrEqualTo(String key, Object value) {
        parseQuery.whereLessThanOrEqualTo(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereMatches(String key, String value) {
        parseQuery.whereMatches(key, value);
    }

    @Kroll.method
    public void whereMatchesKeyInQuery(String key, String keyInQuery, QueryProxy queryProxy) {
        parseQuery.whereMatchesKeyInQuery(key, keyInQuery, queryProxy.parseQuery);
    }

    @Kroll.method
    public void whereMatchesQuery(String key, QueryProxy queryProxy) {
        parseQuery.whereMatchesQuery(key, queryProxy.parseQuery);
    }

    @Kroll.method
    public void whereNotContainedIn(String key, Object values) {
        parseQuery.whereNotContainedIn(key, toParseDataTypeFromArray(values));
    }

    @Kroll.method
    public void whereNotEqualTo(String key, Object value) {
        parseQuery.whereNotEqualTo(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereStartsWith(String key, String value) {
        parseQuery.whereStartsWith(key, value);
    }

    @Kroll.method
    public void whereNear(String key, Object value) {
        parseQuery.whereNear(key, getParseGeoPoint(value));
    }

    @Kroll.method
    public void whereWithinKilometers(String key, Object value, double distance) {
        parseQuery.whereWithinKilometers(key, getParseGeoPoint(value), distance);
    }

    @Kroll.method
    public void whereWithinMiles(String key, Object value, double distance) {
        parseQuery.whereWithinMiles(key, getParseGeoPoint(value), distance);
    }

    @Kroll.method
    public void whereWithinRadians(String key, Object value, double distance) {
        parseQuery.whereWithinRadians(key, getParseGeoPoint(value), distance);
    }

    @Kroll.method
    public void whereWithinGeoBox(String key, Object value1, Object value2) {
        parseQuery.whereWithinGeoBox(key, getParseGeoPoint(value1), getParseGeoPoint(value2));
    }

    @Kroll.method
    public void whereWithinPolygon(String key, Object parseGeoPointProxies) {
        parseQuery.whereWithinPolygon(key, getParseGeoPoints(parseGeoPointProxies));
    }

    @Kroll.method
    public void whereWithinPolygon(String key, ParsePolygonProxy parsePolygonProxy) {
        parseQuery.whereWithinPolygon(key, parsePolygonProxy.getParsePolygon());
    }

    @Kroll.method
    public void wherePolygonContains(String key, ParseGeoPointProxy parseGeoPointProxy) {
        parseQuery.wherePolygonContains(key, parseGeoPointProxy.getParseGeoPoint());
    }
}
