class PersonalInformation {
  String? address;
  String? birthDate;
  int? gender;
  String? nameEN;
  String? nameTH;
  String? personalID;
  String? pictureTag;
  bool ? status;
  String? message;

  PersonalInformation(
      {this.address,
        this.birthDate,
        this.gender,
        this.nameEN,
        this.nameTH,
        this.personalID,this.pictureTag,this.status,this.message});

  PersonalInformation.fromJson(Map<String, dynamic> json) {
    address = json['Address'];
    birthDate = json['BirthDate'];
    gender = json['Gender'];
    nameEN = json['NameEN'];
    nameTH = json['NameTH'];
    personalID = json['PersonalID'];
    pictureTag = json['PictureTag'];
    status = json['Status'];
    message = json['Message'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['Address'] = this.address;
    data['BirthDate'] = this.birthDate;
    data['Gender'] = this.gender;
    data['NameEN'] = this.nameEN;
    data['NameTH'] = this.nameTH;
    data['PersonalID'] = this.personalID;
    data['PictureTag'] = this.pictureTag;
    data['Status'] = this.status;
    data['Message'] = this.message;
    return data;
  }
}