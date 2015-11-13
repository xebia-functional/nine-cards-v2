package android.content;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ComponentName implements Parcelable, Cloneable, Comparable<ComponentName> {
    public static final Creator<ComponentName> CREATOR = null;

    public ComponentName(String pkg, String cls) {
        throw new RuntimeException("Stub!");
    }

    public ComponentName(Context pkg, String cls) {
        throw new RuntimeException("Stub!");
    }

    public ComponentName(Context pkg, Class<?> cls) {
        throw new RuntimeException("Stub!");
    }

    public ComponentName(Parcel in) {
        throw new RuntimeException("Stub!");
    }

    public ComponentName clone() {
        throw new RuntimeException("Stub!");
    }

    public String getPackageName() {
        return "";
    }

    public String getClassName() {
        return "";
    }

    public String getShortClassName() {
        throw new RuntimeException("Stub!");
    }

    public String flattenToString() {
        throw new RuntimeException("Stub!");
    }

    public String flattenToShortString() {
        throw new RuntimeException("Stub!");
    }

    public static ComponentName unflattenFromString(String str) {
        throw new RuntimeException("Stub!");
    }

    public String toShortString() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object obj) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public int compareTo(ComponentName that) {
        throw new RuntimeException("Stub!");
    }

    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    public void writeToParcel(Parcel out, int flags) {
        throw new RuntimeException("Stub!");
    }

    public static void writeToParcel(ComponentName c, Parcel out) {
        throw new RuntimeException("Stub!");
    }

    public static ComponentName readFromParcel(Parcel in) {
        throw new RuntimeException("Stub!");
    }
}
