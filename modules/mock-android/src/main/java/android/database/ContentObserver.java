package android.database;

import android.net.Uri;
import android.os.Handler;

public abstract class ContentObserver {
    public ContentObserver(Handler handler) { }

    public boolean deliverSelfNotifications() {
        throw new RuntimeException("Stub!");
    }

    public void onChange(boolean selfChange) {
        throw new RuntimeException("Stub!");
    }

    public void onChange(boolean selfChange, Uri uri) {
        throw new RuntimeException("Stub!");
    }

    /** @deprecated */
    @Deprecated
    public final void dispatchChange(boolean selfChange) {
        throw new RuntimeException("Stub!");
    }

    public final void dispatchChange(boolean selfChange, Uri uri) {
        throw new RuntimeException("Stub!");
    }
}