## ti.parselivequery
------------------------

```js
import Parse from 'ti.parselivequery';
```
------------------------

# Version 1.0.0
#### Module Constants
```js
// use to set/get log-levels
Parse.LOG_LEVEL_DEBUG
Parse.LOG_LEVEL_ERROR
Parse.LOG_LEVEL_INFO
Parse.LOG_LEVEL_VERBOSE
Parse.LOG_LEVEL_WARNING
Parse.LOG_LEVEL_NONE

// use to set/get cache-policy
Parse.IGNORE_CACHE
Parse.CACHE_ONLY
Parse.NETWORK_ONLY
Parse.CACHE_ELSE_NETWORK
Parse.NETWORK_ELSE_CACHE
Parse.CACHE_THEN_NETWORK

// use to know query event type in `event`
Parse.EVENT_TYPE_ENTERED
Parse.EVENT_TYPE_LEFT
Parse.EVENT_TYPE_CREATED
Parse.EVENT_TYPE_UPDATED
Parse.EVENT_TYPE_DELETED
```
------------------------

### Common patterns
```js
// All callbacks follow below pattern:
function allCallbacks(e) {
    const IMPORTANT_MESSAGE = 'e.success: this will be `true` when the operation was successful, otherwise `false`,
    e.code: this will be `null` when e.success = true, otherwise will hold an error code from Parse SDK
    e.message: error message corresponds to `e.code` above, otherwise empty
    *******************************************************************************************
    some callbacks will return extra field to access the required data of that callback
    e.g., e.parseFile, e.parseUser, e.parseObject, e.parseObjects, etc...
    only this extra field will be mentioned in callbacks in rest of the documentation';
}
```
------------------------

### Parse
```js
// Always initialize the module before accessing anything
const isInitialized = Parse.initialize({
    appId: '',
    clientKey: '',
    server: '',
    enableLocalStore: true  // default `false`
});

// Parse methods
Parse.destroyParse();
Parse.setLogLevel(int logLevel);
Parse.setServer(String url);
Parse.fetchAllInBackground([ParseObject], optional => alert(optional.success));
Parse.pinAllInBackground([ParseObject], optional => alert(optional.success));
Parse.pinAllInBackground('key', [ParseObject], optional => alert(optional.success));
Parse.saveAllInBackground([ParseObject], optional => alert(optional.success));
Parse.unpinAllInBackground(optional => alert(optional.success));
Parse.unpinAllInBackground([ParseObject], optional => alert(optional.success));
Parse.unpinAllInBackground('key', optional => alert(optional.success));
Parse.unpinAllInBackground('key', [ParseObject], optional => alert(optional.success));
```

### Parse Client
```js
// always create a single client for single app session
const Client = Parse.createParseClient();

// properties
const isConnected = Client.isConnected;

// events
Client.addEventListener('clientConnected', e => alert(e.value));        // value: true
Client.addEventListener('clientDisconnected', e => alert(e.value));     // value: true
Client.addEventListener('clientSocketError', e => alert(e.value));      // value: error message
Client.addEventListener('clientError', e => alert(e.value));            // value: error message

// methods
Client.registerListener();      // this will register & fire above events
Client.unregisterListener();    // this will only unregister above events
Client.reconnectClient();
Client.reconnectClientIfNeeded();
Client.disconnectClient();
Client.destroyClient();         // disconnect client, unregister events and destroy the client instancce
```

