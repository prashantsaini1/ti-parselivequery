/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

import com.parse.ParseGeoPoint;
import com.parse.ParsePolygon;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import java.util.ArrayList;
import java.util.List;

import static ti.parselivequery.Constant.PROPERTY_PARSE_GEOPOINT_LIST;
import static ti.parselivequery.Util.getParseGeoPoints;

@SuppressWarnings("unused")
@Kroll.proxy(creatableInModule = TiParselivequeryModule.class)
public class ParsePolygonProxy extends KrollProxy {
    private ArrayList<ParseGeoPointProxy> parseGeoPointProxyList = new ArrayList<>();
    private ParsePolygon parsePolygon;

    ParsePolygon getParsePolygon() {
        return parsePolygon;
    }

    @Override
    public void release() {
        super.release();

        if (parseGeoPointProxyList != null) {
            parseGeoPointProxyList.clear();
            parseGeoPointProxyList = null;
        }

        parsePolygon = null;
    }

    @Override
    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);

        if (dict.containsKeyAndNotNull(PROPERTY_PARSE_GEOPOINT_LIST)) {
            createParsePolygon(dict.get(PROPERTY_PARSE_GEOPOINT_LIST));
        }
    }

    @Kroll.getProperty
    public Object[] getParseGeoPointList() {
        return parseGeoPointProxyList.toArray();
    }

    @Kroll.setProperty
    public void setParseGeoPointList(Object list) {
        createParsePolygon(list);
    }

    @Kroll.method
    public boolean containsPoint(Object parseGeoPointProxy) {
        return parsePolygon.containsPoint(((ParseGeoPointProxy) parseGeoPointProxy).getParseGeoPoint());
    }

    void createParsePolygon(Object parseGeoPointProxies) {
        // initiate ParseGeoPoint proxies
        createParseGeoPointProxyList(parseGeoPointProxies);

        // create ParsePolygon instance now
        List<ParseGeoPoint> points = getParseGeoPoints(parseGeoPointProxies);

        if (!points.isEmpty()) {
            parsePolygon = new ParsePolygon(points);
        }
    }

    void createParseGeoPointProxyList(Object parseGeoPointProxies) {
        parseGeoPointProxyList.clear();

        for (Object nextObject : (Object[]) parseGeoPointProxies) {
            parseGeoPointProxyList.add((ParseGeoPointProxy) nextObject);
        }
    }
}
