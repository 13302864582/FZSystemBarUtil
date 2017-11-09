package fz.com.androidarcture;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import static fz.com.androidarcture.SystembarUtil.BAR_ALPHA;
import static fz.com.androidarcture.SystembarUtil.BAR_COLOR;
import static fz.com.androidarcture.SystembarUtil.BAR_FULLSCREEN;
import static fz.com.androidarcture.SystembarUtil.BAR_VISABLE;
import static fz.com.androidarcture.SystembarUtil.calculateColor;
import static fz.com.androidarcture.SystembarUtil.checkDeviceHasNavigationBar;
import static fz.com.androidarcture.SystembarUtil.createNavBarView;
import static fz.com.androidarcture.SystembarUtil.createStatusBarView;
import static fz.com.androidarcture.SystembarUtil.setRootViewfirst;

/**
 * Created by Administrator on 2017/11/1.
 */

class DefaultSystembar implements ISystembar {
    private ViewGroup mDecorView;
    /**
     * 传入的上下文
     */
    private Activity activity;

    /**
     * 是否全屏
     */
    private boolean isFillscreen;

    /**
     * 是否抽屉布局
     */
    private boolean isDrawer;

    /**
     * 是否包含虚拟导航栏
     */
    private boolean isNavigationExist;

    /**
     * 颜色
     */
    private int color;


    /**
     * 颜色透明度
     */
    private int alpha;

    /**
     * 是否上下透明
     */
    private boolean isTransparent;

    /**
     * 上下是否隐藏
     */
    private boolean isVisable;

    /**
     * Window实例
     */
    private Window mWindow;


    /**
     * DefaultSystembar构造
     */
    public DefaultSystembar(Activity activity) {
        if (activity == null)
            throw new IllegalArgumentException("The activity can not be null.");
        this.activity = activity;
        this.mWindow = activity.getWindow();
        this.mDecorView = (ViewGroup) mWindow.getDecorView();
        initParams();
    }

    /**
     * 初始化默认参数
     */
    private void initParams() {
        PrefrenceUtil.init(activity);
        isVisable = PrefrenceUtil.getBoolean(BAR_VISABLE, false);
        isFillscreen = PrefrenceUtil.getBoolean(BAR_FULLSCREEN, false);
        color = PrefrenceUtil.getInt(BAR_COLOR, Color.TRANSPARENT);
        alpha = PrefrenceUtil.getInt(BAR_ALPHA, 0);
        isDrawer = false;
        isTransparent = false;
    }


    /**
     * 设置全屏参数
     */
    @Override
    public ISystembar setFillscreen(boolean fillscreen) {
        this.isFillscreen = fillscreen;
        return this;
    }


    /**
     * 设置抽屉参数
     */
    @Override
    public ISystembar setDrawer(boolean drawer) {
        this.isDrawer = drawer;
        return this;
    }


    /**
     * 设置颜色参数
     */
    @Override
    public ISystembar setColor(int color) {
        this.color = color;
        return this;
    }


    /**
     * 设置透明参数
     */
    @Override
    public ISystembar setTransparent(boolean transparent) {
        this.isTransparent = transparent;
        return this;
    }


    /**
     * 设置透明度参数
     */
    @Override
    public ISystembar setAlpha(int alpha) {
        this.alpha = alpha;
        return this;
    }

    @Override
    public ISystembar setVisable(boolean hide) {
        this.isVisable = hide;
        return this;
    }


    /**
     * 设置系统状态栏和导航栏
     */
    @Override
    public void InitSystemBar() {
        isNavigationExist = checkDeviceHasNavigationBar(activity);
        View statebar_view = createStatusBarView(activity, getColor());
        View navigationbar_view = createNavBarView(activity, getColor());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //大于等于LOLLIPOP
            if (isDrawer) {
                setFullOption();
                setBackColor(Color.TRANSPARENT);

                mDecorView.addView(statebar_view, 0);
                if (isNavigationExist)
                    mDecorView.addView(navigationbar_view, 1);

                if (!isFillscreen)
                    SystembarUtil.setRootViewfirst(activity);
            } else {
                if (isFillscreen) {
                    setFullOption();
                } else {
                    //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
                    mWindow.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    if (isNavigationExist)
                        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                }
            }
            //setBackColor();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //大于等于KITKAT，小于LOLLIPOP
            SetupKitcatBar(statebar_view, navigationbar_view);
        }
        //setNavVisibility(activity, isVisable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isVisable) {
                setBackColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public void ReacateSystemBar() {
        PrefrenceUtil.putBoolean(BAR_VISABLE, isVisable);
        PrefrenceUtil.putBoolean(BAR_FULLSCREEN, isFillscreen);
        PrefrenceUtil.putInt(BAR_COLOR, color);
        PrefrenceUtil.putInt(BAR_ALPHA, alpha);
        activity.recreate();
    }

    @Override
    public void setHintBar() {
        SystembarUtil.setNavVisibilityMode1(activity);
    }


    /**
     * 获取颜色参数
     */
    private int getColor() {
        return isTransparent ? Color.TRANSPARENT : (calculateColor(color, alpha));
    }


    /**
     * 设置状态栏和导航栏背景颜色
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setBackColor() {
        if (isNavigationExist)
            mWindow.setNavigationBarColor(getColor());
        mWindow.setStatusBarColor(getColor());
    }

    /**
     * 设置状态栏和导航栏背景颜色
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setBackColor(int color) {
        if (isNavigationExist)
            mWindow.setNavigationBarColor(color);
        mWindow.setStatusBarColor(color);
    }

    /**
     * 显示隐藏状态栏，全屏不变，只在有全屏时有效
     *
     * @param enable
     */
    private void setStatusBarVisibility(boolean enable) {
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        if (enable) {
            lp.flags |= WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        } else {
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        mWindow.setAttributes(lp);
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    /**
     * 设置全屏
     */
    private void setFullOption() {
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (isNavigationExist)
            option = option | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        mDecorView.setSystemUiVisibility(option);
    }


    /**
     * 设置KITCAT
     */
    private void SetupKitcatBar(View statebar_view, View navigationbar_view) {
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (isDrawer)
            mDecorView.addView(statebar_view, 0);
        else
            mDecorView.addView(statebar_view);

        if (isNavigationExist) {
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            if (isDrawer)
                mDecorView.addView(navigationbar_view, 1);
            else
                mDecorView.addView(navigationbar_view);
        }

        if (!isFillscreen)
            setRootViewfirst(activity);
    }


}
