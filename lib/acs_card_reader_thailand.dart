
import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:flutter/services.dart';

import 'model/personalInformation.dart';


class AcsCardReaderThailand {
  static const MethodChannel _channel = MethodChannel('acs_card_reader_thailand');


  static const EventChannel messageChannel = EventChannel('eventChannelStream');



  static  Stream<bool> get messageStream async* {
    await for (bool message in messageChannel.receiveBroadcastStream().map((message) => message)){
      yield message;
    }
  }

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<PersonalInformation> acsCardID() async {
    return Platform.isAndroid?thailandCard():Future.value(PersonalInformation(status: false,Message_code: 4));
  }

  static Future<PersonalInformation> thailandCard() async {
    dynamic data = await (_channel.invokeMethod("acs_card"));
    return PersonalInformation.fromJson(jsonDecode(data));
  }
}
