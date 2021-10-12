
class PersonalInformation {
  String? address;
  String? birthDate;
  int? gender;
  String? nameEN;
  String? nameTH;
  String? personalID;
  String? PicturePreFix;
  String? PictureSubFix;
  String? CardIssuer;
  String? IssueDate;
  String? ExpireDate;
  bool ? status;
  int? Message_code;

  PersonalInformation(
      {this.address,
        this.birthDate,
        this.gender,
        this.nameEN,
        this.nameTH,
        this.personalID,this.PicturePreFix,this.PictureSubFix,this.status,this.Message_code});

  PersonalInformation.fromJson(Map<String, dynamic> json) {
    address = json['Address'];
    birthDate = json['BirthDate'];
    gender = json['Gender'];
    nameEN = json['NameEN'];
    nameTH = json['NameTH'];
    personalID = json['PersonalID'];
    PicturePreFix = json['PicturePreFix'];
    PictureSubFix = json['PictureSubFix'];
    CardIssuer = json['CardIssuer'];
    IssueDate = json['IssueDate'];
    ExpireDate = json['ExpireDate'];
    status = json['Status'];
    Message_code = json['Message_code'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['Address'] = this.address;
    data['BirthDate'] = this.birthDate;
    data['Gender'] = this.gender;
    data['NameEN'] = this.nameEN;
    data['NameTH'] = this.nameTH;
    data['PersonalID'] = this.personalID;
    data['PicturePreFix'] = this.PicturePreFix;
    data['PictureSubFix'] = this.PictureSubFix;
    data['CardIssuer'] = this.CardIssuer;
    data['IssueDate'] = this.IssueDate;
    data['ExpireDate'] = this.ExpireDate;
    data['Status'] = this.status;
    data['Message_code'] = this.Message_code;
    return data;
  }
}