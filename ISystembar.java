package fz.com.androidarcture;

/**
 * Created by Administrator on 2017/11/1.
 */

interface ISystembar {
    ISystembar setFillscreen(boolean fillscreen);
    ISystembar setDrawer(boolean drawer);
    ISystembar setColor(int color);
    ISystembar setTransparent(boolean transparent);
    ISystembar setAlpha(int alpha);
    ISystembar setVisable(boolean hide);
    void InitSystemBar();
    void ReacateSystemBar();
    void setHintBar();
}