### Parse Query
```js
// `className` is mandatory here
const Query = Parse.createQuery({ className: 'object-name' });

// properties
Query.className;
Query.isRunning;         // to check whether this query is already running


// events
Query.addEventListener('error', e => alert(e.message));
Query.addEventListener('subscribe', () => { /* just to know query has been subscribed */ });
Query.addEventListener('unsubscribe', () => { /* just to know query has been un-subscribed */ });
Query.addEventListener('event', e => {
    // e.parseObject = ParseObject associated with this query
    // e.parseEventType = Parse.EVENT_TYPE_CREATED , etc.
});


// methods
Query.subscribe(Client);
Query.unsubscribe(Client);
Query.findInBackground(e => {
    // optional callback
    // e.parseObjects - array having ParseObject instances
});
Query.clear('key');
Query.addAscendingOrder('key');
Query.cancel();
Query.clearCachedResult();
Query.countInBackground(e => {
    // optional callback
    // e.count - array having ParseObject instances
});
Query.addDescendingOrder('key');
Query.fromLocalDatastore();
Query.fromNetwork();
Query.fromPin('optional key');
Query.getCachePolicy();    // returns String
Query.getFirstInBackground(e => {
    // optional callback
    // e.parseObject - ParseObject instance or null
});
Query.getInBackground('key', e => {
    // optional callback
    // e.parseObject - ParseObject instance or null
});
Query.getLimit();          // returns int
Query.getMaxCacheAge();    // returns int
Query.getSkip();           // returns int
Query.hasCachedResult();   // returns boolean
Query.ignoreACLs();
Query.include('key');
Query.orderByAscending('key');
Query.orderByDescending('key');
Query.selectKeys(['array of keys']);
Query.setCachePolicy('key');
Query.setLimit(10);
Query.setMaxCacheAge(10);
Query.setSkip(10);
Query.setTrace(true);
Query.whereContainedIn('key', [1, 'Joseph', {name: 'Prashant Saini'}]);
Query.whereContains('key', 'sub-key');
Query.whereContainsAll('key', ['array of anything which can be converted to JSON']);
Query.whereContainsAllStartsWith('key', ['array of matching keys']);
Query.whereDoesNotExist('key');
Query.whereDoesNotMatchKeyInQuery('key', 'key in query', Query);
Query.whereDoesNotMatchQuery('key', Query);
Query.whereEndsWith('key', 'value');
Query.whereEqualTo('key', anyValueToMatchForKey);
Query.whereExists('key');
Query.whereFullText('key', 'value');
Query.whereGreaterThan('key', anyValueToMatchForKey);
Query.whereGreaterThanOrEqualTo('key', anyValueToMatchForKey);
Query.whereLessThan('key', anyValueToMatchForKey);
Query.whereLessThanOrEqualTo('key', anyValueToMatchForKey);
Query.whereMatches('key', 'value');
Query.whereMatchesKeyInQuery('key', 'key in query', Query);
Query.whereMatchesQuery('key', Query);
Query.whereNotContainedIn('key', ['array of anything which can be converted to JSON']);
Query.whereNotEqualTo('key', anyValueToMatchForKey);
Query.whereStartsWith('key', 'value');
```

### Parse Object
```js
// `className` is mandatory
const ParseObject = Parse.createParseObject({ className: 'object-name' });

// properties
ParseObject.className;
ParseObject.isAvailable;    // read-only: whether the ParseObject is ready
ParseObject.updatedAt;      // read-only: JS Date
ParseObject.createdAt;      // read-only: JS Date
ParseObject.objectId;       // read-write: String: unique object-id for this object

// methods
ParseObject.add('key', anyJSONObject);
ParseObject.addUnique('key', anyJSONObject);
ParseObject.containsKey('key');                             // returns boolean
ParseObject.deleteEventually(optional => alert(optional.success));
ParseObject.deleteInBackground(optional => alert(optional.success));
ParseObject.fetchInBackground(optional => alert(optional.success));                      // this will internally update the current ParseObject before firing callback
ParseObject.fetchFromLocalDatastoreInBackground(optional => alert(optional.success));    // this will internally update the current ParseObject before firing callback
ParseObject.fetchIfNeededInBackground(optional => alert(optional.success));              // this will internally update the current ParseObject before firing callback
ParseObject.get('key');                              // returns object
ParseObject.getJSONArray('key');                     // returns JSON array
ParseObject.getJSONObject('key');                    // returns JSON object
ParseObject.has('key');                              // returns boolean
ParseObject.hasSameId(ParseObjectProxy);             // returns boolean
ParseObject.increment('key', anyOptionalInteger);
ParseObject.isDataAvailable('optional key');                    // returns boolean
ParseObject.isDirty('optional key');                            // returns boolean
ParseObject.keySet();                                           // returns String array
ParseObject.pinInBackground(optional => alert(optional.success));
ParseObject.pinInBackground('key', optional => alert(optional.success));
ParseObject.put('key', anyJSONObjectOrPrimitiveValue);
ParseObject.remove('key', ['optional array of anything which can be converted to JSON']);
ParseObject.revert('optional key');
ParseObject.saveEventually(optional => alert(optional.success));
ParseObject.saveInBackground(optional => alert(optional.success));
ParseObject.unpinInBackground(optional => alert(optional.success));
ParseObject.unpinInBackground('key', optional => alert(optional.success));
```

------------------------

# Version 1.1.0
### ParseFile

