package android.content;
public abstract class ContentResolver
{
    public  ContentResolver(android.content.Context context) { throw new RuntimeException("Stub!"); }
    public   java.lang.String getType(android.net.Uri url) { throw new RuntimeException("Stub!"); }
    public  java.lang.String[] getStreamTypes(android.net.Uri url, java.lang.String mimeTypeFilter) { throw new RuntimeException("Stub!"); }
    public   android.database.Cursor query(android.net.Uri uri, java.lang.String[] projection, java.lang.String selection, java.lang.String[] selectionArgs, java.lang.String sortOrder) { throw new RuntimeException("Stub!"); }
    public   android.database.Cursor query(android.net.Uri uri, java.lang.String[] projection, java.lang.String selection, java.lang.String[] selectionArgs, java.lang.String sortOrder, android.os.CancellationSignal cancellationSignal) { throw new RuntimeException("Stub!"); }
    public   java.io.InputStream openInputStream(android.net.Uri uri) throws java.io.FileNotFoundException { throw new RuntimeException("Stub!"); }
    public   java.io.OutputStream openOutputStream(android.net.Uri uri) throws java.io.FileNotFoundException { throw new RuntimeException("Stub!"); }
    public   java.io.OutputStream openOutputStream(android.net.Uri uri, java.lang.String mode) throws java.io.FileNotFoundException { throw new RuntimeException("Stub!"); }
    public   android.os.ParcelFileDescriptor openFileDescriptor(android.net.Uri uri, java.lang.String mode) throws java.io.FileNotFoundException { throw new RuntimeException("Stub!"); }
    public   android.content.res.AssetFileDescriptor openAssetFileDescriptor(android.net.Uri uri, java.lang.String mode) throws java.io.FileNotFoundException { throw new RuntimeException("Stub!"); }
    public   android.content.res.AssetFileDescriptor openTypedAssetFileDescriptor(android.net.Uri uri, java.lang.String mimeType, android.os.Bundle opts) throws java.io.FileNotFoundException { throw new RuntimeException("Stub!"); }
    public   android.net.Uri insert(android.net.Uri url, android.content.ContentValues values) { throw new RuntimeException("Stub!"); }
    public  android.content.ContentProviderResult[] applyBatch(java.lang.String authority, java.util.ArrayList<android.content.ContentProviderOperation> operations) throws android.os.RemoteException, android.content.OperationApplicationException { throw new RuntimeException("Stub!"); }
    public   int bulkInsert(android.net.Uri url, android.content.ContentValues[] values) { throw new RuntimeException("Stub!"); }
    public   int delete(android.net.Uri url, java.lang.String where, java.lang.String[] selectionArgs) { throw new RuntimeException("Stub!"); }
    public   int update(android.net.Uri uri, android.content.ContentValues values, java.lang.String where, java.lang.String[] selectionArgs) { throw new RuntimeException("Stub!"); }
    public   android.os.Bundle call(android.net.Uri uri, java.lang.String method, java.lang.String arg, android.os.Bundle extras) { throw new RuntimeException("Stub!"); }
    public   android.content.ContentProviderClient acquireContentProviderClient(android.net.Uri uri) { throw new RuntimeException("Stub!"); }
    public   android.content.ContentProviderClient acquireContentProviderClient(java.lang.String name) { throw new RuntimeException("Stub!"); }
    public   android.content.ContentProviderClient acquireUnstableContentProviderClient(android.net.Uri uri) { throw new RuntimeException("Stub!"); }
    public   android.content.ContentProviderClient acquireUnstableContentProviderClient(java.lang.String name) { throw new RuntimeException("Stub!"); }
    public   void registerContentObserver(android.net.Uri uri, boolean notifyForDescendents, android.database.ContentObserver observer) {  }
    public   void unregisterContentObserver(android.database.ContentObserver observer) {  }
    public  void notifyChange(android.net.Uri uri, android.database.ContentObserver observer) { throw new RuntimeException("Stub!"); }
    public  void notifyChange(android.net.Uri uri, android.database.ContentObserver observer, boolean syncToNetwork) { throw new RuntimeException("Stub!"); }
    @java.lang.Deprecated()
    public  void startSync(android.net.Uri uri, android.os.Bundle extras) { throw new RuntimeException("Stub!"); }
    public static  void requestSync(android.accounts.Account account, java.lang.String authority, android.os.Bundle extras) { throw new RuntimeException("Stub!"); }
    public static  void validateSyncExtrasBundle(android.os.Bundle extras) { throw new RuntimeException("Stub!"); }
    @java.lang.Deprecated()
    public  void cancelSync(android.net.Uri uri) { throw new RuntimeException("Stub!"); }
    public static  void cancelSync(android.accounts.Account account, java.lang.String authority) { throw new RuntimeException("Stub!"); }
    public static  android.content.SyncAdapterType[] getSyncAdapterTypes() { throw new RuntimeException("Stub!"); }
    public static  boolean getSyncAutomatically(android.accounts.Account account, java.lang.String authority) { throw new RuntimeException("Stub!"); }
    public static  void setSyncAutomatically(android.accounts.Account account, java.lang.String authority, boolean sync) { throw new RuntimeException("Stub!"); }
    public static  void addPeriodicSync(android.accounts.Account account, java.lang.String authority, android.os.Bundle extras, long pollFrequency) { throw new RuntimeException("Stub!"); }
    public static  void removePeriodicSync(android.accounts.Account account, java.lang.String authority, android.os.Bundle extras) { throw new RuntimeException("Stub!"); }
    public static  java.util.List<android.content.PeriodicSync> getPeriodicSyncs(android.accounts.Account account, java.lang.String authority) { throw new RuntimeException("Stub!"); }
    public static  int getIsSyncable(android.accounts.Account account, java.lang.String authority) { throw new RuntimeException("Stub!"); }
    public static  void setIsSyncable(android.accounts.Account account, java.lang.String authority, int syncable) { throw new RuntimeException("Stub!"); }
    public static  boolean getMasterSyncAutomatically() { throw new RuntimeException("Stub!"); }
    public static  void setMasterSyncAutomatically(boolean sync) { throw new RuntimeException("Stub!"); }
    public static  boolean isSyncActive(android.accounts.Account account, java.lang.String authority) { throw new RuntimeException("Stub!"); }
    @java.lang.Deprecated()
    public static  android.content.SyncInfo getCurrentSync() { throw new RuntimeException("Stub!"); }
    public static  java.util.List<android.content.SyncInfo> getCurrentSyncs() { throw new RuntimeException("Stub!"); }
    public static  boolean isSyncPending(android.accounts.Account account, java.lang.String authority) { throw new RuntimeException("Stub!"); }
    public static  java.lang.Object addStatusChangeListener(int mask, android.content.SyncStatusObserver callback) { throw new RuntimeException("Stub!"); }
    public static  void removeStatusChangeListener(java.lang.Object handle) { throw new RuntimeException("Stub!"); }
    @java.lang.Deprecated()
    public static final java.lang.String SYNC_EXTRAS_ACCOUNT = "account";
    public static final java.lang.String SYNC_EXTRAS_EXPEDITED = "expedited";
    @java.lang.Deprecated()
    public static final java.lang.String SYNC_EXTRAS_FORCE = "force";
    public static final java.lang.String SYNC_EXTRAS_IGNORE_SETTINGS = "ignore_settings";
    public static final java.lang.String SYNC_EXTRAS_IGNORE_BACKOFF = "ignore_backoff";
    public static final java.lang.String SYNC_EXTRAS_DO_NOT_RETRY = "do_not_retry";
    public static final java.lang.String SYNC_EXTRAS_MANUAL = "force";
    public static final java.lang.String SYNC_EXTRAS_UPLOAD = "upload";
    public static final java.lang.String SYNC_EXTRAS_OVERRIDE_TOO_MANY_DELETIONS = "deletions_override";
    public static final java.lang.String SYNC_EXTRAS_DISCARD_LOCAL_DELETIONS = "discard_deletions";
    public static final java.lang.String SYNC_EXTRAS_INITIALIZE = "initialize";
    public static final java.lang.String SCHEME_CONTENT = "content";
    public static final java.lang.String SCHEME_ANDROID_RESOURCE = "android.resource";
    public static final java.lang.String SCHEME_FILE = "file";
    public static final java.lang.String CURSOR_ITEM_BASE_TYPE = "vnd.android.cursor.item";
    public static final java.lang.String CURSOR_DIR_BASE_TYPE = "vnd.android.cursor.dir";
    public static final int SYNC_OBSERVER_TYPE_SETTINGS = 1;
    public static final int SYNC_OBSERVER_TYPE_PENDING = 2;
    public static final int SYNC_OBSERVER_TYPE_ACTIVE = 4;
}
