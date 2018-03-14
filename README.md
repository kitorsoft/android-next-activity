# android-next-activity

##What is this?
Using this simple class, you can define a sequence of activities in your Manifest file and navigate the easily in your code. I needed it to create a kind of "wizard" and thought sharing might help others.

##How to use it
Simply include a tag like this:
    <meta-data android:name="NextActivity" android:value=".SecondActivity"/>
in your activity in your Manifest.

Add NextActivity.java to your project and adjust the naespace.

From your code, you can now call
`executeNext(activity)` passing in the current activity,
or to remove everything from the Activity stack to disable "back", call
`executeNext(activity, true)`
