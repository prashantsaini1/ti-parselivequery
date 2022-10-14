/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

import com.parse.ParseGeoPoint;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;

import static ti.parselivequery.Constant.PROPERTY_LATITUDE;
import static ti.parselivequery.Constant.PROPERTY_LONGITUDE;

@SuppressWarnings("unused")
@Kroll.proxy(creatableInModule = TiParselivequeryModule.class)
public class ParseGeoPointProxy extends KrollProxy {
    private double latitude = 90;
    private double longitude = 180;
    private ParseGeoPoint parseGeoPoint;

    @Override
    public void release() {
        super.release();
    }

    @Override
    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);

        if (dict.containsKeyAndNotNull(PROPERTY_LATITUDE) && dict.containsKeyAndNotNull(PROPERTY_LONGITUDE)) {
            createParseGeoPoint(dict.getDouble(PROPERTY_LATITUDE), dict.getDouble(PROPERTY_LONGITUDE));
        }
    }

    @Kroll.getProperty
    public double getLatitude() {
        return latitude;
    }

    @Kroll.setProperty
    public void setLatitude(double value) {
        if (value < 90 && value > -90) {
            latitude = value;
            generateParseGeoPoint();
        }
    }

    @Kroll.getProperty
    public double getLongitude() {
        return longitude;
    }

    @Kroll.setProperty
    public void setLongitude(double value) {
        if (value < 180 && value > -180) {
            longitude = value;
            generateParseGeoPoint();
        }
    }

    void setParseGeoPoint(ParseGeoPoint pgp) {
        parseGeoPoint = pgp;
    }

    ParseGeoPoint getParseGeoPoint() {
        return parseGeoPoint;
    }

    void createParseGeoPoint(double lt, double lng) {
        if (lt < 90 && lt > -90 && lng < 180 && lng > -180) {
            latitude = lt;
            longitude = lng;
            generateParseGeoPoint();
        }
    }

    private void generateParseGeoPoint() {
        if (latitude < 90 && latitude > -90 && longitude < 180 && longitude > -180) {
            parseGeoPoint = new ParseGeoPoint(latitude, longitude);
        }
    }
}
