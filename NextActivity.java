package kitorsoft.com.poc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.HashMap;

public class NextActivity
{
    private static String TAG = NextActivity.class.getCanonicalName();

    static private final HashMap<Class, Class> mMap =  new HashMap<Class, Class>();

    /***
     * reads the map from Manifest
     * manifest defines <meta-data android:name="NextActivity" android:value=".activities.SettingsActivity"/>
     *  in activity
      */

    private static void init(Context ctx)
    {
        if(mMap.size() == 0)
        {
            String packageName = ctx.getPackageName();

            PackageInfo pi = null;
            try
            {
                pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
            if(pi != null)
            {
                for (ActivityInfo ai : pi.activities)
                {
                    if(ai.metaData != null)
                    {
                        String nextActivityName = ai.metaData.getString("NextActivity");
                        // replace any leading "." with package name, so class can be found
                        if (nextActivityName != null)
                        {
                            nextActivityName = nextActivityName.replaceAll("^\\.", packageName + "\\.");
                        }
                        if (nextActivityName != null && !nextActivityName.equals(""))
                        {
                            Class nextActivity = null;
                            Class currentActivity = null;
                            try
                            {
                                // requires canonical class name
                                nextActivity = Class.forName(nextActivityName);
                                currentActivity = Class.forName(ai.name);
                            }
                            catch (ClassNotFoundException e)
                            {
                                e.printStackTrace();
                            }
                            if (nextActivity != null)
                            {
                                Log.d(TAG, "Adding: " + currentActivity.getCanonicalName() + " - " + nextActivity.getCanonicalName());
                                mMap.put(currentActivity, nextActivity);
                            }
                        }
                    }
                }
            }
        }
    }


    private static Class getNext(Activity current)
    {
        init(current);
        Class retVal = null;

        retVal = mMap.get(current.getClass());

        return retVal;
    }

    public static void executeNext(Activity activity)
    {
        executeNext(activity, false);
    }

    public static void executeNext(Activity activity, boolean deleteStack)
    {
        Class c = getNext(activity);
        if (c == null)
        {
            Log.e(TAG, "No activity defined after " + activity.getClass().getCanonicalName());
            return;
        }
        Log.d(TAG, "Navigating from class " +
                activity.getClass().getCanonicalName() + " to " +
                c.getCanonicalName());
        Intent intent = new Intent(activity, c);
        intent.setAction(activity.getIntent().getAction());
        if(deleteStack)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        activity.startActivity(intent);
    }
}
