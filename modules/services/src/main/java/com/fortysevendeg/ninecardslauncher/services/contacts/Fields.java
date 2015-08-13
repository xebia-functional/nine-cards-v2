package com.fortysevendeg.ninecardslauncher.services.contacts;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Fields {

    final static Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    final static String CONTACT_ID = ContactsContract.Contacts._ID;
    final static String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;
    final static String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    final static String STARRED = ContactsContract.Contacts.STARRED;

    final static String ID_SELECTION = ContactsContract.Contacts._ID + " = ? ";
    final static String STARRED_SELECTION = ContactsContract.Contacts.STARRED + " > 0";

    final static Uri EMAIL_CONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    final static String EMAIL_CONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
    final static String EMAIL_DISPLAY_NAME = ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY;
    final static String EMAIL_HAS_PHONE_NUMBER = ContactsContract.CommonDataKinds.Email.HAS_PHONE_NUMBER;
    final static String EMAIL_STARRED = ContactsContract.CommonDataKinds.Email.STARRED;
    final static String EMAIL_ADDRESS = ContactsContract.CommonDataKinds.Email.DATA;
    final static String EMAIL_TYPE = ContactsContract.CommonDataKinds.Email.TYPE;

    final static int EMAIL_TYPE_HOME = ContactsContract.CommonDataKinds.Email.TYPE_HOME;
    final static int EMAIL_TYPE_WORK = ContactsContract.CommonDataKinds.Email.TYPE_WORK;


    final static String EMAIL_CONTACT_ID_SELECTION = ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ? ";
    final static String EMAIL_SELECTION = ContactsContract.CommonDataKinds.Email.DATA + " = ? ";

    final static Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    final static String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    final static String PHONE_DISPLAY_NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY;
    final static String PHONE_HAS_PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER;
    final static String PHONE_STARRED = ContactsContract.CommonDataKinds.Phone.STARRED;
    final static String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    final static String PHONE_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;

    final static int PHONE_TYPE_HOME = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
    final static int PHONE_TYPE_WORK = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
    final static int PHONE_TYPE_MOBILE = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;


    final static String PHONE_CONTACT_ID_SELECTION = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? ";
    final static String PHONE_SELECTION = ContactsContract.CommonDataKinds.Phone.DATA + " = ? ";

}