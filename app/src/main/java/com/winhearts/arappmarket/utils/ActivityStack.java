package com.winhearts.arappmarket.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * 储存activity堆栈信息
 */
public class ActivityStack {
    private final String TAG = "ActivityStack";
    private static Stack<Activity> activityStack;
    private static ActivityStack instance;

    private ActivityStack() {
    }

    public static ActivityStack getActivityStack() {
        if (instance == null) {
            instance = new ActivityStack();
        }

        return instance;
    }

    public void popActivity() {
        try {
            if (activityStack.size() > 0) {
                Activity activity = activityStack.lastElement();
                if (activity != null) {
                    activityStack.remove(activity);
                }
            }
        } catch (Exception e) {
            LogDebugUtil.d(TAG, e.toString());
        }
    }


    public void clearActivity() {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            Activity activity = activityStack.get(i);
            popActivity(activity);
        }
    }

    public void clearOtherActivity(Activity activity) {
        int size = activityStack.size();
        for (int i = size - 1; i >= 0; i--) {
            Activity a = activityStack.get(i);
            if (a != activity) {
                popActivity(a);
            }
        }
    }

    public void pullActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
        }
    }

    public Activity currentActivity() {
        return activityStack.lastElement();
    }

    public Stack<Activity> getStacks() {
        return activityStack;
    }

    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    public void popAllActivityExceptOne(Class<?> cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }

    public void finishActivity(int times) {
        int size = activityStack.size();
        int time = times;
        while (time > 0) {
            Activity activity = activityStack.get(size - times);
            if (activity != null) {
                activity.finish();
                activityStack.remove(activity);
                activity = null;
            }
            time--;
        }

    }
}