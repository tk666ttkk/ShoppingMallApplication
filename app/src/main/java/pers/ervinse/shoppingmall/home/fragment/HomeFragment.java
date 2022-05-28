package pers.ervinse.shoppingmall.home.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pers.ervinse.shoppingmall.BaseFragment;
import pers.ervinse.shoppingmall.R;
import pers.ervinse.shoppingmall.home.adapter.HomeAdapter;
import pers.ervinse.shoppingmall.domain.Goods;
import pers.ervinse.shoppingmall.utils.OkhttpUtils;


public class HomeFragment extends BaseFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private Handler handler = new Handler();

    private RecyclerView rvHome;
    private ImageView ib_top;
    private TextView tv_search_home;
    private TextView tv_message_home;

    private HomeAdapter adapter;

    List<Goods> goodsList;

    /**
     * 初始化视图
     *
     * @return
     */
    @Override
    public View initView() {
        Log.i(TAG, "主页视图初始化");
        //为当前fragment加载布局文件
        View view = View.inflate(mContext, R.layout.fragment_home, null);
        rvHome = view.findViewById(R.id.rv_home);
        tv_search_home = view.findViewById(R.id.tv_search_home);
        tv_message_home = view.findViewById(R.id.tv_message_home);
        return view;
    }

    public void initData() {
        super.initData();
        Log.i(TAG, "主页数据初始化");

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "进入获取商品线程");


                Gson gson = new Gson();
                String responseJson = null;
                try {
                    //发送登录请求
                    responseJson = OkhttpUtils.doGet("http://192.168.1.8:8088/goods/getHotGoods");
                    Log.i(TAG, "获取热点商品响应json:" + responseJson);
                    goodsList = gson.fromJson(responseJson, new TypeToken<List<Goods>>() {
                    }.getType());
                    Log.i(TAG, "获取热点响应解析对象:" + goodsList);
                    //加创建商品布局
                    if (goodsList != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                //创建首页循环视图适配器,加载数据
                                adapter = new HomeAdapter(goodsList, mContext);
                                //循环视图加载适配器
                                rvHome.setAdapter(adapter);
                                //创建网格布局
                                GridLayoutManager manager = new GridLayoutManager(mContext, 2);
                                //循环视图加载网格布局
                                rvHome.setLayoutManager(manager);

                            }
                        });
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(mContext, "获取数据失败,服务器错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }.start();
    }

    @Override
    public void refreshData() {
        Log.i(TAG, "联网刷新数据");
        new Thread() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                Log.i(TAG, "进入获取商品线程");

                Gson gson = new Gson();
                String responseJson = null;

                try {
                    //发送登录请求
                    responseJson = OkhttpUtils.doGet("http://192.168.1.8:8088/goods");
                    Log.i(TAG, "获取商品响应json:" + responseJson);
                    goodsList = gson.fromJson(responseJson, new TypeToken<List<Goods>>() {
                    }.getType());
                    Log.i(TAG, "获取商品响应解析对象:" + goodsList);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (adapter != null) {
                                adapter.setGoodsList(goodsList);
                                adapter.flushView();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(mContext, "获取数据失败,服务器错误", Toast.LENGTH_SHORT).show();
                    Looper.loop();

                }
            }
        }.start();
    }

    @Override
    public void saveData() {

    }
}
