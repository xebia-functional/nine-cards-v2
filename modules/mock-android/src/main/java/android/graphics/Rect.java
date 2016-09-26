package android.graphics;


import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Rect implements Parcelable {
    public static final Creator<Rect> CREATOR = null;
    public int bottom;
    public int left;
    public int right;
    public int top;

    public Rect() {
        throw new RuntimeException("Stub!");
    }

    public Rect(int left, int top, int right, int bottom) {
        throw new RuntimeException("Stub!");
    }

    public Rect(Rect r) {
        throw new RuntimeException("Stub!");
    }

    public boolean equals(Object o) {
        throw new RuntimeException("Stub!");
    }

    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    public String toString() {
        throw new RuntimeException("Stub!");
    }

    public String toShortString() {
        throw new RuntimeException("Stub!");
    }

    public String flattenToString() {
        throw new RuntimeException("Stub!");
    }

    public static Rect unflattenFromString(String str) {
        throw new RuntimeException("Stub!");
    }

    public final boolean isEmpty() {
        throw new RuntimeException("Stub!");
    }

    public final int width() {
        throw new RuntimeException("Stub!");
    }

    public final int height() {
        throw new RuntimeException("Stub!");
    }

    public final int centerX() {
        throw new RuntimeException("Stub!");
    }

    public final int centerY() {
        throw new RuntimeException("Stub!");
    }

    public final float exactCenterX() {
        return 53;
    }

    public final float exactCenterY() {
        return 53;
    }

    public void setEmpty() {
        throw new RuntimeException("Stub!");
    }

    public void set(int left, int top, int right, int bottom) {
        throw new RuntimeException("Stub!");
    }

    public void set(Rect src) {
        throw new RuntimeException("Stub!");
    }

    public void offset(int dx, int dy) {
        throw new RuntimeException("Stub!");
    }

    public void offsetTo(int newLeft, int newTop) {
        throw new RuntimeException("Stub!");
    }

    public void inset(int dx, int dy) {
        throw new RuntimeException("Stub!");
    }

    public boolean contains(int x, int y) {
        throw new RuntimeException("Stub!");
    }

    public boolean contains(int left, int top, int right, int bottom) {
        throw new RuntimeException("Stub!");
    }

    public boolean contains(Rect r) {
        throw new RuntimeException("Stub!");
    }

    public boolean intersect(int left, int top, int right, int bottom) {
        throw new RuntimeException("Stub!");
    }

    public boolean intersect(Rect r) {
        throw new RuntimeException("Stub!");
    }

    public boolean setIntersect(Rect a, Rect b) {
        throw new RuntimeException("Stub!");
    }

    public boolean intersects(int left, int top, int right, int bottom) {
        throw new RuntimeException("Stub!");
    }

    public static boolean intersects(Rect a, Rect b) {
        throw new RuntimeException("Stub!");
    }

    public void union(int left, int top, int right, int bottom) {
        throw new RuntimeException("Stub!");
    }

    public void union(Rect r) {
        throw new RuntimeException("Stub!");
    }

    public void union(int x, int y) {
        throw new RuntimeException("Stub!");
    }

    public void sort() {
        throw new RuntimeException("Stub!");
    }

    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    public void writeToParcel(Parcel out, int flags) {
        throw new RuntimeException("Stub!");
    }

    public void readFromParcel(Parcel in) {
        throw new RuntimeException("Stub!");
    }
}
