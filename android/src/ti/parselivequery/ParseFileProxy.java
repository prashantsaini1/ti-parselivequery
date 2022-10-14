/*
 * Created by Prashant Saini
 */

package ti.parselivequery;

import com.parse.ParseFile;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiBlob;

import static ti.parselivequery.Constant.PROPERTY_FILE_DATA;
import static ti.parselivequery.Constant.PROPERTY_FILE_NAME;
import static ti.parselivequery.Constant.PROPERTY_FILE_PROGRESS;
import static ti.parselivequery.Util.checkException;
import static ti.parselivequery.Util.checkExceptionForResult;

@SuppressWarnings("unused")
@Kroll.proxy(creatableInModule = TiParselivequeryModule.class)
public class ParseFileProxy extends KrollProxy {
    private String fileName = "";
    private TiBlob fileBlob = null;
    private ParseFile parseFile = null;

    @Override
    public void release() {
        super.release();

        if (fileBlob != null) {
            fileBlob.release();
            fileBlob = null;
        }

        if (parseFile != null) {
            parseFile.cancel();
            parseFile = null;
        }
    }

    @Override
    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);

        if (dict.containsKeyAndNotNull(PROPERTY_FILE_NAME))
            fileName = "" + dict.get(PROPERTY_FILE_NAME);

        if (dict.containsKeyAndNotNull(PROPERTY_FILE_DATA))
            fileBlob = (TiBlob) dict.get(PROPERTY_FILE_DATA);
    }

    @Kroll.getProperty
    public String getFileName() {
        return fileName;
    }

    @Kroll.setProperty
    public void setFileName(String value) {
        fileName = value;
    }

    @Kroll.getProperty
    public TiBlob getFileData() {
        return fileBlob;
    }

    @Kroll.setProperty
    public void setFileData(TiBlob value) {
        fileBlob = value;
    }

    public ParseFile getParseFile() {
        return parseFile;
    }

    // exclusively set ParseFile instance which can then further be used to fetch data or perform other queries
    public void setParseFile(ParseFile parseFile) {
        this.parseFile = parseFile;
        this.fileName = parseFile.getName();
    }

    // invoke this method to create ParseFile object internally once the file-name & blob is available
    @Kroll.method
    public boolean invalidate() {
        // check if all required fields are available to make a new instance of ParseFile
        if (!fileName.isEmpty() && fileBlob != null && fileBlob.getBytes().length != 0) {

            // cancel any ongoing parseFile operation
            if (parseFile != null && parseFile.isDirty()) {
                parseFile.cancel();
            }

            parseFile = new ParseFile(fileName, fileBlob.getBytes());
            return true;
        }

        return false;
    }

    /**
     * Cancels the operations for this {@code ParseFile} if they are still in the task queue.
     * However, if a network request has already been started for an operation, the network request
     * will not be canceled.
     */
    @Kroll.method
    public void cancel() {
        if (parseFile != null) {
            parseFile.cancel();
        }
    }

    /**
     * The filename. Before save is called, this is just the filename given by the user (if any).
     * After save is called, that name gets prefixed with a unique identifier.
     */
    @Kroll.method
    public String getName() {
        if (parseFile != null) {
            return parseFile.getName();
        }

        return "";
    }

    /**
     * This returns the url of the file. It's only available after you save or after you get the
     * file from a ParseObject.
     */
    @Kroll.method
    public String getUrl() {
        if (parseFile != null) {
            return parseFile.getUrl();
        }

        return "";
    }

    /** Whether the file has available data. */
    @Kroll.method
    public boolean isDataAvailable() {
        if (parseFile != null) {
            return parseFile.isDataAvailable();
        }

        return false;
    }

    /** Whether the file still needs to be saved. */
    @Kroll.method
    public boolean isDirty() {
        if (parseFile != null) {
            return parseFile.isDirty();
        }

        return false;
    }

    /**
     * Asynchronously gets the data from cache if available or fetches its content from the network.
     * @param dataCallback is called when the get completes.
     * @param progressCallback is called periodically with progress updates.
     */
    @Kroll.method
    public void getDataInBackground(KrollFunction dataCallback, @Kroll.argument(optional = true) KrollFunction progressCallback) {
        if (parseFile != null) {
            parseFile.getDataInBackground((data, exc) -> {
                KrollDict result = new KrollDict();
                boolean isSuccess = checkException(exc, result);

                if (isSuccess) {
                    fileBlob = TiBlob.blobFromData(data);
                }

                result.put(PROPERTY_FILE_DATA, isSuccess ? fileBlob : null);
                dataCallback.callAsync(krollObject, result);

            }, percentDone -> {
                if (progressCallback != null) {
                    KrollDict result = new KrollDict();
                    result.put(PROPERTY_FILE_PROGRESS, percentDone);
                    progressCallback.callAsync(krollObject, result);
                }
            });
        }
    }

    /**
     * Saves the file to the Parse cloud in a background thread. `progressCallback` is guaranteed to
     * be called with 100 before saveCallback is called.
     * @param saveCallback gets called when the save completes.
     * @param progressCallback is called periodically with progress updates.
     * */
    @Kroll.method
    public void saveDataInBackground(@Kroll.argument(optional = true) KrollFunction saveCallback, @Kroll.argument(optional = true) KrollFunction progressCallback) {
        if (parseFile != null) {
            parseFile.saveInBackground(exc -> {
                if (saveCallback != null) {
                    saveCallback.callAsync(krollObject, checkExceptionForResult(exc));
                }
            }, percentDone -> {
                if (progressCallback != null) {
                    KrollDict result = new KrollDict();
                    result.put(PROPERTY_FILE_PROGRESS, percentDone);
                    progressCallback.callAsync(krollObject, result);
                }
            });
        }
    }
}
