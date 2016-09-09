package android.accounts;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.accounts.AuthenticatorException;
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    public static final String ACTION_AUTHENTICATOR_INTENT = "android.accounts.AccountAuthenticator";
    public static final String AUTHENTICATOR_ATTRIBUTES_NAME = "account-authenticator";
    public static final String AUTHENTICATOR_META_DATA_NAME = "android.accounts.AccountAuthenticator";
    public static final int ERROR_CODE_BAD_ARGUMENTS = 7;
    public static final int ERROR_CODE_BAD_AUTHENTICATION = 9;
    public static final int ERROR_CODE_BAD_REQUEST = 8;
    public static final int ERROR_CODE_CANCELED = 4;
    public static final int ERROR_CODE_INVALID_RESPONSE = 5;
    public static final int ERROR_CODE_NETWORK_ERROR = 3;
    public static final int ERROR_CODE_REMOTE_EXCEPTION = 1;
    public static final int ERROR_CODE_UNSUPPORTED_OPERATION = 6;
    public static final String KEY_ACCOUNTS = "accounts";
    public static final String KEY_ACCOUNT_AUTHENTICATOR_RESPONSE = "accountAuthenticatorResponse";
    public static final String KEY_ACCOUNT_MANAGER_RESPONSE = "accountManagerResponse";
    public static final String KEY_ACCOUNT_NAME = "authAccount";
    public static final String KEY_ACCOUNT_TYPE = "accountType";
    public static final String KEY_ANDROID_PACKAGE_NAME = "androidPackageName";
    public static final String KEY_AUTHENTICATOR_TYPES = "authenticator_types";
    public static final String KEY_AUTHTOKEN = "authtoken";
    public static final String KEY_AUTH_FAILED_MESSAGE = "authFailedMessage";
    public static final String KEY_AUTH_TOKEN_LABEL = "authTokenLabelKey";
    public static final String KEY_BOOLEAN_RESULT = "booleanResult";
    public static final String KEY_CALLER_PID = "callerPid";
    public static final String KEY_CALLER_UID = "callerUid";
    public static final String KEY_ERROR_CODE = "errorCode";
    public static final String KEY_ERROR_MESSAGE = "errorMessage";
    public static final String KEY_INTENT = "intent";
    public static final String KEY_LAST_AUTHENTICATED_TIME = "lastAuthenticatedTime";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USERDATA = "userdata";
    public static final String LOGIN_ACCOUNTS_CHANGED_ACTION = "android.accounts.LOGIN_ACCOUNTS_CHANGED";

    AccountManager() {
        throw new RuntimeException("Stub!");
    }

    public static AccountManager get(Context context) {
        throw new RuntimeException("Stub!");
    }

    public String getPassword(Account account) {
        throw new RuntimeException("Stub!");
    }

    public String getUserData(Account account, String key) {
        throw new RuntimeException("Stub!");
    }

    public AuthenticatorDescription[] getAuthenticatorTypes() {
        throw new RuntimeException("Stub!");
    }

    public Account[] getAccounts() {
        throw new RuntimeException("Stub!");
    }

    public Account[] getAccountsByTypeForPackage(String type, String packageName) {
        throw new RuntimeException("Stub!");
    }

    public Account[] getAccountsByType(String type) {
        throw new RuntimeException("Stub!");
    }

    public AccountManagerFuture<Boolean> hasFeatures(Account account, String[] features, AccountManagerCallback<Boolean> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public AccountManagerFuture<Account[]> getAccountsByTypeAndFeatures(String type, String[] features, AccountManagerCallback<Account[]> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public boolean addAccountExplicitly(Account account, String password, Bundle userdata) {
        throw new RuntimeException("Stub!");
    }

    public boolean notifyAccountAuthenticated(Account account) {
        throw new RuntimeException("Stub!");
    }

    public AccountManagerFuture<Account> renameAccount(Account account, String newName, AccountManagerCallback<Account> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public String getPreviousName(Account account) {
        throw new RuntimeException("Stub!");
    }

    /** @deprecated */
    @Deprecated
    public AccountManagerFuture<Boolean> removeAccount(Account account, AccountManagerCallback<Boolean> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public AccountManagerFuture<Bundle> removeAccount(Account account, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public boolean removeAccountExplicitly(Account account) {
        throw new RuntimeException("Stub!");
    }

    public void invalidateAuthToken(String accountType, String authToken) {
        throw new RuntimeException("Stub!");
    }

    public String peekAuthToken(Account account, String authTokenType) {
        throw new RuntimeException("Stub!");
    }

    public void setPassword(Account account, String password) {
        throw new RuntimeException("Stub!");
    }

    public void clearPassword(Account account) {
        throw new RuntimeException("Stub!");
    }

    public void setUserData(Account account, String key, String value) {
        throw new RuntimeException("Stub!");
    }

    public void setAuthToken(Account account, String authTokenType, String authToken) {
        throw new RuntimeException("Stub!");
    }

    public String blockingGetAuthToken(Account account, String authTokenType, boolean notifyAuthFailure) throws OperationCanceledException, IOException, AuthenticatorException {
        throw new RuntimeException("Stub!");
    }

    public AccountManagerFuture<Bundle> getAuthToken(Account account, String authTokenType, Bundle options, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    /** @deprecated */
    @Deprecated
    public AccountManagerFuture<Bundle> getAuthToken(Account account, String authTokenType, boolean notifyAuthFailure, AccountManagerCallback<Bundle> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public AccountManagerFuture<Bundle> getAuthToken(Account account, String authTokenType, Bundle options, boolean notifyAuthFailure, AccountManagerCallback<Bundle> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public AccountManagerFuture<Bundle> addAccount(String accountType, String authTokenType, String[] requiredFeatures, Bundle addAccountOptions, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public AccountManagerFuture<Bundle> confirmCredentials(Account account, Bundle options, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public AccountManagerFuture<Bundle> updateCredentials(Account account, String authTokenType, Bundle options, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public AccountManagerFuture<Bundle> editProperties(String accountType, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    public AccountManagerFuture<Bundle> getAuthTokenByFeatures(String accountType, String authTokenType, String[] features, Activity activity, Bundle addAccountOptions, Bundle getAuthTokenOptions, AccountManagerCallback<Bundle> callback, Handler handler) {
        throw new RuntimeException("Stub!");
    }

    /** @deprecated */
    @Deprecated
    public static Intent newChooseAccountIntent(Account selectedAccount, ArrayList<Account> allowableAccounts, String[] allowableAccountTypes, boolean alwaysPromptForAccount, String descriptionOverrideText, String addAccountAuthTokenType, String[] addAccountRequiredFeatures, Bundle addAccountOptions) {
        throw new RuntimeException("Stub!");
    }

    public static Intent newChooseAccountIntent(Account selectedAccount, List<Account> allowableAccounts, String[] allowableAccountTypes, String descriptionOverrideText, String addAccountAuthTokenType, String[] addAccountRequiredFeatures, Bundle addAccountOptions) {
        return null;
    }

    public void addOnAccountsUpdatedListener(OnAccountsUpdateListener listener, Handler handler, boolean updateImmediately) {
        throw new RuntimeException("Stub!");
    }

    public void removeOnAccountsUpdatedListener(OnAccountsUpdateListener listener) {
        throw new RuntimeException("Stub!");
    }
}
