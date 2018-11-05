package com.hicoo.areapickerview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhouZi on 2018/9/29.
 * time:9:45
 * ----------Dragon be here!----------/
 * 　　　┏┓　　 ┏┓
 * 　　┏┛┻━━━┛┻┓━━━
 * 　　┃　　　　　 ┃
 * 　　┃　　　━　  ┃
 * 　　┃　┳┛　┗┳
 * 　　┃　　　　　 ┃
 * 　　┃　　　┻　  ┃
 * 　　┃　　　　   ┃
 * 　　┗━┓　　　┏━┛Code is far away from bug with the animal protecting
 * 　　　　┃　　　┃    神兽保佑,代码无bug
 * 　　　　┃　　　┃
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛━━━━━
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━━━━━━神兽出没━━━━━━━━━━━━━━
 */
public class AreaPickerView extends Dialog {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView ivBtn;

    private AreaPickerViewCallback areaPickerViewCallback;
    /**
     * View的集合
     */
    private List<View> views;
    /**
     * tab的集合
     */
    private List<String> strings;
    /**
     * 省
     */
    private List<AddressBean> addressBeans;
    /**
     * 市
     */
    private List<AddressBean.CityBean> cityBeans;
    /**
     * 区
     */
    private List<AddressBean.CityBean.AreaBean> areaBeans;

    private Context context;

    private ViewPagerAdapter viewPagerAdapter;
    private ProvinceAdapter provinceAdapter;
    private CityAdapter cityAdapter;
    private AreaAdapter areaAdapter;

    /**
     * 选中的区域下标 默认-1
     */
    private int provinceSelected = -1;
    private int citySelected = -1;
    private int areaSelected = -1;

    /**
     * 历史选中的区域下标 默认-1
     */
    private int oldProvinceSelected = -1;
    private int oldCitySelected = -1;
    private int oldAreaSelected = -1;

    private RecyclerView areaRecyclerView;
    private RecyclerView cityRecyclerView;

    private boolean isCreate;

    public AreaPickerView(@NonNull Context context, int themeResId, List<AddressBean> addressBeans) {
        super(context, themeResId);
        this.addressBeans = addressBeans;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_area_pickerview);
        Window window = this.getWindow();

        isCreate = true;

        /**
         * 位于底部
         */
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        /**
         * 设置弹出动画
         */
        window.setWindowAnimations(R.style.PickerAnim);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        ivBtn = findViewById(R.id.iv_btn);
        ivBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        View provinceView = LayoutInflater.from(context)
                .inflate(R.layout.layout_recyclerview, null, false);
        View cityView = LayoutInflater.from(context)
                .inflate(R.layout.layout_recyclerview, null, false);
        View areaView = LayoutInflater.from(context)
                .inflate(R.layout.layout_recyclerview, null, false);

        final RecyclerView provinceRecyclerView = provinceView.findViewById(R.id.recyclerview);
        cityRecyclerView = cityView.findViewById(R.id.recyclerview);
        areaRecyclerView = areaView.findViewById(R.id.recyclerview);

        views = new ArrayList<>();
        views.add(provinceView);
        views.add(cityView);
        views.add(areaView);

        /**
         * 配置adapter
         */
        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        /**
         * 这句话设置了过后，假如又3个tab 删除第三个 刷新过后 第二个划第三个会有弹性
         * viewPager.setOffscreenPageLimit(2);
         */

