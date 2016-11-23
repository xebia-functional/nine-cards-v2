package android.os;

import android.util.SparseArray;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
public class Bundle implements Parcelable, Cloneable {
public static final Bundle EMPTY = null;
public static final Creator<Bundle> CREATOR = null;
public Bundle() {  }
public Bundle(ClassLoader loader) { throw new RuntimeException("Stub!"); }
public Bundle(int capacity) { throw new RuntimeException("Stub!"); }
public Bundle(Bundle b) { throw new RuntimeException("Stub!"); }
public void setClassLoader(ClassLoader loader) { throw new RuntimeException("Stub!"); }
public ClassLoader getClassLoader() { throw new RuntimeException("Stub!"); }
public Object clone() { throw new RuntimeException("Stub!"); }
public int size() { throw new RuntimeException("Stub!"); }
public boolean isEmpty() { throw new RuntimeException("Stub!"); }
public void clear() { throw new RuntimeException("Stub!"); }
public boolean containsKey(String key) { throw new RuntimeException("Stub!"); }
public Object get(String key) { throw new RuntimeException("Stub!"); }
public void remove(String key) { throw new RuntimeException("Stub!"); }
public void putAll(Bundle map) { throw new RuntimeException("Stub!"); }
public Set<String> keySet() { throw new RuntimeException("Stub!"); }
public boolean hasFileDescriptors() { throw new RuntimeException("Stub!"); }
public void putBoolean(String key, boolean value) { throw new RuntimeException("Stub!"); }
public void putByte(String key, byte value) { throw new RuntimeException("Stub!"); }
public void putChar(String key, char value) { throw new RuntimeException("Stub!"); }
public void putShort(String key, short value) { throw new RuntimeException("Stub!"); }
public void putInt(String key, int value) { }
public void putLong(String key, long value) { throw new RuntimeException("Stub!"); }
public void putFloat(String key, float value) { throw new RuntimeException("Stub!"); }
public void putDouble(String key, double value) { throw new RuntimeException("Stub!"); }
public void putString(String key, String value) { }
public void putCharSequence(String key, CharSequence value) { throw new RuntimeException("Stub!"); }
public void putParcelable(String key, Parcelable value) { throw new RuntimeException("Stub!"); }
public void putParcelableArray(String key, Parcelable[] value) { throw new RuntimeException("Stub!"); }
public void putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) { throw new RuntimeException("Stub!"); }
public void putSparseParcelableArray(String key, SparseArray<? extends Parcelable> value) { throw new RuntimeException("Stub!"); }
public void putIntegerArrayList(String key, ArrayList<Integer> value) { throw new RuntimeException("Stub!"); }
public void putStringArrayList(String key, ArrayList<String> value) { throw new RuntimeException("Stub!"); }
public void putCharSequenceArrayList(String key, ArrayList<CharSequence> value) { throw new RuntimeException("Stub!"); }
public void putSerializable(String key, Serializable value) { throw new RuntimeException("Stub!"); }
public void putBooleanArray(String key, boolean[] value) { throw new RuntimeException("Stub!"); }
public void putByteArray(String key, byte[] value) { throw new RuntimeException("Stub!"); }
public void putShortArray(String key, short[] value) { throw new RuntimeException("Stub!"); }
public void putCharArray(String key, char[] value) { throw new RuntimeException("Stub!"); }
public void putIntArray(String key, int[] value) { throw new RuntimeException("Stub!"); }
public void putLongArray(String key, long[] value) { throw new RuntimeException("Stub!"); }
public void putFloatArray(String key, float[] value) { throw new RuntimeException("Stub!"); }
public void putDoubleArray(String key, double[] value) { throw new RuntimeException("Stub!"); }
public void putStringArray(String key, String[] value) {  }
public void putCharSequenceArray(String key, CharSequence[] value) { throw new RuntimeException("Stub!"); }
public void putBundle(String key, Bundle value) { throw new RuntimeException("Stub!"); }
public boolean getBoolean(String key) { throw new RuntimeException("Stub!"); }
public boolean getBoolean(String key, boolean defaultValue) { throw new RuntimeException("Stub!"); }
public byte getByte(String key) { throw new RuntimeException("Stub!"); }
public Byte getByte(String key, byte defaultValue) { throw new RuntimeException("Stub!"); }
public char getChar(String key) { throw new RuntimeException("Stub!"); }
public char getChar(String key, char defaultValue) { throw new RuntimeException("Stub!"); }
public short getShort(String key) { throw new RuntimeException("Stub!"); }
public short getShort(String key, short defaultValue) { throw new RuntimeException("Stub!"); }
public int getInt(String key) { throw new RuntimeException("Stub!"); }
public int getInt(String key, int defaultValue) { throw new RuntimeException("Stub!"); }
public long getLong(String key) { throw new RuntimeException("Stub!"); }
public long getLong(String key, long defaultValue) { throw new RuntimeException("Stub!"); }
public float getFloat(String key) { throw new RuntimeException("Stub!"); }
public float getFloat(String key, float defaultValue) { throw new RuntimeException("Stub!"); }
public double getDouble(String key) { throw new RuntimeException("Stub!"); }
public double getDouble(String key, double defaultValue) { throw new RuntimeException("Stub!"); }
public String getString(String key) { throw new RuntimeException("Stub!"); }
public String getString(String key, String defaultValue) { throw new RuntimeException("Stub!"); }
public CharSequence getCharSequence(String key) { throw new RuntimeException("Stub!"); }
public CharSequence getCharSequence(String key, CharSequence defaultValue) { throw new RuntimeException("Stub!"); }
public Bundle getBundle(String key) { throw new RuntimeException("Stub!"); }
public <T extends Parcelable> T getParcelable(String key) { throw new RuntimeException("Stub!"); }
public Parcelable[] getParcelableArray(String key) { throw new RuntimeException("Stub!"); }
public <T extends Parcelable> ArrayList<T> getParcelableArrayList(String key) { throw new RuntimeException("Stub!"); }
public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(String key) { throw new RuntimeException("Stub!"); }
public Serializable getSerializable(String key) { throw new RuntimeException("Stub!"); }
public ArrayList<Integer> getIntegerArrayList(String key) { throw new RuntimeException("Stub!"); }
public ArrayList<String> getStringArrayList(String key) { throw new RuntimeException("Stub!"); }
public ArrayList<CharSequence> getCharSequenceArrayList(String key) { throw new RuntimeException("Stub!"); }
public boolean[] getBooleanArray(String key) { throw new RuntimeException("Stub!"); }
public byte[] getByteArray(String key) { throw new RuntimeException("Stub!"); }
public short[] getShortArray(String key) { throw new RuntimeException("Stub!"); }
public char[] getCharArray(String key) { throw new RuntimeException("Stub!"); }
public int[] getIntArray(String key) { throw new RuntimeException("Stub!"); }
public long[] getLongArray(String key) { throw new RuntimeException("Stub!"); }
public float[] getFloatArray(String key) { throw new RuntimeException("Stub!"); }
public double[] getDoubleArray(String key) { throw new RuntimeException("Stub!"); }
public String[] getStringArray(String key) { throw new RuntimeException("Stub!"); }
public CharSequence[] getCharSequenceArray(String key) { throw new RuntimeException("Stub!"); }
public int describeContents() { throw new RuntimeException("Stub!"); }
public void writeToParcel(Parcel parcel, int flags) { throw new RuntimeException("Stub!"); }
public void readFromParcel(Parcel parcel) { throw new RuntimeException("Stub!"); }
public synchronized String toString() { throw new RuntimeException("Stub!"); }
}
