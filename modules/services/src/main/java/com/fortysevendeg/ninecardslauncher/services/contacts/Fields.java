package com.fortysevendeg.ninecardslauncher.services.contacts;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Fields {

    final static Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    final static Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;

    final static String CONTACT_ID = ContactsContract.Contacts._ID;
    final static String LOOKUP_KEY = ContactsContract.Contacts.LOOKUP_KEY;
    final static String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
    final static String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    final static String STARRED = ContactsContract.Contacts.STARRED;
}