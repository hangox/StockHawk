package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;

import com.udacity.stockhawk.BuildConfig;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {
    public static final String NOTIFY_UPDATE = BuildConfig.APPLICATION_ID +".action.UPDATE";

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        QuoteSyncJob.getQuotes(getApplicationContext());
        sendBroadcast(new Intent(NOTIFY_UPDATE));
    }
}
