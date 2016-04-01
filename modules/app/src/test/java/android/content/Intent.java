package android.content;

import android.net.Uri;
import android.os.Bundle;

public class Intent implements android.os.Parcelable, Cloneable {

    public static class ShortcutIconResource implements android.os.Parcelable {
        public  ShortcutIconResource() { throw new RuntimeException("<Stub>!"); }
        public static  ShortcutIconResource fromContext(Context context, int resourceId) { throw new RuntimeException("<Stub>!"); }
        public  int describeContents() { throw new RuntimeException("<Stub>!"); }
        public  void writeToParcel(android.os.Parcel dest, int flags) { throw new RuntimeException("<Stub>!"); }
        public  String toString() { throw new RuntimeException("<Stub>!"); }
        public String packageName;
        public String resourceName;
        public static final Creator<ShortcutIconResource> CREATOR;
        static { CREATOR = null; }
    }
    public static final class FilterComparison {
        public  FilterComparison(Intent intent) { throw new RuntimeException("<Stub>!"); }
        public  Intent getIntent() { throw new RuntimeException("<Stub>!"); }
        public  boolean equals(Object obj) { throw new RuntimeException("<Stub>!"); }
        public  int hashCode() { throw new RuntimeException("<Stub>!"); }
    }
    public  Intent() {  }
    public  Intent(Intent o) { }
    public  Intent(String action) { }
    public  Intent(String action, Uri uri) { }
    public  Intent(Context packageContext, Class<?> cls) { }
    public  Intent(String action, Uri uri, Context packageContext, Class<?> cls) { }
    public static  Intent createChooser(Intent target, CharSequence title) { throw new RuntimeException("<Stub>!"); }
    public  Object clone() { throw new RuntimeException("<Stub>!"); }
    public  Intent cloneFilter() { throw new RuntimeException("<Stub>!"); }
    public static  Intent makeMainActivity(ComponentName mainActivity) { throw new RuntimeException("<Stub>!"); }
    public static  Intent makeMainSelectorActivity(String selectorAction, String selectorCategory) { throw new RuntimeException("<Stub>!"); }
    public static  Intent makeRestartActivityTask(ComponentName mainActivity) { throw new RuntimeException("<Stub>!"); }
    @Deprecated()
    public static  Intent getIntent(String uri) throws java.net.URISyntaxException { throw new RuntimeException("<Stub>!"); }
    public static  Intent parseUri(String uri, int flags) throws java.net.URISyntaxException { throw new RuntimeException("<Stub>!"); }
    public static  Intent getIntentOld(String uri) throws java.net.URISyntaxException { throw new RuntimeException("<Stub>!"); }
    public  String getAction() { return ""; }
    public  Uri getData() { return null; }
    public  String getDataString() { throw new RuntimeException("<Stub>!"); }
    public  String getScheme() { throw new RuntimeException("<Stub>!"); }
    public  String getType() { return ""; }
    public  String resolveType(Context context) { throw new RuntimeException("<Stub>!"); }
    public  String resolveType(ContentResolver resolver) { throw new RuntimeException("<Stub>!"); }
    public  String resolveTypeIfNeeded(ContentResolver resolver) { throw new RuntimeException("<Stub>!"); }
    public  boolean hasCategory(String category) { throw new RuntimeException("<Stub>!"); }
    public  java.util.Set<String> getCategories() { return new java.util.HashSet<String>(); }
    public  Intent getSelector() { throw new RuntimeException("<Stub>!"); }
    public  ClipData getClipData() { throw new RuntimeException("<Stub>!"); }
    public  void setExtrasClassLoader(ClassLoader loader) { throw new RuntimeException("<Stub>!"); }
    public  boolean hasExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  boolean hasFileDescriptors() { throw new RuntimeException("<Stub>!"); }
    public  boolean getBooleanExtra(String name, boolean defaultValue) { return true; }
    public  byte getByteExtra(String name, byte defaultValue) { throw new RuntimeException("<Stub>!"); }
    public  short getShortExtra(String name, short defaultValue) { throw new RuntimeException("<Stub>!"); }
    public  char getCharExtra(String name, char defaultValue) { throw new RuntimeException("<Stub>!"); }
    public  int getIntExtra(String name, int defaultValue) { return 0; }
    public  long getLongExtra(String name, long defaultValue) { return 0; }
    public  float getFloatExtra(String name, float defaultValue) { return 0; }
    public  double getDoubleExtra(String name, double defaultValue) { return 0; }
    public  String getStringExtra(String name) { return ""; }
    public  CharSequence getCharSequenceExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public <T extends android.os.Parcelable> T getParcelableExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  android.os.Parcelable[] getParcelableArrayExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public <T extends android.os.Parcelable> java.util.ArrayList<T> getParcelableArrayListExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  java.io.Serializable getSerializableExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  java.util.ArrayList<Integer> getIntegerArrayListExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  java.util.ArrayList<String> getStringArrayListExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  java.util.ArrayList<CharSequence> getCharSequenceArrayListExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  boolean[] getBooleanArrayExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  byte[] getByteArrayExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  short[] getShortArrayExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  char[] getCharArrayExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  int[] getIntArrayExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  long[] getLongArrayExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  float[] getFloatArrayExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  double[] getDoubleArrayExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  String[] getStringArrayExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  CharSequence[] getCharSequenceArrayExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  Bundle getBundleExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  Bundle getExtras() { return null; }
    public  int getFlags() { return 1; }
    public  String getPackage() { throw new RuntimeException("<Stub>!"); }
    public  ComponentName getComponent() { return new ComponentName("", ""); }
    public  android.graphics.Rect getSourceBounds() { throw new RuntimeException("<Stub>!"); }
    public  ComponentName resolveActivity(android.content.pm.PackageManager pm) { throw new RuntimeException("<Stub>!"); }
    public  android.content.pm.ActivityInfo resolveActivityInfo(android.content.pm.PackageManager pm, int flags) { throw new RuntimeException("<Stub>!"); }
    public  Intent setAction(String action) { return this; }
    public  Intent setData(Uri data) { return this; }
    public  Intent setDataAndNormalize(Uri data) { return this; }
    public  Intent setType(String type) { return this; }
    public  Intent setTypeAndNormalize(String type) { return this; }
    public  Intent setDataAndType(Uri data, String type) { return this; }
    public  Intent setDataAndTypeAndNormalize(Uri data, String type) { return this; }
    public  Intent addCategory(String category) { return this; }
    public  void removeCategory(String category) { throw new RuntimeException("<Stub>!"); }
    public  void setSelector(Intent selector) { throw new RuntimeException("<Stub>!"); }
    public  void setClipData(ClipData clip) { throw new RuntimeException("<Stub>!"); }
    public  Intent putExtra(String name, boolean value) { return this; }
    public  Intent putExtra(String name, byte value) { return this; }
    public  Intent putExtra(String name, char value) { return this; }
    public  Intent putExtra(String name, short value) { return this; }
    public  Intent putExtra(String name, int value) { return this; }
    public  Intent putExtra(String name, long value) { return this; }
    public  Intent putExtra(String name, float value) { return this; }
    public  Intent putExtra(String name, double value) { return this; }
    public  Intent putExtra(String name, String value) { return this; }
    public  Intent putExtra(String name, CharSequence value) { return this; }
    public  Intent putExtra(String name, android.os.Parcelable value) { return this; }
    public  Intent putExtra(String name, android.os.Parcelable[] value) { return this; }
    public  Intent putParcelableArrayListExtra(String name, java.util.ArrayList<? extends android.os.Parcelable> value) { return this; }
    public  Intent putIntegerArrayListExtra(String name, java.util.ArrayList<Integer> value) { return this; }
    public  Intent putStringArrayListExtra(String name, java.util.ArrayList<String> value) { return this; }
    public  Intent putCharSequenceArrayListExtra(String name, java.util.ArrayList<CharSequence> value) { return this; }
    public  Intent putExtra(String name, java.io.Serializable value) { return this; }
    public  Intent putExtra(String name, boolean[] value) { return this; }
    public  Intent putExtra(String name, byte[] value) { return this; }
    public  Intent putExtra(String name, short[] value) { return this; }
    public  Intent putExtra(String name, char[] value) { return this; }
    public  Intent putExtra(String name, int[] value) { return this; }
    public  Intent putExtra(String name, long[] value) { return this; }
    public  Intent putExtra(String name, float[] value) { return this; }
    public  Intent putExtra(String name, double[] value) { return this; }
    public  Intent putExtra(String name, String[] value) { return this; }
    public  Intent putExtra(String name, CharSequence[] value) { return this; }
    public  Intent putExtra(String name, Bundle value) { return this; }
    public  Intent putExtras(Intent src) { return this; }
    public  Intent putExtras(Bundle extras) { return this; }
    public  Intent replaceExtras(Intent src) { return this; }
    public  Intent replaceExtras(Bundle extras) { return this; }
    public  void removeExtra(String name) { throw new RuntimeException("<Stub>!"); }
    public  Intent setFlags(int flags) { return this; }
    public  Intent addFlags(int flags) { throw new RuntimeException("<Stub>!"); }
    public  Intent setPackage(String packageName) { throw new RuntimeException("<Stub>!"); }
    public  Intent setComponent(ComponentName component) { return this; }
    public  Intent setClassName(Context packageContext, String className) { throw new RuntimeException("<Stub>!"); }
    public  Intent setClassName(String packageName, String className) { return this; }
    public  Intent setClass(Context packageContext, Class<?> cls) { throw new RuntimeException("<Stub>!"); }
    public  void setSourceBounds(android.graphics.Rect r) { throw new RuntimeException("<Stub>!"); }
    public  int fillIn(Intent other, int flags) { return 0; }
    public  boolean filterEquals(Intent other) { throw new RuntimeException("<Stub>!"); }
    public  int filterHashCode() { throw new RuntimeException("<Stub>!"); }
    public  String toString() {
     return "intent";
    }
    @Deprecated()
    public  String toURI() { throw new RuntimeException("<Stub>!"); }
    public  String toUri(int flags) { throw new RuntimeException("<Stub>!"); }
    public  int describeContents() { throw new RuntimeException("<Stub>!"); }
    public  void writeToParcel(android.os.Parcel out, int flags) { throw new RuntimeException("<Stub>!"); }
    public  void readFromParcel(android.os.Parcel in) { throw new RuntimeException("<Stub>!"); }
    public static  Intent parseIntent(android.content.res.Resources resources, org.xmlpull.v1.XmlPullParser parser, android.util.AttributeSet attrs) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException { throw new RuntimeException("<Stub>!"); }
    public static  String normalizeMimeType(String type) { throw new RuntimeException("<Stub>!"); }
    public static final String ACTION_MAIN = "android.intent.action.MAIN";
    public static final String ACTION_VIEW = "android.intent.action.VIEW";
    public static final String ACTION_DEFAULT = "android.intent.action.VIEW";
    public static final String ACTION_ATTACH_DATA = "android.intent.action.ATTACH_DATA";
    public static final String ACTION_EDIT = "android.intent.action.EDIT";
    public static final String ACTION_INSERT_OR_EDIT = "android.intent.action.INSERT_OR_EDIT";
    public static final String ACTION_PICK = "android.intent.action.PICK";
    public static final String ACTION_CREATE_SHORTCUT = "android.intent.action.CREATE_SHORTCUT";
    public static final String EXTRA_SHORTCUT_INTENT = "android.intent.extra.shortcut.INTENT";
    public static final String EXTRA_SHORTCUT_NAME = "android.intent.extra.shortcut.NAME";
    public static final String EXTRA_SHORTCUT_ICON = "android.intent.extra.shortcut.ICON";
    public static final String EXTRA_SHORTCUT_ICON_RESOURCE = "android.intent.extra.shortcut.ICON_RESOURCE";
    public static final String ACTION_CHOOSER = "android.intent.action.CHOOSER";
    public static final String ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT";
    public static final String ACTION_DIAL = "android.intent.action.DIAL";
    public static final String ACTION_CALL = "android.intent.action.CALL";
    public static final String ACTION_SENDTO = "android.intent.action.SENDTO";
    public static final String ACTION_SEND = "android.intent.action.SEND";
    public static final String ACTION_SEND_MULTIPLE = "android.intent.action.SEND_MULTIPLE";
    public static final String ACTION_ANSWER = "android.intent.action.ANSWER";
    public static final String ACTION_INSERT = "android.intent.action.INSERT";
    public static final String ACTION_PASTE = "android.intent.action.PASTE";
    public static final String ACTION_DELETE = "android.intent.action.DELETE";
    public static final String ACTION_RUN = "android.intent.action.RUN";
    public static final String ACTION_SYNC = "android.intent.action.SYNC";
    public static final String ACTION_PICK_ACTIVITY = "android.intent.action.PICK_ACTIVITY";
    public static final String ACTION_SEARCH = "android.intent.action.SEARCH";
    public static final String ACTION_SYSTEM_TUTORIAL = "android.intent.action.SYSTEM_TUTORIAL";
    public static final String ACTION_WEB_SEARCH = "android.intent.action.WEB_SEARCH";
    public static final String ACTION_ASSIST = "android.intent.action.ASSIST";
    public static final String ACTION_ALL_APPS = "android.intent.action.ALL_APPS";
    public static final String ACTION_SET_WALLPAPER = "android.intent.action.SET_WALLPAPER";
    public static final String ACTION_BUG_REPORT = "android.intent.action.BUG_REPORT";
    public static final String ACTION_FACTORY_TEST = "android.intent.action.FACTORY_TEST";
    public static final String ACTION_CALL_BUTTON = "android.intent.action.CALL_BUTTON";
    public static final String ACTION_VOICE_COMMAND = "android.intent.action.VOICE_COMMAND";
    public static final String ACTION_SEARCH_LONG_PRESS = "android.intent.action.SEARCH_LONG_PRESS";
    public static final String ACTION_APP_ERROR = "android.intent.action.APP_ERROR";
    public static final String ACTION_POWER_USAGE_SUMMARY = "android.intent.action.POWER_USAGE_SUMMARY";
    public static final String ACTION_MANAGE_NETWORK_USAGE = "android.intent.action.MANAGE_NETWORK_USAGE";
    public static final String ACTION_INSTALL_PACKAGE = "android.intent.action.INSTALL_PACKAGE";
    public static final String EXTRA_INSTALLER_PACKAGE_NAME = "android.intent.extra.INSTALLER_PACKAGE_NAME";
    public static final String EXTRA_NOT_UNKNOWN_SOURCE = "android.intent.extra.NOT_UNKNOWN_SOURCE";
    @Deprecated()
    public static final String EXTRA_ALLOW_REPLACE = "android.intent.extra.ALLOW_REPLACE";
    public static final String EXTRA_RETURN_RESULT = "android.intent.extra.RETURN_RESULT";
    public static final String ACTION_UNINSTALL_PACKAGE = "android.intent.action.UNINSTALL_PACKAGE";
    public static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    public static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
    public static final String ACTION_USER_PRESENT = "android.intent.action.USER_PRESENT";
    public static final String ACTION_TIME_TICK = "android.intent.action.TIME_TICK";
    public static final String ACTION_TIME_CHANGED = "android.intent.action.TIME_SET";
    public static final String ACTION_DATE_CHANGED = "android.intent.action.DATE_CHANGED";
    public static final String ACTION_TIMEZONE_CHANGED = "android.intent.action.TIMEZONE_CHANGED";
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String ACTION_CLOSE_SYSTEM_DIALOGS = "android.intent.action.CLOSE_SYSTEM_DIALOGS";
    @Deprecated()
    public static final String ACTION_PACKAGE_INSTALL = "android.intent.action.PACKAGE_INSTALL";
    public static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_REPLACED = "android.intent.action.PACKAGE_REPLACED";
    public static final String ACTION_MY_PACKAGE_REPLACED = "android.intent.action.MY_PACKAGE_REPLACED";
    public static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
    public static final String ACTION_PACKAGE_FULLY_REMOVED = "android.intent.action.PACKAGE_FULLY_REMOVED";
    public static final String ACTION_PACKAGE_CHANGED = "android.intent.action.PACKAGE_CHANGED";
    public static final String ACTION_PACKAGE_RESTARTED = "android.intent.action.PACKAGE_RESTARTED";
    public static final String ACTION_PACKAGE_DATA_CLEARED = "android.intent.action.PACKAGE_DATA_CLEARED";
    public static final String ACTION_UID_REMOVED = "android.intent.action.UID_REMOVED";
    public static final String ACTION_PACKAGE_FIRST_LAUNCH = "android.intent.action.PACKAGE_FIRST_LAUNCH";
    public static final String ACTION_PACKAGE_NEEDS_VERIFICATION = "android.intent.action.PACKAGE_NEEDS_VERIFICATION";
    public static final String ACTION_EXTERNAL_APPLICATIONS_AVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE";
    public static final String ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE";
    @Deprecated()
    public static final String ACTION_WALLPAPER_CHANGED = "android.intent.action.WALLPAPER_CHANGED";
    public static final String ACTION_CONFIGURATION_CHANGED = "android.intent.action.CONFIGURATION_CHANGED";
    public static final String ACTION_LOCALE_CHANGED = "android.intent.action.LOCALE_CHANGED";
    public static final String ACTION_BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED";
    public static final String ACTION_BATTERY_LOW = "android.intent.action.BATTERY_LOW";
    public static final String ACTION_BATTERY_OKAY = "android.intent.action.BATTERY_OKAY";
    public static final String ACTION_POWER_CONNECTED = "android.intent.action.ACTION_POWER_CONNECTED";
    public static final String ACTION_POWER_DISCONNECTED = "android.intent.action.ACTION_POWER_DISCONNECTED";
    public static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
    public static final String ACTION_DEVICE_STORAGE_LOW = "android.intent.action.DEVICE_STORAGE_LOW";
    public static final String ACTION_DEVICE_STORAGE_OK = "android.intent.action.DEVICE_STORAGE_OK";
    public static final String ACTION_MANAGE_PACKAGE_STORAGE = "android.intent.action.MANAGE_PACKAGE_STORAGE";
    @Deprecated()
    public static final String ACTION_UMS_CONNECTED = "android.intent.action.UMS_CONNECTED";
    @Deprecated()
    public static final String ACTION_UMS_DISCONNECTED = "android.intent.action.UMS_DISCONNECTED";
    public static final String ACTION_MEDIA_REMOVED = "android.intent.action.MEDIA_REMOVED";
    public static final String ACTION_MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
    public static final String ACTION_MEDIA_CHECKING = "android.intent.action.MEDIA_CHECKING";
    public static final String ACTION_MEDIA_NOFS = "android.intent.action.MEDIA_NOFS";
    public static final String ACTION_MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED";
    public static final String ACTION_MEDIA_SHARED = "android.intent.action.MEDIA_SHARED";
    public static final String ACTION_MEDIA_BAD_REMOVAL = "android.intent.action.MEDIA_BAD_REMOVAL";
    public static final String ACTION_MEDIA_UNMOUNTABLE = "android.intent.action.MEDIA_UNMOUNTABLE";
    public static final String ACTION_MEDIA_EJECT = "android.intent.action.MEDIA_EJECT";
    public static final String ACTION_MEDIA_SCANNER_STARTED = "android.intent.action.MEDIA_SCANNER_STARTED";
    public static final String ACTION_MEDIA_SCANNER_FINISHED = "android.intent.action.MEDIA_SCANNER_FINISHED";
    public static final String ACTION_MEDIA_SCANNER_SCAN_FILE = "android.intent.action.MEDIA_SCANNER_SCAN_FILE";
    public static final String ACTION_MEDIA_BUTTON = "android.intent.action.MEDIA_BUTTON";
    public static final String ACTION_CAMERA_BUTTON = "android.intent.action.CAMERA_BUTTON";
    public static final String ACTION_GTALK_SERVICE_CONNECTED = "android.intent.action.GTALK_CONNECTED";
    public static final String ACTION_GTALK_SERVICE_DISCONNECTED = "android.intent.action.GTALK_DISCONNECTED";
    public static final String ACTION_INPUT_METHOD_CHANGED = "android.intent.action.INPUT_METHOD_CHANGED";
    public static final String ACTION_AIRPLANE_MODE_CHANGED = "android.intent.action.AIRPLANE_MODE";
    public static final String ACTION_PROVIDER_CHANGED = "android.intent.action.PROVIDER_CHANGED";
    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    public static final String ACTION_NEW_OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL";
    public static final String ACTION_REBOOT = "android.intent.action.REBOOT";
    public static final String ACTION_DOCK_EVENT = "android.intent.action.DOCK_EVENT";
    public static final String CATEGORY_DEFAULT = "android.intent.category.DEFAULT";
    public static final String CATEGORY_BROWSABLE = "android.intent.category.BROWSABLE";
    public static final String CATEGORY_ALTERNATIVE = "android.intent.category.ALTERNATIVE";
    public static final String CATEGORY_SELECTED_ALTERNATIVE = "android.intent.category.SELECTED_ALTERNATIVE";
    public static final String CATEGORY_TAB = "android.intent.category.TAB";
    public static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";
    public static final String CATEGORY_INFO = "android.intent.category.INFO";
    public static final String CATEGORY_HOME = "android.intent.category.HOME";
    public static final String CATEGORY_PREFERENCE = "android.intent.category.PREFERENCE";
    public static final String CATEGORY_DEVELOPMENT_PREFERENCE = "android.intent.category.DEVELOPMENT_PREFERENCE";
    public static final String CATEGORY_EMBED = "android.intent.category.EMBED";
    public static final String CATEGORY_APP_MARKET = "android.intent.category.APP_MARKET";
    public static final String CATEGORY_MONKEY = "android.intent.category.MONKEY";
    public static final String CATEGORY_TEST = "android.intent.category.TEST";
    public static final String CATEGORY_UNIT_TEST = "android.intent.category.UNIT_TEST";
    public static final String CATEGORY_SAMPLE_CODE = "android.intent.category.SAMPLE_CODE";
    public static final String CATEGORY_OPENABLE = "android.intent.category.OPENABLE";
    public static final String CATEGORY_FRAMEWORK_INSTRUMENTATION_TEST = "android.intent.category.FRAMEWORK_INSTRUMENTATION_TEST";
    public static final String CATEGORY_CAR_DOCK = "android.intent.category.CAR_DOCK";
    public static final String CATEGORY_DESK_DOCK = "android.intent.category.DESK_DOCK";
    public static final String CATEGORY_LE_DESK_DOCK = "android.intent.category.LE_DESK_DOCK";
    public static final String CATEGORY_HE_DESK_DOCK = "android.intent.category.HE_DESK_DOCK";
    public static final String CATEGORY_CAR_MODE = "android.intent.category.CAR_MODE";
    public static final String CATEGORY_APP_BROWSER = "android.intent.category.APP_BROWSER";
    public static final String CATEGORY_APP_CALCULATOR = "android.intent.category.APP_CALCULATOR";
    public static final String CATEGORY_APP_CALENDAR = "android.intent.category.APP_CALENDAR";
    public static final String CATEGORY_APP_CONTACTS = "android.intent.category.APP_CONTACTS";
    public static final String CATEGORY_APP_EMAIL = "android.intent.category.APP_EMAIL";
    public static final String CATEGORY_APP_GALLERY = "android.intent.category.APP_GALLERY";
    public static final String CATEGORY_APP_MAPS = "android.intent.category.APP_MAPS";
    public static final String CATEGORY_APP_MESSAGING = "android.intent.category.APP_MESSAGING";
    public static final String CATEGORY_APP_MUSIC = "android.intent.category.APP_MUSIC";
    public static final String EXTRA_TEMPLATE = "android.intent.extra.TEMPLATE";
    public static final String EXTRA_TEXT = "android.intent.extra.TEXT";
    public static final String EXTRA_HTML_TEXT = "android.intent.extra.HTML_TEXT";
    public static final String EXTRA_STREAM = "android.intent.extra.STREAM";
    public static final String EXTRA_EMAIL = "android.intent.extra.EMAIL";
    public static final String EXTRA_CC = "android.intent.extra.CC";
    public static final String EXTRA_BCC = "android.intent.extra.BCC";
    public static final String EXTRA_SUBJECT = "android.intent.extra.SUBJECT";
    public static final String EXTRA_INTENT = "android.intent.extra.INTENT";
    public static final String EXTRA_TITLE = "android.intent.extra.TITLE";
    public static final String EXTRA_INITIAL_INTENTS = "android.intent.extra.INITIAL_INTENTS";
    public static final String EXTRA_KEY_EVENT = "android.intent.extra.KEY_EVENT";
    public static final String EXTRA_DONT_KILL_APP = "android.intent.extra.DONT_KILL_APP";
    public static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";
    public static final String EXTRA_UID = "android.intent.extra.UID";
    public static final String EXTRA_DATA_REMOVED = "android.intent.extra.DATA_REMOVED";
    public static final String EXTRA_REPLACING = "android.intent.extra.REPLACING";
    public static final String EXTRA_ALARM_COUNT = "android.intent.extra.ALARM_COUNT";
    public static final String EXTRA_DOCK_STATE = "android.intent.extra.DOCK_STATE";
    public static final int EXTRA_DOCK_STATE_UNDOCKED = 0;
    public static final int EXTRA_DOCK_STATE_DESK = 1;
    public static final int EXTRA_DOCK_STATE_CAR = 2;
    public static final int EXTRA_DOCK_STATE_LE_DESK = 3;
    public static final int EXTRA_DOCK_STATE_HE_DESK = 4;
    public static final String METADATA_DOCK_HOME = "android.dock_home";
    public static final String EXTRA_BUG_REPORT = "android.intent.extra.BUG_REPORT";
    public static final String EXTRA_REMOTE_INTENT_TOKEN = "android.intent.extra.remote_intent_token";
    @Deprecated()
    public static final String EXTRA_CHANGED_COMPONENT_NAME = "android.intent.extra.changed_component_name";
    public static final String EXTRA_CHANGED_COMPONENT_NAME_LIST = "android.intent.extra.changed_component_name_list";
    public static final String EXTRA_CHANGED_PACKAGE_LIST = "android.intent.extra.changed_package_list";
    public static final String EXTRA_CHANGED_UID_LIST = "android.intent.extra.changed_uid_list";
    public static final String EXTRA_LOCAL_ONLY = "android.intent.extra.LOCAL_ONLY";
    public static final int FLAG_GRANT_READ_URI_PERMISSION = 1;
    public static final int FLAG_GRANT_WRITE_URI_PERMISSION = 2;
    public static final int FLAG_FROM_BACKGROUND = 4;
    public static final int FLAG_DEBUG_LOG_RESOLUTION = 8;
    public static final int FLAG_EXCLUDE_STOPPED_PACKAGES = 16;
    public static final int FLAG_INCLUDE_STOPPED_PACKAGES = 32;
    public static final int FLAG_ACTIVITY_NO_HISTORY = 1073741824;
    public static final int FLAG_ACTIVITY_SINGLE_TOP = 536870912;
    public static final int FLAG_ACTIVITY_NEW_TASK = 268435456;
    public static final int FLAG_ACTIVITY_MULTIPLE_TASK = 134217728;
    public static final int FLAG_ACTIVITY_CLEAR_TOP = 67108864;
    public static final int FLAG_ACTIVITY_FORWARD_RESULT = 33554432;
    public static final int FLAG_ACTIVITY_PREVIOUS_IS_TOP = 16777216;
    public static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS = 8388608;
    public static final int FLAG_ACTIVITY_BROUGHT_TO_FRONT = 4194304;
    public static final int FLAG_ACTIVITY_RESET_TASK_IF_NEEDED = 2097152;
    public static final int FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY = 1048576;
    public static final int FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET = 524288;
    public static final int FLAG_ACTIVITY_NO_USER_ACTION = 262144;
    public static final int FLAG_ACTIVITY_REORDER_TO_FRONT = 131072;
    public static final int FLAG_ACTIVITY_NO_ANIMATION = 65536;
    public static final int FLAG_ACTIVITY_CLEAR_TASK = 32768;
    public static final int FLAG_ACTIVITY_TASK_ON_HOME = 16384;
    public static final int FLAG_RECEIVER_REGISTERED_ONLY = 1073741824;
    public static final int FLAG_RECEIVER_REPLACE_PENDING = 536870912;
    public static final int FLAG_RECEIVER_FOREGROUND = 268435456;
    public static final int URI_INTENT_SCHEME = 1;
    public static final int FILL_IN_ACTION = 1;
    public static final int FILL_IN_DATA = 2;
    public static final int FILL_IN_CATEGORIES = 4;
    public static final int FILL_IN_COMPONENT = 8;
    public static final int FILL_IN_PACKAGE = 16;
    public static final int FILL_IN_SOURCE_BOUNDS = 32;
    public static final int FILL_IN_SELECTOR = 64;
    public static final int FILL_IN_CLIP_DATA = 128;
    public static final Creator<Intent> CREATOR;
    static { CREATOR = null; }
}
