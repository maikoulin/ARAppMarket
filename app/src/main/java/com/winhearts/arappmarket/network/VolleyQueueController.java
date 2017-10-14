package com.winhearts.arappmarket.network;

import android.content.Context;

import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;
import com.winhearts.arappmarket.activity.VpnStoreApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by lmh on 2015/11/16.
 */
public class VolleyQueueController {
    public final String TAG = "RequesterDefaultTag";
    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "volley";
    /**
     * Number of network request dispatcher threads to start.
     */
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 1;
    private RequestQueue mRequestQueue;
    private RequestQueue mAsyncRequestQueue;
    private static VolleyQueueController mInstance;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static VolleyQueueController getInstance() {
        if (mInstance == null) {
            synchronized (VolleyQueueController.class) {
                if (mInstance == null) {
                    mInstance = new VolleyQueueController(VpnStoreApplication.getApp());
                }
            }
        }
        return mInstance;
    }

    private VolleyQueueController(Context context) {
        HurlStack stack = new SelfSignSslOkHttpStack();
        mRequestQueue = newRequestQueue(context, stack);
        mAsyncRequestQueue = newAsyncRequestQueue(context, stack);
    }

    private RequestQueue newRequestQueue(Context context, HurlStack stack) {
        Network network = new BasicNetwork(stack);
        RequestQueue queue = new RequestQueue(new NoCache(), network);
        queue.start();
        return queue;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public RequestQueue getmAsyncRequestQueue() {
        return mAsyncRequestQueue;
    }

    private RequestQueue newAsyncRequestQueue(Context context, HurlStack stack) {
        //获取volley的缓存文件
//        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        BasicNetwork network1 = new BasicNetwork(stack);
        //修改Volley的请求队列，构键新的线程池,因为服务端没有做缓存的处理，移动端也不进行缓存
        RequestQueue queue1 = new RequestQueue(new NoCache(), network1, DEFAULT_NETWORK_THREAD_POOL_SIZE,
                new ExecutorDelivery(executorService));
        queue1.start();
        return queue1;
    }

    public void cancelAll(Object tag) {
        mRequestQueue.cancelAll(tag);
        mAsyncRequestQueue.cancelAll(tag);
    }
}
