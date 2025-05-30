package anchovy.team.epialarm;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import java.util.concurrent.CompletableFuture;

public class AuthService {

    private static ISingleAccountPublicClientApplication mSingleAccountApp;
    private IAccount maccount;

    private String maccesstoken;

    private void displayError(@NonNull final Exception exception) {
        System.out.println(exception.toString());
    }

    public void loadAccount(AuthResultHandler handler) {
        if (mSingleAccountApp == null) {
            return;
        }
        mSingleAccountApp.getCurrentAccountAsync(
                new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
                @Override
                public void onAccountLoaded(@Nullable IAccount activeAccount) {
                    maccount = activeAccount;
                    if (maccount != null) {
                        String[] scopes = {"User.Read"};
                        mSingleAccountApp.acquireTokenSilentAsync(scopes, maccount.getAuthority(),
                                getAuthSilentCallback(handler));
                    }
                }

                @Override
                public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                    if (currentAccount == null) {
                        System.out.println("acc changed");
                    }
                }

                @Override
                public void onError(@NonNull MsalException exception) {
                    displayError(exception);
                }
            });
    }

    public void createClientApp(Context context, AuthResultHandler handler) {
        PublicClientApplication.createSingleAccountPublicClientApplication(
                context,
                R.raw.auth_config_single_account,
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        mSingleAccountApp = application;
                        loadAccount(handler);
                        //handler.onAuthSuccess(null);
                    }

                    @Override
                    public void onError(MsalException exception) {
                        displayError(exception);
                    }
                }
        );
    }

    public void signIn(@NonNull final Activity activity, AuthResultHandler handler) {
        String[] scopes = {"User.Read"};
        mSingleAccountApp.signIn(activity, null, scopes, getAuthInteractiveCallback(handler));
    }

    public void signOut(AuthResultHandler handler) {
        CompletableFuture.supplyAsync(() -> {
            mSingleAccountApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
                @Override
                public void onSignOut() {
                    maccount = null;
                    handler.onSignedOut();
                }

                @Override
                public void onError(@NonNull MsalException exception) {
                    displayError(exception);
                }
            });
            return null;
        });
    }

    private AuthenticationCallback getAuthInteractiveCallback(AuthResultHandler handler) {
        return new AuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                maccount = authenticationResult.getAccount();
                maccesstoken = authenticationResult.getAccessToken();
                handler.onAuthSuccess(maccesstoken);
            }

            @Override
            public void onError(MsalException exception) {
                displayError(exception);

                if (exception instanceof MsalClientException) {
                    displayError(exception);
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    displayError(exception);
                    /* Exception when communicating with the STS, likely config issue */
                }
            }

            @Override
            public void onCancel() {
                System.out.println("User canceled the authentication");
            }
        };
    }

    private SilentAuthenticationCallback getAuthSilentCallback(AuthResultHandler handler) {
        return new SilentAuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                maccesstoken = authenticationResult.getAccessToken();
                handler.onAuthSuccess(maccesstoken);
            }

            @Override
            public void onError(MsalException exception) {
                displayError(exception);

                if (exception instanceof MsalClientException) {
                    displayError(exception);
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    displayError(exception);
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception instanceof MsalUiRequiredException) {
                    displayError(exception);
                    /* Tokens expired or no session, retry with interactive */
                }
            }
        };
    }

    public String getAccesToken() {
        return maccesstoken;
    }

    public  IAccount getmAccount() {
        return maccount;
    }

    public ISingleAccountPublicClientApplication getmSingleAccountApp() {
        return mSingleAccountApp;
    }
}