```js
const file1 = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'textfile.txt');
const file2 = Ti.Filesystem.getFile(Ti.Filesystem.resourcesDirectory, 'image.jpg');

// only two parameters are supported - fileName and fileData
// both these parameters can be added/altered at any stage
const parseFile1 = Parse.createParseFile({
    fileName: 'textfile.txt',   // file extension must be available
    fileData: file1.read()
});

// or create ParseFile instance this way
const parseFile2 = Parse.createParseFile();
parseFile2.fileName = 'image.jpg';
parseFile2.fileData = file2.read();

// once these parameters are set, call invalidate() to internally create ParseFile instance to execute further methods
parseFile1.invalidate();

// now we can execute below further methods
parseFile2.cancel();            // Cancels the operations for this {@code ParseFile} if they are still in the task queue. However, if a network request has already been started for an operation, the network request will not be canceled.
parseFile2.getName();           // The filename. Before save is called, this is just the filename given by the user (if any). After save is called, that name gets prefixed with a unique identifier.
parseFile2.getUrl();            // This returns the url of the file. It's only available after you save or after you get the file from a ParseObject.
parseFile2.isDataAvailable();   // Whether the file has available data.
parseFile2.isDirty();           // Whether the file still needs to be saved.

/**
 * Asynchronously gets the data from cache if available or fetches its content from the network.
 * Supports below 2 callbacks
 * @param dataCallback is called when the get completes.
 * @param progressCallback (optional) is called periodically with progress updates.
 */
parseFile2.getDataInBackground(e => {
    // Mandatory callback
    // e.fileData = Ti.Blob instance, similar to as we passed at creation time
}, e => {
    // Optional callback
    // e.fileProgress = 0, 1, 2... to 100 (100 guarantees the fetch completion and first callback will be called)
});

/**
 * Saves the file to the Parse cloud in a background thread. `progressCallback` is guaranteed to
 * be called with 100 before saveCallback is called.
 * Supports below 2 callbacks
 * @param saveCallback gets called when the save completes.
 * @param progressCallback is called periodically with progress updates.
 * */
parseFile2.saveDataInBackground(e => {
    // optional callback
}, e => {
    // Optional callback
    // e.fileProgress = 0, 1, 2... to 100 (100 guarantees the fetch completion and first callback will be called)
});

// finally you can add ParseFile instance in ParseObject
ParseObject.put('mytextfile', parseFile1);
ParseObject.put('myimagefile', parseFile2);

// Retrieve ParseFile from ParseObject
const imageParseFile = ParseObject.getParseFile('myimagefile');
const fileName = imageParseFile.fileName;
let fileData = imageParseFile.fileData;   // it will be null until we call `getDataInBackground`

// no need to call `invalidate()` in this case since we are fetching the ParseFile from server
imageParseFile.getDataInBackground(e => {
    // Mandatory callback
    // e.fileData = Ti.Blob instance, similar to as we passed at creation time
});
```

### Compound Query

```js
const query1 = Parse.createQuery({ className: 'object-name1' });
const query2 = Parse.createQuery({ className: 'object-name2' });
const query3 = Parse.createQuery({ className: 'object-name3' });

// `createCompoundQuery()` needs an array of Query instances
// it will return a new Query instance which you can use further to execute Query class methods listed in v1.0.0
const compoundQuery = Parse.createCompoundQuery([query1, query2, query3]);
```

### ParseGeoPoint, ParsePolygon

```js
const geoPoint1 = Parse.createParseGeoPoint({
    latitude: -30,  // allowed range: -90 < latitude < 90
    longitude: -40.0    // allowed range: -180 < latitude < 180
});

const geoPoint2 = Parse.createParseGeoPoint({
    latitude: 30,
    longitude: 40.0
});

const geoPoint3 = Parse.createParseGeoPoint();
geoPoint3.latitude = 89;
geoPoint3.longitude = 179.0;

// minimum 3 geo-points are required to create a ParsePolygon instance
const polygon1 = Parse.createParsePolygon({
    parseGeoPointList: [geoPoint1, geoPoint2, geoPoint3]   // array of 
});

polygon.containsPoint(geoPoint2);   // true/false: if polygon contains ParseGeoPoint

// add ParseGeoPoint/ParsePolygon in ParseObject
ParseObject.put('geopoint1', geoPoint1);
ParseObject.put('polygon', polygon);

// retrieve like this
const geoPointOne = ParseObject.getParseGeoPoint('geopoint1');
const polygonOne = ParseObject.getParsePolygon('polygon');

// GeoQueries
Query.whereNear('location', geoPoint2);
Query.setLimit(10); // max 100
Query.findInBackground(...);    // use findInBackground as already developed in v1.0.0
Query.whereWithinKilometers('key', geoPoint1, maxDistanceInNumber);
Query.whereWithinMiles('key', geoPoint2, maxDistanceInNumber);
Query.whereWithinRadians('key', geoPoint3, maxDistanceInNumber);
Query.whereWithinGeoBox('key', geoPoint1, geoPoint2);
Query.whereWithinPolygon('key', [geoPoint1, geoPoint2, geoPoint3]); // either array of min 3 ParseGeoPoint
Query.whereWithinPolygon('key', parsePolygonProxy.getParsePolygon());   // or ParsePolygon instance
Query.wherePolygonContains('key', geoPoint2);   // polygon.containsPoint(geoPoint2) is more efficient alternate
```

### DATA TYPES | Parse.put() & Parse.get methods

