package com.remteh.qa;
import android.app.Activity;import android.app.Instrumentation;import android.content.Intent;import android.os.Bundle;import android.graphics.Bitmap;import java.io.File;import java.io.FileOutputStream;
/** Real SDK activity capture and same-identity APK migration; no synthetic controller events. */
public final class ControllerQualityRunner extends Instrumentation {
 private Bundle args;private Activity activity;
 @Override public void onCreate(Bundle input){args=input;start();}
 @Override public void onStart(){Bundle result=new Bundle();try{
  if(!android.os.Build.MODEL.toLowerCase(java.util.Locale.ROOT).contains("sdk"))throw new AssertionError("Disposable SDK emulator only");
  Intent launch=getTargetContext().getPackageManager().getLaunchIntentForPackage(getTargetContext().getPackageName());if(launch==null)throw new AssertionError("Launcher missing");activity=startActivitySync(launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));waitForIdleSync();Thread.sleep(700);
  if("baseline".equals(args.getString("phase"))){if(!activity.getPreferences(0).edit().putString("qa_upgrade_sentinel","synthetic preview migration fixture").commit())throw new AssertionError("Could not seed isolated data");}
  else{
   if(!"synthetic preview migration fixture".equals(activity.getPreferences(0).getString("qa_upgrade_sentinel","")))throw new AssertionError("APK update cleared private data");
   String version=getTargetContext().getPackageManager().getPackageInfo(getTargetContext().getPackageName(),0).versionName;if(!"1.2.1".equals(version))throw new AssertionError("Wrong version: "+version);
   capture("app-1.2.1.png");runOnMainSync(()->activity.setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));for(int i=0;i<50&&activity.getResources().getConfiguration().orientation!=android.content.res.Configuration.ORIENTATION_LANDSCAPE;i++)Thread.sleep(100);waitForIdleSync();Thread.sleep(300);capture("app-1.2.1-landscape.png");
   result.putString("version",version);result.putString("scope","SDK Android 15; no physical controller or paid ad/payment request");
  }
  runOnMainSync(()->activity.finish());waitForIdleSync();result.putString("passed","true");finish(Activity.RESULT_OK,result);
 }catch(Throwable error){result.putString("passed","false");result.putString("failure",error.toString());finish(Activity.RESULT_CANCELED,result);}}
 private void capture(String name)throws Exception{Bitmap image=getUiAutomation().takeScreenshot();File root=getTargetContext().getExternalFilesDir("qa");if(root==null||!root.exists()&&!root.mkdirs())throw new java.io.IOException("Screenshot directory");try(FileOutputStream output=new FileOutputStream(new File(root,name))){image.compress(Bitmap.CompressFormat.PNG,100,output);}image.recycle();}
}
