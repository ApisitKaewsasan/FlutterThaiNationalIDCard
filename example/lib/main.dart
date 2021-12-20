import 'dart:convert';

import 'package:acs_card_reader_thailand/model/personalInformation.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:acs_card_reader_thailand/acs_card_reader_thailand.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  bool isCheck_btn = true;

  @override
  void initState() {
    super.initState();
    initPlatformState();


  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await AcsCardReaderThailand.platformVersion ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  late PersonalInformation personalInformation = PersonalInformation(status: false);

  onConnectDevice() async {
     AcsCardReaderThailand.messageStream.listen((event) async {
      if(event){
        if(isCheck_btn){
          isCheck_btn = false;
          setState(() {});
          personalInformation =
              await AcsCardReaderThailand.acsCardID();
          isCheck_btn = true;
          setState(() {});
        }
      }else{
        isCheck_btn = true;
        personalInformation = PersonalInformation(status: false);
        setState(() {});
      }
    });
  }
  @override
  Widget build(BuildContext context) {
    onConnectDevice();
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Container(
            width: 250,
            child: SingleChildScrollView(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  personalInformation.status!?getImagenBase64(personalInformation.PictureSubFix.toString()):SizedBox(),
                  SizedBox(height: 20,),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Expanded(child: Text('NameTH ')),
                      Expanded(flex: 2, child: Text('${personalInformation
                          .nameTH ??
                          '-------------------- -------------------'} ')),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Expanded(child: Text('NameEN ')),
                      Expanded(flex: 2, child: Text('${personalInformation
                          .nameEN ??
                          '-------------------- -------------------'} ')),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Expanded(child: Text('BirthDate ')),
                      Expanded(flex: 2, child: Text('${personalInformation
                          .birthDate ??
                          '-------------------- -------------------'} ')),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Expanded(child: Text('IDCard ')),
                      Expanded(flex: 2, child: Text('${personalInformation
                          .personalID ??
                          '-------------------- -------------------'} ')),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Expanded(child: Text('Gender ')),
                      Expanded(flex: 2, child: Text('${personalInformation
                          .gender == 1 ? 'ชาย' : personalInformation.gender == 2
                          ? 'หญิง'
                          :
                          '-------------------- -------------------'} ')),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Expanded(child: Text('Adress ')),
                      Expanded(flex: 2, child: Text('${personalInformation
                          .address ??
                          '-------------------- -------------------'} ')),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Expanded(child: Text('CardIssuer ')),
                      Expanded(flex: 2, child: Text('${personalInformation
                          .CardIssuer ??
                          '-------------------- -------------------'} ')),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Expanded(child: Text('IssueDate ')),
                      Expanded(flex: 2, child: Text('${personalInformation
                          .IssueDate ??
                          '-------------------- -------------------'} ')),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      Expanded(child: Text('ExpireDate ')),
                      Expanded(flex: 2, child: Text('${personalInformation
                          .ExpireDate ??
                          '-------------------- -------------------'} ')),
                    ],
                  ),
                  SizedBox(height: 1,),
                  Text("Message_code -> ${personalInformation.Message_code}"),
                  SizedBox(height: 20,),
                  TextButton(
                      style: ButtonStyle(
                        backgroundColor: MaterialStateProperty.all(
                            isCheck_btn?Colors.blueAccent:Colors.grey.shade400),
                      ),
                      onPressed: () async {
                      if(isCheck_btn){
                        isCheck_btn = false;
                        setState(() {});
                        personalInformation =
                        await AcsCardReaderThailand.acsCardID();
                        isCheck_btn = true;
                        setState(() {});
                      }

                      },
                      child: Text("ScanNow CardID",
                        style: TextStyle(color: Colors.white),)),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget getImagenBase64(String imagen) {
    const Base64Codec base64 = Base64Codec();
    if (personalInformation.status == false) return new Container(
      padding: EdgeInsets.all(20),
      color: Colors.grey.shade400,
      child: Icon(Icons.person_add, size: 100, color: Colors.grey.shade500,),
    );

    return Image.memory(
      base64.decode(imagen),
      fit: BoxFit.fitWidth,
    );
  }



}
