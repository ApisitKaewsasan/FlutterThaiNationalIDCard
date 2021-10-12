package com.dotscoket.readercard.acs_card_reader_thailand;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dotscoket.readercard.acs_card_reader_thailand.interfaces.SmartCardDeviceEvent;
import com.dotscoket.readercard.acs_card_reader_thailand.model.PersonalInformation;
import com.dotscoket.readercard.acs_card_reader_thailand.src.SmartCardDevice;
import com.google.gson.Gson;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** AcsCardReaderThailandPlugin */
public class AcsCardReaderThailandPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context context;
  private static final String TAG = "AcsCardReaderThailandPlugin";
  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "acs_card_reader_thailand");
    channel.setMethodCallHandler(this);
    this.context = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if(call.method.equals("acs_card")){

      acsReaderCard(result);
    }else  {
      result.notImplemented();
    }
  }


  void acsReaderCard(Result result){
    SmartCardDevice.getSmartCardDevice(context, new SmartCardDeviceEvent() {
      @Override
      public void OnReady(SmartCardDevice device) {
        Toast.makeText(
                context,
                "OnReady",
                Toast.LENGTH_LONG
        ).show();
      }

      @Override
      public void OnDetached(SmartCardDevice device) {
        Toast.makeText(
                context,
                "Smart Card is removed",
                Toast.LENGTH_LONG
        ).show();
      }

      @Override
      public void OnErrory(String message) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
      }

      @SuppressLint("LongLogTag")
      @Override
      public void OnSuceess(PersonalInformation personalInformation) {
        Gson gson = new Gson();
      try {
        result.success(gson.toJson(personalInformation));
      }catch (Exception e){
        Log.d(TAG,e.getMessage());
      }
      }
    });
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }




}
