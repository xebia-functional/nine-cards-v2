package com.fortysevendeg.ninecardslauncher.services.contacts;

import android.net.Uri;
import android.provider.ContactsContract;

public class EmailContract {

    final static Uri CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    final static String ADDRESS = ContactsContract.CommonDataKinds.Email.ADDRESS;
    final static String CONTENT_TYPE = ContactsContract.CommonDataKinds.Email.CONTENT_TYPE;
}
