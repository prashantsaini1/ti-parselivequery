/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

import org.appcelerator.titanium.util.TiConvert;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Prashant Saini on 10/10/22.
 */
public class ParseDataTypeConverter {
    static Object toParseDataTypeFrom(Object value) {
        Object parseDataTypeValue = value;

        if (value instanceof ParseObjectProxy) {
            parseDataTypeValue = ((ParseObjectProxy) value).parseObject;
        } else if (value instanceof ParseUserProxy) {
            parseDataTypeValue = ((ParseUserProxy) value).getMyParseUser();
        } else if (value instanceof ParseFileProxy) {
            parseDataTypeValue = ((ParseFileProxy) value).getParseFile();
        } else if (value instanceof ParseGeoPointProxy) {
            parseDataTypeValue = ((ParseGeoPointProxy) value).getParseGeoPoint();
        } else if (value instanceof ParsePolygonProxy) {
            parseDataTypeValue = ((ParsePolygonProxy) value).getParsePolygon();
        } else if (value instanceof HashMap) {
            //noinspection unchecked
            parseDataTypeValue = TiConvert.toJSON((HashMap) value);
        } else if (value instanceof Object[]) {
            parseDataTypeValue = TiConvert.toJSONArray((Object[]) value);
        } else if (value == null) {
            parseDataTypeValue = JSONObject.NULL;
        }
        
        return parseDataTypeValue;
    }

    static List<Object> toParseDataTypeFromArray(Object values) {
        List<Object> list = new ArrayList<>();

        for (Object nextObject : (Object[]) values) {
            list.add( toParseDataTypeFrom(nextObject) );
        }

        return list;
    }
}
