package com.fortysevendeg.ninecardslauncher.services.contacts;

import android.net.Uri;
import android.provider.ContactsContract;

public class Fields {

    final static Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    final static String CONTACT_ID = ContactsContract.Contacts._ID;
    final static String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    final static String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    final static String STARRED = ContactsContract.Contacts.STARRED;
}