```js
// equivalent put/get methods for currently supported data types
ParseObject.put('key', number);
ParseObject.put('key', string);
ParseObject.put('key', boolean);
ParseObject.put('key', null);
ParseObject.get('key'); // cast to above type in JS

ParseObject.put('key', [array]);
ParseObject.getJSONArray('key');

ParseObject.put('key', {dictionary});
ParseObject.getJSONObject('key');

ParseObject.put('key', ParseObject);    // acts as Pointer for other ParseObject
ParseObject.getParseObject('key');

ParseObject.put('key', ParseFile);
ParseObject.getParseFile('key');

ParseObject.put('key', ParseGeoPoint);
ParseObject.getParseGeoPoint('key');

ParseObject.put('key', ParsePolygon);
ParseObject.getParsePolygon('key');
```

### Changes in existing Parse/Query methods
```js
// pass any data type mentioned above in below methods
// all these methods are still backward compatible, they now support more data types
ParseObject.addAll('key', 'array of data-type mentioned above');
ParseObject.addAllUnique('key', 'array of data-type mentioned above');
ParseObject.removeAll('key', 'array of data-type mentioned above');

Query.whereContainedIn('key', 'array of data-type mentioned above');
Query.whereContainsAll('key', 'array of data-type mentioned above');
Query.whereNotContainedIn('key', 'array of data-type mentioned above');

Query.whereEqualTo('key', 'data-type mentioned above');
Query.whereGreaterThan('key', 'data-type mentioned above');
Query.whereGreaterThanOrEqualTo('key', 'data-type mentioned above');
Query.whereLessThan('key', 'data-type mentioned above');
Query.whereLessThanOrEqualTo('key', 'data-type mentioned above');
Query.whereNotEqualTo('key', 'data-type mentioned above');
```

### ParseUser
##### (subclass of ParseObject, already contains all methods available in ParseObject class)
```js
const parseUser = Parse.createParseUser();

parseUser.setUsername('string');
parseUser.setEmail('string');
parseUser.setPassword('string');
parseUser.getUsername();
parseUser.getEmail();
parseUser.getSessionToken(); // session token for a user, if they are logged in.

parseUser.getParseUser('key to retrieve another parseUser as pointers');

// Indicates whether this user was created during this session through a call to signUpInBackground
// or by logging in with a linked service such as Facebook.
parseUser.isNew();

/**
 * Whether the ParseUser has been authenticated on this device. This will be true if the
 * ParseUser was obtained via a logIn or signUp method. Only an authenticated ParseUser can be
 * saved (with altered attributes) and deleted.
 */
parseUser.isAuthenticated();


/**
* Signs up a new user. You should call this instead of {@link #save} for new ParseUsers. This
* will create a new ParseUser on the server, and also persist the session on disk so that you
* can access the user using {@link #getCurrentUser}.
*
* A username and password must be set before calling signUp.
*/
parseUser.signUpInBackground(e => {
    // optional callback
});
```

#### Methods in Parse.() scope, related to ParseUser instances
```js
// This retrieves the currently logged in ParseUser with a valid session, either from memory or disk if necessary.
Parse.getCurrentUser();

/**
* Enables automatic creation of anonymous users. After calling this method,
* getCurrentUser() will always have a value. The user will only be created on the server once
* the user has been saved, or once an object with a relation to that user or an ACL that refers
* to the user has been saved.
*/
Parse.enableAutomaticUser();

// if no ParseUser is passed in arguments, it will consider the Parse.getCurrentUser();
Parse.isLinked(parseUser);

// Logs in a user with a username and password. On success, this saves the session to disk, so
// you can retrieve the currently logged in user using {@link #getCurrentUser}.
Parse.loginInBackground('username', 'password', e => {
    // Mandatory callback
    // e.parseUser = logged-in Parse.createParseUser() instance
});

// Logs out the currently logged in user session. This will remove the session from disk, log
//out of linked services, and future calls to getCurrentUser() will be null     
Parse.logOutInBackground(e => {
    // Mandatory callback
    // e.parseUser = null
});

// Creates an anonymous user in the background (https://docs.parseplatform.org/android/guide/#anonymous-users)
Parse.anonymousLogIn(e => {
    // Mandatory callback
    // e.parseUser = anonymous logged-in user
});

// Authorize a user with a session token. On success, this saves the session to disk, so you can
// retrieve the currently logged in user using getCurrentUser().    
Parse.becomeInBackground('session-token', e => {
    // Mandatory callback
    // e.parseUser = logged-in Parse.createParseUser() instance
});
```

### ParseUserQuery
##### (similar to ParseQuery class, but for ParseUser, not ParseObject)
##### https://docs.parseplatform.org/android/guide/#querying
```js
const parseUserQuery = Parse.createParseUserQuery({'className': 'parse-user-query'});

// execute same methods as ParseQuery class, but replace all ParseObject instances with ParseUser instances
```

