
import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

import 'model/personalInformation.dart';

class AcsCardReaderThailand {
  static const MethodChannel _channel = MethodChannel('acs_card_reader_thailand');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<PersonalInformation> acsCardID() async {
    dynamic data = await (_channel.invokeMethod("acs_card"));
    return PersonalInformation.fromJson(jsonDecode(data));
  }
}
