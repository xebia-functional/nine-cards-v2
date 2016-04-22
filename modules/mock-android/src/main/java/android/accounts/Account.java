package android.accounts;

import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {
    public String name;
    public String type;
    public static Creator<Account> CREATOR = null;

    public Account(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Account(Parcel in) {}

    public boolean equals(Object o) { return true; }

    public int hashCode() { return 0;}

    public int describeContents() { return 0;}

    public void writeToParcel(Parcel dest, int flags) {}

    public String toString() { return "";}
}