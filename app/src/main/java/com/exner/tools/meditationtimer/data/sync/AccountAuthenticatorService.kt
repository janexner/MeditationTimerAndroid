package com.exner.tools.meditationtimer.data.sync

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.accounts.NetworkErrorException
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.exner.tools.meditationtimer.MeditationTimerApplication


/**
 * Authenticator service that returns a subclass of AbstractAccountAuthenticator in onBind()
 */
class AccountAuthenticatorService : Service() {
    private var sAccountAuthenticator: AccountAuthenticatorImpl? = null

    override fun onBind(intent: Intent): IBinder? {
        var ret: IBinder? = null
        if (intent.action == AccountManager.ACTION_AUTHENTICATOR_INTENT) ret =
            authenticator!!.iBinder
        return ret
    }

    private val authenticator: AccountAuthenticatorImpl?
        get() {
            if (sAccountAuthenticator == null) sAccountAuthenticator = AccountAuthenticatorImpl(
                this
            )
            return sAccountAuthenticator
        }

    private class AccountAuthenticatorImpl(private val mContext: Context) :
        AbstractAccountAuthenticator(mContext) {
        /*
                  *  The user has requested to add a new account to the system.  We return an intent that will launch our login screen if the user has not logged in yet,
                  *  otherwise our activity will just pass the user's credentials on to the account manager.
                  */
        @Throws(NetworkErrorException::class)
        override fun addAccount(
            response: AccountAuthenticatorResponse,
            accountType: String,
            authTokenType: String,
            requiredFeatures: Array<String>,
            options: Bundle
        ): Bundle {
            val reply = Bundle()

            val i = Intent(mContext, MeditationTimerApplication::class.java)
            i.setAction("com.exner.tools.meditationtimer.sync.LOGIN")
            i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            reply.putParcelable(AccountManager.KEY_INTENT, i)

            return reply
        }

        override fun confirmCredentials(
            response: AccountAuthenticatorResponse,
            account: Account,
            options: Bundle
        ): Bundle? {
            return null
        }

        override fun editProperties(
            response: AccountAuthenticatorResponse,
            accountType: String
        ): Bundle? {
            return null
        }

        @Throws(NetworkErrorException::class)
        override fun getAuthToken(
            response: AccountAuthenticatorResponse,
            account: Account,
            authTokenType: String,
            options: Bundle
        ): Bundle? {
            return null
        }

        override fun getAuthTokenLabel(authTokenType: String): String? {
            return null
        }

        @Throws(NetworkErrorException::class)
        override fun hasFeatures(
            response: AccountAuthenticatorResponse,
            account: Account,
            features: Array<String>
        ): Bundle? {
            return null
        }

        override fun updateCredentials(
            response: AccountAuthenticatorResponse,
            account: Account,
            authTokenType: String,
            options: Bundle
        ): Bundle? {
            return null
        }
    }
}