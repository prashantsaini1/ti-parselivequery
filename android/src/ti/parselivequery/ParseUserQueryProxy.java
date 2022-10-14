/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
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
import static ti.parselivequery.Constant.PROPERTY_PARSE_USER;
import static ti.parselivequery.Constant.PROPERTY_PARSE_USERS;
import static ti.parselivequery.ParseDataTypeConverter.toParseDataTypeFrom;
import static ti.parselivequery.ParseDataTypeConverter.toParseDataTypeFromArray;
import static ti.parselivequery.Util.checkException;
import static ti.parselivequery.Util.createParseUserProxyList;
import static ti.parselivequery.Util.getParseGeoPoint;
import static ti.parselivequery.Util.getParseGeoPoints;
import static ti.parselivequery.Util.log;
import static ti.parselivequery.Util.toStringCollection;


@SuppressWarnings("unused")
@Kroll.proxy(creatableInModule = TiParselivequeryModule.class)
public class ParseUserQueryProxy extends KrollProxy {
    private SubscriptionHandling<ParseUser> subscriptionHandling;
    private ParseQuery<ParseUser> parseUserQuery;

    @Override
    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);

        if (dict.containsKeyAndNotNull(PROPERTY_CLASS_NAME)) {
            parseUserQuery = ParseQuery.getQuery(dict.getString(PROPERTY_CLASS_NAME));
        }
    }

    public ParseUserQueryProxy setParseUserQuery(ParseQuery<ParseUser> parseUserQuery) {
        this.parseUserQuery = parseUserQuery;
        return this;
    }

    void sendParseUser(KrollFunction callback, ParseException exc, ParseUser userObject) {
        if (callback != null) {
            KrollDict result = new KrollDict();
            boolean isSuccess = checkException(exc, result);
            result.put(PROPERTY_PARSE_USER, isSuccess ? new ParseUserProxy(userObject) : null);
            callback.callAsync(krollObject, result);
        }
    }

    void sendParseUsers(KrollFunction callback, ParseException exc, List<ParseUser> objects) {
        if (callback != null) {
            KrollDict result = new KrollDict();
            checkException(exc, result);
            result.put(PROPERTY_PARSE_USERS, createParseUserProxyList(objects).toArray());
            callback.callAsync(krollObject, result);
        }
    }

    void sendParseUserCount(KrollFunction callback, int count, ParseException exc) {
        if (callback != null) {
            KrollDict result = new KrollDict();
            boolean isSuccess = checkException(exc, result);
            result.put(PROPERTY_COUNT, isSuccess ? count : -1);
            callback.callAsync(krollObject, result);
        }
    }

    ParseQuery<ParseUser> getParseQuery() {
        return parseUserQuery;
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

        subscriptionHandling.handleEvents((query, event, userObject) -> {
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

            if (userObject != null) {
                d.put(PROPERTY_PARSE_USER, new ParseUserProxy(userObject));
            } else {
                d.put(PROPERTY_PARSE_USER, null);
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
        subscriptionHandling = parseClientProxy.parseLiveQueryClient.subscribe(parseUserQuery);
        handleEvents();
    }

    @Kroll.method
    public void unsubscribe(ParseClientProxy parseClientProxy) {
        if (subscriptionHandling != null) {
            parseClientProxy.parseLiveQueryClient.unsubscribe(parseUserQuery, subscriptionHandling);
        } else {
            parseClientProxy.parseLiveQueryClient.unsubscribe(parseUserQuery);
        }
    }

    @Kroll.method
    public void findInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUserQuery.findInBackground((objects, exc) -> sendParseUsers(callback, exc, objects));
    }

    @Kroll.getProperty
    public String getClassName() {
        return parseUserQuery.getClassName();
    }

    @Kroll.getProperty
    public boolean getIsRunning() {
        return parseUserQuery.isRunning();
    }

    @Kroll.method
    public void clear(String key) {
        parseUserQuery.clear(key);
    }

    @Kroll.method
    public void addAscendingOrder(String key) {
        parseUserQuery.addAscendingOrder(key);
    }

    @Kroll.method
    public void cancel() {
        parseUserQuery.cancel();
    }

    @Kroll.method
    public void clearCachedResult() {
        parseUserQuery.clearCachedResult();
    }

    @Kroll.method
    public void countInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUserQuery.countInBackground((count, exc) -> sendParseUserCount(callback, count, exc));
    }

    @Kroll.method
    public void addDescendingOrder(String key) {
        parseUserQuery.addDescendingOrder(key);
    }

    @Kroll.method
    public void fromLocalDatastore() {
        parseUserQuery.fromLocalDatastore();
    }

    @Kroll.method
    public void fromNetwork() {
        parseUserQuery.fromNetwork();
    }

    @Kroll.method
    public void fromPin(@Kroll.argument(optional = true) String key) {
        if (key == null) {
            parseUserQuery.fromPin();
        } else {
            parseUserQuery.fromPin(key);
        }
    }

    @Kroll.method
    public String getCachePolicy() {
        //  IGNORE_CACHE, CACHE_ONLY, NETWORK_ONLY, CACHE_ELSE_NETWORK, NETWORK_ELSE_CACHE, CACHE_THEN_NETWORK
        return parseUserQuery.getCachePolicy().name();
    }

    @Kroll.method
    public void getFirstInBackground(@Kroll.argument(optional = true) KrollFunction callback) {
        parseUserQuery.getFirstInBackground((userObject, exc) -> sendParseUser(callback, exc, userObject));
    }

    @Kroll.method
    public void getInBackground(String key, @Kroll.argument(optional = true) KrollFunction callback) {
        parseUserQuery.getInBackground(key, (userObject, exc) -> sendParseUser(callback, exc, userObject));
    }

    @Kroll.method
    public int getLimit() {
        return parseUserQuery.getLimit();
    }

    @Kroll.method
    public int getMaxCacheAge() {
        return (int) parseUserQuery.getMaxCacheAge();
    }

    @Kroll.method
    public int getSkip() {
        return parseUserQuery.getSkip();
    }

    @Kroll.method
    public boolean hasCachedResult() {
        return parseUserQuery.hasCachedResult();
    }

    @Kroll.method
    public void ignoreACLs() {
        parseUserQuery.ignoreACLs();
    }

    @Kroll.method
    public void include(String key) {
        parseUserQuery.include(key);
    }

    @Kroll.method
    public void orderByAscending(String key) {
        parseUserQuery.orderByAscending(key);
    }

    @Kroll.method
    public void orderByDescending(String key) {
        parseUserQuery.orderByDescending(key);
    }

    @Kroll.method
    public void selectKeys(Object keys) {
        parseUserQuery.selectKeys(toStringCollection(keys));
    }

    @Kroll.method
    public void setCachePolicy(String key) {
        parseUserQuery.setCachePolicy(ParseQuery.CachePolicy.valueOf(key));
    }

    @Kroll.method
    public void setLimit(int i) {
        parseUserQuery.setLimit(i);
    }

    @Kroll.method
    public void setMaxCacheAge(int i) {
        parseUserQuery.setMaxCacheAge(i);
    }

    @Kroll.method
    public void setSkip(int i) {
        parseUserQuery.setSkip(i);
    }

    @Kroll.method
    public void setTrace(boolean enable) {
        parseUserQuery.setTrace(enable);
    }

    @Kroll.method
    public void whereContainedIn(String key, Object values) {
        parseUserQuery.whereContainedIn(key, toParseDataTypeFromArray(values));
    }

    @Kroll.method
    public void whereContains(String key, String subKey) {
        parseUserQuery.whereContains(key, subKey);
    }

    @Kroll.method
    public void whereContainsAll(String key, Object values) {
        parseUserQuery.whereContainsAll(key, toParseDataTypeFromArray(values));
    }

    @Kroll.method
    public void whereContainsAllStartsWith(String key, Object values) {
        parseUserQuery.whereContainsAllStartsWith(key, toStringCollection(values));
    }

    @Kroll.method
    public void whereDoesNotExist(String key) {
        parseUserQuery.whereDoesNotExist(key);
    }

    @Kroll.method
    public void whereDoesNotMatchKeyInQuery(String key, String keyInQuery, ParseUserQueryProxy parseUserQueryProxy) {
        parseUserQuery.whereDoesNotMatchKeyInQuery(key, keyInQuery, parseUserQueryProxy.parseUserQuery);
    }

    @Kroll.method
    public void whereDoesNotMatchQuery(String key, ParseUserQueryProxy parseUserQueryProxy) {
        parseUserQuery.whereDoesNotMatchQuery(key, parseUserQueryProxy.parseUserQuery);
    }

    @Kroll.method
    public void whereEndsWith(String key, String value) {
        parseUserQuery.whereEndsWith(key, value);
    }

    @Kroll.method
    public void whereEqualTo(String key, Object value) {
        parseUserQuery.whereEqualTo(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereExists(String key) {
        parseUserQuery.whereExists(key);
    }

    @Kroll.method
    public void whereFullText(String key, String value) {
        parseUserQuery.whereFullText(key, value);
    }

    @Kroll.method
    public void whereGreaterThan(String key, Object value) {
        parseUserQuery.whereGreaterThan(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereGreaterThanOrEqualTo(String key, Object value) {
        parseUserQuery.whereGreaterThanOrEqualTo(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereLessThan(String key, Object value) {
        parseUserQuery.whereLessThan(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereLessThanOrEqualTo(String key, Object value) {
        parseUserQuery.whereLessThanOrEqualTo(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereMatches(String key, String value) {
        parseUserQuery.whereMatches(key, value);
    }

    @Kroll.method
    public void whereMatchesKeyInQuery(String key, String keyInQuery, ParseUserQueryProxy parseUserQueryProxy) {
        parseUserQuery.whereMatchesKeyInQuery(key, keyInQuery, parseUserQueryProxy.parseUserQuery);
    }

    @Kroll.method
    public void whereMatchesQuery(String key, ParseUserQueryProxy parseUserQueryProxy) {
        parseUserQuery.whereMatchesQuery(key, parseUserQueryProxy.parseUserQuery);
    }

    @Kroll.method
    public void whereNotContainedIn(String key, Object values) {
        parseUserQuery.whereNotContainedIn(key, toParseDataTypeFromArray(values));
    }

    @Kroll.method
    public void whereNotEqualTo(String key, Object value) {
        parseUserQuery.whereNotEqualTo(key, toParseDataTypeFrom(value));
    }

    @Kroll.method
    public void whereStartsWith(String key, String value) {
        parseUserQuery.whereStartsWith(key, value);
    }

    @Kroll.method
    public void whereNear(String key, Object value) {
        parseUserQuery.whereNear(key, getParseGeoPoint(value));
    }

    @Kroll.method
    public void whereWithinKilometers(String key, Object value, double distance) {
        parseUserQuery.whereWithinKilometers(key, getParseGeoPoint(value), distance);
    }

    @Kroll.method
    public void whereWithinMiles(String key, Object value, double distance) {
        parseUserQuery.whereWithinMiles(key, getParseGeoPoint(value), distance);
    }

    @Kroll.method
    public void whereWithinRadians(String key, Object value, double distance) {
        parseUserQuery.whereWithinRadians(key, getParseGeoPoint(value), distance);
    }

    @Kroll.method
    public void whereWithinGeoBox(String key, Object value1, Object value2) {
        parseUserQuery.whereWithinGeoBox(key, getParseGeoPoint(value1), getParseGeoPoint(value2));
    }

    @Kroll.method
    public void whereWithinPolygon(String key, Object parseGeoPointProxies) {
        parseUserQuery.whereWithinPolygon(key, getParseGeoPoints(parseGeoPointProxies));
    }

    @Kroll.method
    public void whereWithinPolygon(String key, ParsePolygonProxy parsePolygonProxy) {
        parseUserQuery.whereWithinPolygon(key, parsePolygonProxy.getParsePolygon());
    }

    @Kroll.method
    public void wherePolygonContains(String key, ParseGeoPointProxy parseGeoPointProxy) {
        parseUserQuery.wherePolygonContains(key, parseGeoPointProxy.getParseGeoPoint());
    }
}
