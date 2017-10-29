package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.Settings.System;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;

public class MinitBattery extends RelativeLayout {
    private ImageView mBattery;
    private int mBatteryColor = -1;
    private String mBatteryIconsLoaction;
    private int mBatteryLowColor = -2217724;
    private int mBatteryMidColor = -15360;
    private int mBatteryType = 8;
    private int mChargeAnim = 0;
    private String mDownloadBatteryIconsLoaction;
    private File mFile;
    private boolean mIsColorable = false;
    private int mLevel;
    private int mLowLevel = 20;
    private int mMidLevel = 50;
    private TextView mPercent;
    private ResourceManager mRM;
    private BroadcastReceiver mReceiver = new MinitBatteryReceiver();
    private int mStatus;
    private int mTextColor = -1;
    private int mTextSize = 30;
    private int mWorkingType = 0;

    class MinitBatteryReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.BATTERY_CHANGED")) {
                MinitBattery.this.mLevel = intent.getIntExtra("level", 0);
                MinitBattery.this.mStatus = intent.getIntExtra("status", 1);
            }
            MinitBattery.this.getSettings();
            MinitBattery.this.updateImageView();
        }
    }

    private class ResourceManager {
        private Resources mRes;
        private Context mResourceContext;

        public ResourceManager(Context context) {
            try {
                this.mResourceContext = context.createPackageContext("com.three.minit.batteryresources", 2);
                this.mRes = this.mResourceContext.getResources();
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        public Drawable getDrawable(String name) {
            return this.mRes.getDrawable(getResourceId(name, "drawable"));
        }

        public int getResourceId(String name, String type) {
            return this.mRes.getIdentifier(name, type, this.mResourceContext.getPackageName());
        }

        public Resources getResources() {
            return this.mRes;
        }
    }

    public MinitBattery(Context context) {
        super(context);
        init(context);
    }

    public MinitBattery(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MinitBattery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.mLevel = 0;
        this.mStatus = 0;
        try {
            this.mBatteryIconsLoaction = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "3MinitBatteryIcons";
            this.mFile = new File(this.mBatteryIconsLoaction);
            this.mFile.mkdirs();
            this.mDownloadBatteryIconsLoaction = getSaveLocation(context);
            this.mRM = new ResourceManager(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        removeAllViews();
        this.mBattery = new ImageView(getContext());
        this.mBattery.setLayoutParams(new LayoutParams(-1, -1));
        addView(this.mBattery);
        this.mPercent = new TextView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(13);
        this.mPercent.setLayoutParams(params);
        addView(this.mPercent);
        getSettings();
    }

    private String getSaveLocation(Context context) {
        String t = System.getString(context.getContentResolver(), "save_loc");
        if (t != null) {
            return t + "/3Minit Downloads/BatteryIcons/";
        }
        return Environment.getExternalStorageDirectory().getPath() + "/3Minit Downloads/BatteryIcons/";
    }

    private void getSettings() {
        boolean z;
        ContentResolver cr = getContext().getContentResolver();
        this.mDownloadBatteryIconsLoaction = getSaveLocation(getContext());
        this.mChargeAnim = System.getInt(cr, "minit_anim_type", 0);
        this.mBatteryType = System.getInt(cr, "minit_battery_type", 8);
        this.mWorkingType = System.getInt(cr, "minit_working_type", 0);
        if (System.getInt(cr, "minit_colorable", 0) == 1) {
            z = true;
        } else {
            z = false;
        }
        this.mIsColorable = z;
        this.mBatteryColor = System.getInt(cr, "minit_battery_color", this.mBatteryColor);
        this.mBatteryMidColor = System.getInt(cr, "minit_battery_mid_color", this.mBatteryMidColor);
        this.mBatteryLowColor = System.getInt(cr, "minit_battery_low_color", this.mBatteryLowColor);
        this.mMidLevel = System.getInt(cr, "minit_mid_level", this.mMidLevel);
        this.mLowLevel = System.getInt(cr, "minit_low_level", this.mLowLevel);
        this.mTextSize = System.getInt(cr, "minit_battery_text_size", 15);
        this.mTextColor = System.getInt(cr, "minit_battery_text_color", this.mTextColor);
        String fontDir = System.getString(getContext().getContentResolver(), "minit_battery_font_dir");
        if (fontDir == null || fontDir.equals("")) {
            fontDir = "/system/fonts/Roboto-Thin.ttf";
        }
        try {
            this.mPercent.setTypeface(Typeface.createFromFile(fontDir));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.mTextSize > 16) {
            this.mTextSize = 16;
        }
        this.mPercent.setTextSize((float) this.mTextSize);
        this.mPercent.setTextColor(this.mTextColor);
        if (this.mIsColorable) {
            this.mPercent.setVisibility(0);
        } else {
            this.mPercent.setVisibility(8);
        }
        if (System.getInt(cr, "minit_battery_visible", 1) == 1) {
            setVisibility(0);
        } else {
            setVisibility(8);
        }
    }

    private void applyColorFilter() {
        if (!this.mIsColorable) {
            this.mBattery.setColorFilter(-1, Mode.MULTIPLY);
        } else if (this.mLevel >= this.mMidLevel && this.mLevel >= this.mLowLevel) {
            this.mBattery.setColorFilter(this.mBatteryColor, Mode.MULTIPLY);
        } else if (this.mLevel < this.mMidLevel && this.mLevel > this.mLowLevel) {
            this.mBattery.setColorFilter(this.mBatteryMidColor, Mode.MULTIPLY);
        } else if (this.mLevel < this.mLowLevel) {
            this.mBattery.setColorFilter(this.mBatteryLowColor, Mode.MULTIPLY);
        }
    }

    private Drawable getDefaultBattery(int level, boolean charge) {
        Drawable d;
        if (charge) {
            d = this.mRM.getDrawable("battery_" + String.valueOf(this.mBatteryType) + "_charge_anim" + String.valueOf(level));
        } else {
            d = this.mRM.getDrawable("battery_" + String.valueOf(this.mBatteryType) + "_" + String.valueOf(level));
        }
        setBatterySize(null, d);
        return d;
    }

    private Drawable getNormalDrawable(int level) {
        Drawable drawable;
        switch (this.mWorkingType) {
            case 0:
                return getDefaultBattery(level, false);
            case 1:
                File f = new File(this.mDownloadBatteryIconsLoaction + "stat_sys_battery_" + String.valueOf(level) + ".png");
                if (!f.exists()) {
                    return getDefaultBattery(level, false);
                }
                drawable = Drawable.createFromPath(f.getAbsolutePath());
                setBatterySize(f, null);
                return drawable;
            case 2:
                File fi = new File(this.mBatteryIconsLoaction, "stat_sys_battery_" + String.valueOf(level) + ".png");
                if (!fi.exists()) {
                    return getDefaultBattery(level, false);
                }
                drawable = Drawable.createFromPath(fi.getAbsolutePath());
                setBatterySize(fi, null);
                return drawable;
            default:
                return null;
        }
    }

    private Drawable getChargingDrawable(int level) {
        Drawable drawable;
        switch (this.mWorkingType) {
            case 0:
                return getDefaultBattery(level, true);
            case 1:
                File f = new File(this.mDownloadBatteryIconsLoaction + "stat_sys_battery_charge_" + String.valueOf(level) + ".png");
                if (!f.exists()) {
                    return getDefaultBattery(level, true);
                }
                drawable = Drawable.createFromPath(f.getAbsolutePath());
                setBatterySize(f, null);
                return drawable;
            case 2:
                File fi = new File(this.mBatteryIconsLoaction, "stat_sys_battery_charge_anim" + String.valueOf(level) + ".png");
                if (!fi.exists()) {
                    return getDefaultBattery(level, true);
                }
                drawable = Drawable.createFromPath(fi.getAbsolutePath());
                setBatterySize(fi, null);
                return drawable;
            default:
                return null;
        }
    }

    private AnimationDrawable getChargingAnimation(int level) {
        AnimationDrawable ad = new AnimationDrawable();
        int i;
        switch (this.mChargeAnim) {
            case 0:
                ad.addFrame(getChargingDrawable(level), 1500);
                ad.addFrame(getNormalDrawable(level), 500);
                break;
            case 1:
                for (i = 1; i < 100; i++) {
                    ad.addFrame(getChargingDrawable(i), 20);
                }
                ad.addFrame(getChargingDrawable(100), 500);
                ad.addFrame(getNormalDrawable(level), 2500);
                break;
            case 2:
                ad.addFrame(getNormalDrawable(level), 2000);
                for (i = level; i < 101; i++) {
                    ad.addFrame(getChargingDrawable(i), 80);
                }
                break;
            case 3:
                int l = level;
                if (l == 0) {
                    l = 1;
                }
                for (i = 0; i < l; i++) {
                    ad.addFrame(getChargingDrawable(i), 20);
                }
                ad.addFrame(getNormalDrawable(level), 2500);
                break;
            case 4:
                for (i = 0; i < 101; i++) {
                    ad.addFrame(getChargingDrawable(i), 20);
                }
                ad.addFrame(getNormalDrawable(level), 250);
                ad.addFrame(getChargingDrawable(level), 100);
                ad.addFrame(getNormalDrawable(level), 250);
                ad.addFrame(getChargingDrawable(level), 100);
                ad.addFrame(getNormalDrawable(level), 2000);
                break;
        }
        return ad;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        filter.addAction("com.three.minit.BATTERY_TYPE_CHANGED");
        filter.setPriority(1000);
        getContext().registerReceiver(this.mReceiver, filter);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getContext().unregisterReceiver(this.mReceiver);
    }

    private void updateImageView() {
        switch (this.mStatus) {
            case 2:
                AnimationDrawable ad = getChargingAnimation(this.mLevel);
                this.mBattery.setImageDrawable(ad);
                ad.setOneShot(false);
                ad.start();
                break;
            case 5:
                this.mBattery.setImageDrawable(getNormalDrawable(100));
                break;
            default:
                this.mBattery.setImageDrawable(getNormalDrawable(this.mLevel));
                break;
        }
        applyColorFilter();
        this.mPercent.setText(String.valueOf(this.mLevel));
    }

    private void setBatterySize(File file, Drawable drawable) {
        Options options = new Options();
        options.inPreferredConfig = Config.ARGB_8888;
        Bitmap b = null;
        if (file != null) {
            b = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        }
        if (drawable != null) {
            b = ((BitmapDrawable) drawable).getBitmap();
        }
        int width = b.getWidth();
        int height = b.getHeight();
        int size = System.getInt(getContext().getContentResolver(), "minit_battery_size", 0);
        int t;
        if (size < 0) {
            t = Integer.valueOf(String.valueOf(size).substring(1)).intValue();
            getLayoutParams().height = height - t;
            getLayoutParams().width = width - t;
        } else if (size > 0) {
            t = size;
            getLayoutParams().height = height + t;
            getLayoutParams().width = width + t;
        } else {
            getLayoutParams().height = height;
            getLayoutParams().width = width;
        }
        setLayoutParams(getLayoutParams());
    }
}
