package com.udacity.stockhawk.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContentResolverCompat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created With Android Studio
 * User hangox
 * Date 2017/4/25
 * Time 上午10:31
 */

public class StockItemRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockItemRemoteViewFactory(this,intent);
    }

}



class StockItemRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private final DecimalFormat dollarFormatWithPlus;
    private final DecimalFormat dollarFormat;
    private final DecimalFormat percentageFormat;
    private final int mAppWidgetId;
    private Cursor mCursor;
    private final Context mContext;

    public StockItemRemoteViewFactory(Context context, Intent intent) {
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mContext = context;

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix("+$");
        percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix("+");

    }

    @Override
    public void onCreate() {
        queryData();
    }

    private void queryData() {
        mCursor = ContentResolverCompat.query(mContext.getContentResolver(),
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL,null);
    }

    @Override
    public void onDataSetChanged() {
        queryData();
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        mCursor.moveToPosition(position);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);

        remoteViews.setTextViewText(R.id.symbol,mCursor.getString(Contract.Quote.POSITION_SYMBOL));
        remoteViews.setTextViewText(R.id.price,dollarFormat.format(mCursor.getFloat(Contract.Quote.POSITION_PRICE)));

        float rawAbsoluteChange = mCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = mCursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        String displayText ;
        if (PrefUtils.getDisplayMode(mContext)
                .equals(mContext.getString(R.string.pref_display_mode_absolute_key))) {
            displayText = dollarFormatWithPlus.format(rawAbsoluteChange);
        } else {
            displayText = percentageFormat.format(percentageChange / 100);
        }
        remoteViews.setTextViewText(R.id.change,displayText);
        remoteViews.setTextColor(R.id.change,rawAbsoluteChange > 0 ?
                mContext.getResources().getColor(R.color.material_green_700):
        mContext.getResources().getColor(R.color.material_red_700));
        Timber.d("display %s",mCursor.getString(Contract.Quote.POSITION_SYMBOL));

        //设定Action
        Intent intent = new Intent();
        intent.putExtra("symbol",mCursor.getString(Contract.Quote.POSITION_SYMBOL));
        intent.putExtra("history",mCursor.getString(Contract.Quote.POSITION_HISTORY));
        remoteViews.setOnClickFillInIntent(R.id.item, intent);
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
