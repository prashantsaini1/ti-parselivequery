/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

import android.content.Context;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiConvert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static ti.parselivequery.Constant.PROPERTY_CLASS_MSG;
import static ti.parselivequery.Constant.PROPERTY_ERROR_CODE;
import static ti.parselivequery.Constant.PROPERTY_PARSE_USER;
import static ti.parselivequery.Constant.PROPERTY_SUCCESS;


public class Util {
    final static String TAG = "TiParse";

    static Context AppContext() {
        return TiApplication.getInstance().getApplicationContext();
    }

    static void log(String msg) {
        Log.i(TAG, msg);
    }

    static List<String> toStringCollection(Object jsArray) {
        return Arrays.asList( TiConvert.toStringArray( (Object[]) jsArray) );
    }

    static boolean checkException(ParseException e, KrollDict result) {
        boolean isSuccess = e == null;
        result.put(PROPERTY_SUCCESS, isSuccess);
        result.put(PROPERTY_ERROR_CODE, isSuccess ? null : e.getCode());
        result.put(PROPERTY_CLASS_MSG, isSuccess ? "" : e.getLocalizedMessage());
        return isSuccess;
    }

    static KrollDict checkExceptionForResult(ParseException e) {
        boolean isSuccess = e == null;
        KrollDict result = new KrollDict();
        result.put(PROPERTY_SUCCESS, isSuccess);
        result.put(PROPERTY_ERROR_CODE, isSuccess ? null : e.getCode());
        result.put(PROPERTY_CLASS_MSG, isSuccess ? "" : e.getLocalizedMessage());
        return result;
    }

    static ArrayList<ParseObjectProxy> createParseObjectProxyList(List<ParseObject> objects) {
        ArrayList<ParseObjectProxy> parseObjectProxyList = new ArrayList<>();

        if (objects != null && objects.size() > 0) {
            for (ParseObject object : objects) {
                parseObjectProxyList.add(new ParseObjectProxy(object));
            }
        }

        return parseObjectProxyList;
    }

    static ArrayList<ParseUserProxy> createParseUserProxyList(List<ParseUser> userObjects) {
        ArrayList<ParseUserProxy> parseUserProxyList = new ArrayList<>();

        if (userObjects != null && userObjects.size() > 0) {
            for (ParseUser userObject : userObjects) {
                parseUserProxyList.add(new ParseUserProxy(userObject));
            }
        }

        return parseUserProxyList;
    }

    static List<ParseObject> getParseObjectList(Object values) {
        ArrayList<ParseObject> parseObjectArrayList = new ArrayList<>();

        if (values instanceof Object[]) {
            for (Object value : (Object[]) values) {
                if (value instanceof ParseObjectProxy) {
                    parseObjectArrayList.add(((ParseObjectProxy) value).parseObject);
                }
            }
        }

        return parseObjectArrayList;
    }

    static Date toDate(Date date) {
        if (date == null) {
            return null;
        }

        return TiConvert.toDate(date);
    }

    static ArrayList<QueryProxy> createQueryProxyList(Object values) {
        ArrayList<QueryProxy> queryProxyList = new ArrayList<>();

        if (values instanceof Object[]) {
            for (Object value : (Object[]) values) {
                if (value instanceof QueryProxy) {
                    queryProxyList.add(((QueryProxy) value));
                }
            }
        }

        return queryProxyList;
    }

    static QueryProxy createCompoundQueryOr(Object values) {
        ArrayList<QueryProxy> queryProxyList = createQueryProxyList(values);

        if (queryProxyList.size() > 0) {
            List<ParseQuery<ParseObject>> queries = new ArrayList<>();

            for (QueryProxy queryProxy : queryProxyList) {
                queries.add(queryProxy.getParseQuery());
            }

            return new QueryProxy().setParseQuery( ParseQuery.or(queries) );

        } else {
            return null;
        }
    }

    static ParseGeoPoint getParseGeoPoint(Object value) {
        return ((ParseGeoPointProxy) value).getParseGeoPoint();
    }

    static List<ParseGeoPoint> getParseGeoPoints(Object list) {
        // create ParsePolygon instance now
        List<ParseGeoPoint> parseGeoPoints = new ArrayList<>();

        for (Object nextObject: (Object[]) list) {
            parseGeoPoints.add( ((ParseGeoPointProxy) nextObject).getParseGeoPoint() );
        }

        return parseGeoPoints;
    }

    static ParseUserProxy parseUserProxy(ParseUser parseUser) {
        if (parseUser != null) {
            return new ParseUserProxy(parseUser);
        } else {
            return null;
        }
    }

    static void fireCallbackForParseUser(ParseUser parseUser, KrollObject krollObject, ParseException exc, KrollFunction callback) {
        KrollDict result = new KrollDict();
        boolean isSuccess = checkException(exc, result);
        result.put(PROPERTY_PARSE_USER, isSuccess ? Util.parseUserProxy(parseUser) : null);
        callback.callAsync(krollObject, result);
    }
}