        provinceAdapter = new ProvinceAdapter(R.layout.item_address, addressBeans);
        provinceRecyclerView.setAdapter(provinceAdapter);
        LinearLayoutManager provinceManager = new LinearLayoutManager(context);
        provinceRecyclerView.setLayoutManager(provinceManager);
        provinceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Log.e("AreaPickerView", oldProvinceSelected + "~~~" + oldCitySelected + "~~~" + oldAreaSelected);
                cityBeans.clear();
                areaBeans.clear();
                addressBeans.get(position).setStatus(true);
                provinceSelected = position;
                if (oldProvinceSelected != -1 && oldProvinceSelected != provinceSelected) {
                    addressBeans.get(oldProvinceSelected).setStatus(false);
                    Log.e("AreaPickerView", "清空");
                }
                if (position != oldProvinceSelected) {
                    if (oldCitySelected != -1) {
                        addressBeans.get(oldProvinceSelected).getChildren().get(oldCitySelected).setStatus(false);
                    }
                    if (oldAreaSelected != -1) {
                        addressBeans.get(oldProvinceSelected).getChildren().get(oldCitySelected).getChildren().get(oldAreaSelected).setStatus(false);
                    }
                    oldCitySelected = -1;
                    oldAreaSelected = -1;
                }
                cityBeans.addAll(addressBeans.get(position).getChildren());
                provinceAdapter.notifyDataSetChanged();
                cityAdapter.notifyDataSetChanged();
                areaAdapter.notifyDataSetChanged();
                strings.set(0, addressBeans.get(position).getLabel());
                if (strings.size() == 1) {
                    strings.add("请选择");
                } else if (strings.size() > 1) {
                    if (position != oldProvinceSelected) {
                        strings.set(1, "请选择");
                        if (strings.size() == 3) {
                            strings.remove(2);
                        }
                    }
                }
                tabLayout.setupWithViewPager(viewPager);
                viewPagerAdapter.notifyDataSetChanged();
                tabLayout.getTabAt(1).select();
                oldProvinceSelected = provinceSelected;
            }
        });

        cityBeans = new ArrayList<>();
        cityAdapter = new CityAdapter(R.layout.item_address, cityBeans);
        LinearLayoutManager cityListManager = new LinearLayoutManager(context);
        cityRecyclerView.setLayoutManager(cityListManager);
        cityRecyclerView.setAdapter(cityAdapter);
        cityAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                areaBeans.clear();
                cityBeans.get(position).setStatus(true);
                citySelected = position;
                if (oldCitySelected != -1 && oldCitySelected != citySelected) {
                    addressBeans.get(oldProvinceSelected).getChildren().get(oldCitySelected).setStatus(false);
                }
                if (position != oldCitySelected) {
                    if (oldAreaSelected != -1 && cityBeans.get(position).getChildren() != null) {
                        addressBeans.get(oldProvinceSelected).getChildren().get(oldCitySelected).getChildren().get(oldAreaSelected).setStatus(false);
                    }
                    oldAreaSelected = -1;
                }
                oldCitySelected = citySelected;
                if (cityBeans.get(position).getChildren() != null) {
                    areaBeans.addAll(cityBeans.get(position).getChildren());
                    cityAdapter.notifyDataSetChanged();
                    areaAdapter.notifyDataSetChanged();
                    strings.set(1, cityBeans.get(position).getLabel());
                    if (strings.size() == 2) {
                        strings.add("请选择");
                    } else if (strings.size() == 3) {
                        strings.set(2, "请选择");
                    }
                    tabLayout.setupWithViewPager(viewPager);
                    viewPagerAdapter.notifyDataSetChanged();
                    tabLayout.getTabAt(2).select();
                } else {
                    oldAreaSelected = -1;
                    cityAdapter.notifyDataSetChanged();
                    areaAdapter.notifyDataSetChanged();
                    strings.set(1, cityBeans.get(position).getLabel());
                    tabLayout.setupWithViewPager(viewPager);
                    viewPagerAdapter.notifyDataSetChanged();
                    dismiss();
                    areaPickerViewCallback.callback(provinceSelected, citySelected);
                }
            }
        });

        areaBeans = new ArrayList<>();
        areaAdapter = new AreaAdapter(R.layout.item_address, areaBeans);
        LinearLayoutManager areaListManager = new LinearLayoutManager(context);
        areaRecyclerView.setLayoutManager(areaListManager);
        areaRecyclerView.setAdapter(areaAdapter);
        areaAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                strings.set(2, areaBeans.get(position).getLabel());
                tabLayout.setupWithViewPager(viewPager);
                viewPagerAdapter.notifyDataSetChanged();
                areaBeans.get(position).setStatus(true);
                areaSelected = position;
                if (oldAreaSelected != -1 && oldAreaSelected != position) {
                    areaBeans.get(oldAreaSelected).setStatus(false);
                }
                oldAreaSelected = areaSelected;
                areaAdapter.notifyDataSetChanged();
                dismiss();
                areaPickerViewCallback.callback(provinceSelected, citySelected, areaSelected);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        provinceRecyclerView.scrollToPosition(oldProvinceSelected == -1 ? 0 : oldProvinceSelected);
                        break;
                    case 1:
                        cityRecyclerView.scrollToPosition(oldCitySelected == -1 ? 0 : oldCitySelected);
                        break;
                    case 2:
                        areaRecyclerView.scrollToPosition(oldAreaSelected == -1 ? 0 : oldAreaSelected);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return strings.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return strings.get(position);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(views.get(position));
            Log.e("AreaPickView", "------------instantiateItem");
            return views.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(views.get(position));
            Log.e("AreaPickView", "------------destroyItem");
        }

    }

    public interface AreaPickerViewCallback {
        void callback(int... value);
    }

    public void setAreaPickerViewCallback(AreaPickerViewCallback areaPickerViewCallback) {
        this.areaPickerViewCallback = areaPickerViewCallback;
    }

    public void setSelect(int... value) {
        strings = new ArrayList<>();
        if (value == null) {
            strings.add("请选择");
            if (isCreate) {
                tabLayout.setupWithViewPager(viewPager);
                viewPagerAdapter.notifyDataSetChanged();
                tabLayout.getTabAt(0).select();
                if (provinceSelected != -1)
                    addressBeans.get(provinceSelected).setStatus(false);
                if (citySelected != -1)
                    addressBeans.get(provinceSelected).getChildren().get(citySelected).setStatus(false);
                cityBeans.clear();
                areaBeans.clear();
                provinceAdapter.notifyDataSetChanged();
                cityAdapter.notifyDataSetChanged();
                areaAdapter.notifyDataSetChanged();
            }
            return;
        }
        if (value.length == 3) {
            strings.add(addressBeans.get(value[0]).getLabel());
            strings.add(addressBeans.get(value[0]).getChildren().get(value[1]).getLabel());
            strings.add(addressBeans.get(value[0]).getChildren().get(value[1]).getChildren().get(value[2]).getLabel());
            tabLayout.setupWithViewPager(viewPager);
            viewPagerAdapter.notifyDataSetChanged();
            tabLayout.getTabAt(value.length - 1).select();
            if (provinceSelected != -1)
                addressBeans.get(provinceSelected).setStatus(false);
            if (citySelected != -1)
                addressBeans.get(provinceSelected).getChildren().get(citySelected).setStatus(false);
            addressBeans.get(value[0]).setStatus(true);
            addressBeans.get(value[0]).getChildren().get(value[1]).setStatus(true);
            addressBeans.get(value[0]).getChildren().get(value[1]).getChildren().get(value[2]).setStatus(true);
            cityBeans.clear();
            cityBeans.addAll(addressBeans.get(value[0]).getChildren());
            areaBeans.clear();
            areaBeans.addAll(addressBeans.get(value[0]).getChildren().get(value[1]).getChildren());
            provinceAdapter.notifyDataSetChanged();
            cityAdapter.notifyDataSetChanged();
            areaAdapter.notifyDataSetChanged();
            oldProvinceSelected = value[0];
            oldCitySelected = value[1];
            oldAreaSelected = value[2];
            areaRecyclerView.scrollToPosition(oldAreaSelected == -1 ? 0 : oldAreaSelected);
        }

        if (value.length == 2) {
            strings.add(addressBeans.get(value[0]).getLabel());
            strings.add(addressBeans.get(value[0]).getChildren().get(value[1]).getLabel());
            tabLayout.setupWithViewPager(viewPager);
            viewPagerAdapter.notifyDataSetChanged();
            tabLayout.getTabAt(value.length - 1).select();
            addressBeans.get(provinceSelected).setStatus(false);
            addressBeans.get(provinceSelected).getChildren().get(citySelected).setStatus(false);
            addressBeans.get(value[0]).setStatus(true);
            addressBeans.get(value[0]).getChildren().get(value[1]).setStatus(true);
            cityBeans.clear();
            cityBeans.addAll(addressBeans.get(value[0]).getChildren());
            provinceAdapter.notifyDataSetChanged();
            cityAdapter.notifyDataSetChanged();
            oldProvinceSelected = value[0];
            oldCitySelected = value[1];
            oldAreaSelected = -1;
            cityRecyclerView.scrollToPosition(oldCitySelected == -1 ? 0 : oldCitySelected);
        }

    }

}
