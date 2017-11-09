package fz.com.androidarcture;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Method;

/**
 * Created by 冯支 on 2017/11/1.
 * 初始化系统状态栏和导航栏
 * sdk 19及以上处理，以下不处理
 */

public class SystembarUtil {
    /**
     * 顶部view标志
     */
    private static final int VIEW_TOP = 1;

    /**
     * 底部view标志
     */
    private static final int VIEW_BOTTOM = 2;


    /**
     * 隐藏上下栏标志
     */
    public static final String BAR_VISABLE = "BAR_VISABLE";

    /**
     * 全屏标志
     */
    public static final String BAR_FULLSCREEN = "BAR_FULLSCREEN";

    /**
     * 颜色标志
     */
    public static final String BAR_COLOR = "BAR_COLOR";

    /**
     * 透明度标志
     */
    public static final String BAR_ALPHA = "BAR_ALPHA";

    /**
     * 构造Systembar的builder
     * <p>
     * 传入上下文，类型Activity
     */
    public static ISystembar with(Activity activity) {
        return new DefaultSystembar(activity);
    }

    /**
     * 根布局全部fitSystemWindow=true
     */
    public static void setRootView(Activity activity) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }


    /**
     * 抽屉布局除了主界面fitSystemWindow=true，其他皆为false
     */
    public static void setRootViewfirst(Activity activity) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof DrawerLayout) {
                childView.setFitsSystemWindows(false);
                ViewGroup child = (ViewGroup) ((DrawerLayout) childView).getChildAt(0);
                child.setFitsSystemWindows(true);
                ViewGroup child1 = (ViewGroup) ((DrawerLayout) childView).getChildAt(1);
                child1.setFitsSystemWindows(false);
            }
        }
    }

    /**
     * 抽屉布局除了主界面fitSystemWindow=true，其他皆为false
     */
    public static void setRootViewfirst(Activity activity,boolean flag) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof DrawerLayout) {
                childView.setFitsSystemWindows(false);
                ViewGroup child = (ViewGroup) ((DrawerLayout) childView).getChildAt(0);
                child.setFitsSystemWindows(!flag);
                ViewGroup child1 = (ViewGroup) ((DrawerLayout) childView).getChildAt(1);
                child1.setFitsSystemWindows(false);
            }
        }
    }

    /**
     * 创建顶部view
     */
    public static View createStatusBarView(Context context, @ColorInt int color) {
        return CreatView(context, color, VIEW_TOP);
    }

    /**
     * 创建底部view
     */
    public static View createNavBarView(Context context, @ColorInt int color) {
        return CreatView(context, color, VIEW_BOTTOM);
    }

    private static View CreatView(Context context, int color, int viewTop) {
        View mStatusBarTintView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, viewTop == VIEW_TOP ? getStatusBarHeight(context) : getNavigationHeight(context));
        params.gravity = viewTop == VIEW_TOP ? Gravity.TOP : Gravity.BOTTOM;
        mStatusBarTintView.setLayoutParams(params);
        mStatusBarTintView.setBackgroundColor(color);
        return mStatusBarTintView;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        return getHeight(context, "status_bar_height");
    }

    /**
     * 获取虚拟导航栏高度
     */
    public static int getNavigationHeight(Context context) {
        return getHeight(context, "navigation_bar_height");
    }

    /**
     * 获取高度
     *
     * @param deftype 关键标志名
     */
    private static int getHeight(Context context, String deftype) {
        int resourceId = context.getResources().getIdentifier(deftype, "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取计算后的颜色
     *
     * @param alpha 透明度
     * @param color 颜色
     */
    @ColorInt
    public static int calculateColor(@ColorInt int color, int alpha) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }


    /**
     * 获取是否存在NavigationBar
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

    /**
     * 动态隐藏模式一
     * 重新reacreate活动，让活动自动新建状态栏再改变
     * 不会遮挡抽屉顶部
     */
    public static void setNavVisibilityMode1(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    /**
     * 动态隐藏模式二
     * 适合全屏模式，动态隐藏和显示系统上下栏
     * 不重新reacreate活动
     * 如果是抽屉模式，则会遮挡顶部，其他模式不影响
     */
    public static void setNavVisibilityMode2(Activity activity,boolean visible) {
        int visibility = 0;
        int navbar = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            navbar = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }

        if (!visible) {
            navbar |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            navbar |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE;

            visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (visible) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            visibility |= View.SYSTEM_UI_FLAG_VISIBLE;
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (checkDeviceHasNavigationBar(activity))
            visibility |= navbar;

        activity.getWindow().getDecorView().setSystemUiVisibility(visibility);
    }

/**
     * 动态隐藏模式三
     * 适合非全屏模式，动态隐藏和显示系统上下栏，空间大小会改变
     * 不重新reacreate活动
     */
private void setSystemUIVisible(boolean show) {  
    if (show) {  
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;  
        uiFlags |= 0x00001000;  
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);  
    } else {  
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE  
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION  
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION  
                | View.SYSTEM_UI_FLAG_FULLSCREEN;  
        uiFlags |= 0x00001000;  
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);  
    }  
}

}
