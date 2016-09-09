package com.fortysevendeg.ninecardslauncher.services.contacts;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public interface Fields {

    // -- Base Contact -- //
    Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    Uri PHOTO_URI = ContactsContract.Contacts.CONTENT_LOOKUP_URI;
    String LOOKUP_KEY = ContactsContract.Contacts.LOOKUP_KEY;
    String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
    String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    String STARRED = ContactsContract.Contacts.STARRED;

    String ALL_CONTACTS_SELECTION = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1 " +
            " AND " +
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " NOT NULL " +
            " AND " +
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " <> ''";
    String CONTACTS_ORDER_BY_ASC = DISPLAY_NAME + " COLLATE NOCASE ASC";
    String CONTACTS_BY_KEYWORD_SELECTION = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1 " +
            " AND " +
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? " +
            " AND " +
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " NOT NULL " +
            " AND " +
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " <> ''";
    String LOOKUP_SELECTION = ContactsContract.Contacts.LOOKUP_KEY + " = ?";
    String STARRED_SELECTION = ContactsContract.Contacts.STARRED + " > 0";
    String HAS_PHONE_NUMBER_SELECTION = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1";

    // -- Email -- //
    Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    String EMAIL_LOOKUP_KEY = ContactsContract.CommonDataKinds.Email.LOOKUP_KEY;
    String EMAIL_DISPLAY_NAME = ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY;
    String EMAIL_HAS_PHONE_NUMBER = ContactsContract.CommonDataKinds.Email.HAS_PHONE_NUMBER;
    String EMAIL_STARRED = ContactsContract.CommonDataKinds.Email.STARRED;
    String EMAIL_ADDRESS = ContactsContract.CommonDataKinds.Email.ADDRESS;
    String EMAIL_TYPE = ContactsContract.CommonDataKinds.Email.TYPE;

    int EMAIL_TYPE_HOME = ContactsContract.CommonDataKinds.Email.TYPE_HOME;
    int EMAIL_TYPE_WORK = ContactsContract.CommonDataKinds.Email.TYPE_WORK;

    String EMAIL_CONTACT_SELECTION = ContactsContract.CommonDataKinds.Email.LOOKUP_KEY + " IN ";
    String EMAIL_SELECTION = ContactsContract.CommonDataKinds.Email.DATA + " = ? ";

    // -- Phone -- //
    Uri PHONE_LOOKUP_URI = ContactsContract.PhoneLookup.CONTENT_FILTER_URI;
    Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

    String PHONE_LOOKUP_KEY = ContactsContract.PhoneLookup.LOOKUP_KEY;
    String PHONE_DISPLAY_NAME = ContactsContract.PhoneLookup.DISPLAY_NAME;
    String PHONE_HAS_PHONE_NUMBER = ContactsContract.PhoneLookup.HAS_PHONE_NUMBER;
    String PHONE_STARRED = ContactsContract.PhoneLookup.STARRED;
    String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    String PHONE_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;
    String PHONE_CUSTOM_RINGTONE = ContactsContract.CommonDataKinds.Phone.CUSTOM_RINGTONE;

    int PHONE_TYPE_MOBILE = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
    int PHONE_TYPE_WORK = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
    int PHONE_TYPE_HOME = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
    int PHONE_TYPE_MAIN = ContactsContract.CommonDataKinds.Phone.TYPE_MAIN;
    int PHONE_TYPE_FAX_WORK = ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK;
    int PHONE_TYPE_FAX_HOME = ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME;
    int PHONE_TYPE_PAGER = ContactsContract.CommonDataKinds.Phone.TYPE_PAGER;

    String PHONE_CONTACT_SELECTION = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " IN ";

    // -- Calls -- //
    Uri CALL_CONTENT_URI = CallLog.Calls.CONTENT_URI;

    String CALL_NUMBER = CallLog.Calls.NUMBER;
    String CALL_NAME = CallLog.Calls.CACHED_NAME;
    String CALL_NUMBER_TYPE = CallLog.Calls.CACHED_NUMBER_TYPE;
    String CALL_DATE= CallLog.Calls.DATE;
    String CALL_TYPE = CallLog.Calls.TYPE;

}