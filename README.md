# Flutter ThaiNationalIDCard

Flutter ThaiNationnalIDCard  This work is made for use in Flutter only and can be further developed from the original code.

Support Device

-  acs acr39u icc reader

Suport OS

- Android

APDU Command

Get response 0x00, 0xC0, 0x00, 0x00 + Len in hex

INS 0xB0 | Read Bin INS 0xC0 | Get response

SELECT FILE CLA = 0X00 INS = 0XA4 P1 = 0x04 | Direct selection by DF name (data field=DF name) P2 = 0x00 Lc = 0x08 ( len of DF Name )

Thailand Personal DF Name 0xA0, 0X00, 0x00, 0x00, 0x54, 0x48, 0x00, 0x01

| Description     | CLA  | INS  | P1   | P2   | Lc   | Data                                           | Le   |
| --------------- | ---- | ---- | ---- | ---- | ---- | ---------------------------------------------- | ---- |
| Select          | 0x00 | 0xA4 | 0X04 | 0x00 | 0x08 | 0xA0, 0X00, 0x00, 0x00, 0x54, 0x48, 0x00, 0x01 |      |
| GET RESPONSE    | 0X00 | 0XC0 | 0x00 | 0x00 |      |                                                |      |
| CID             | 0x80 | 0xB0 | 0x00 | 0x04 | 0x02 | 0x00                                           | 0x0D |
| TH Fullname     | 0x80 | 0xB0 | 0x00 | 0x11 | 0x02 | 0x00                                           | 0x64 |
| EN Fullname     | 0x80 | 0xB0 | 0x00 | 0x75 | 0x02 | 0x00                                           | 0x64 |
| Date of birth   | 0x80 | 0xB0 | 0x00 | 0xD9 | 0x02 | 0x00                                           | 0x08 |
| Gender          | 0x80 | 0xB0 | 0x00 | 0xE1 | 0x02 | 0x00                                           | 0x01 |
| Card Issuer     | 0x80 | 0xB0 | 0x00 | 0xF6 | 0x02 | 0x00                                           | 0x64 |
| Issue Date      | 0x80 | 0xB0 | 0x01 | 0x67 | 0x02 | 0x00                                           | 0x08 |
| Expire Date     | 0x80 | 0xB0 | 0x01 | 0x6F | 0x02 | 0x00                                           | 0x08 |
| Address         | 0x80 | 0xB0 | 0x15 | 0x79 | 0x02 | 0x00                                           | 0x64 |
| Photo_Part1/20  | 0x80 | 0xB0 | 0x01 | 0x7B | 0x02 | 0x00                                           | 0xFF |
| Photo_Part2/20  | 0x80 | 0xB0 | 0x02 | 0x7A | 0x02 | 0x00                                           | 0xFF |
| Photo_Part3/20  | 0x80 | 0xB0 | 0x03 | 0x79 | 0x02 | 0x00                                           | 0xFF |
| Photo_Part4/20  | 0x80 | 0xB0 | 0x04 | 0x78 | 0x02 | 0x00                                           | 0xFF |
| Photo_Part5/20  | 0x80 | 0xB0 | 0x05 | 0x77 | 0x02 | 0x00                                           | 0xFF |
| Photo_Part6/20  | 0x80 | 0xB0 | 0x06 | 0x76 | 0x02 | 0x00                                           | 0xFF |
| Photo_Part7/20  | 0x80 | 0xB0 | 0x07 | 0x75 | 0x02 | 0x00                                           | 0xFF |
| Photo_Part8/20  | 0x80 | 0xB0 | 0x08 | 0x74 | 0x02 | 0x00                                           | 0xFF |
| Photo_Part9/20  | 0x80 | 0xB0 | 0x09 | 0x73 | 0x02 | 0x00                                           | 0xFF |
| Photo_Part10/20 | 0x80 | 0xB0 | 0x0A | 0x72 | 0x02 | 0x00                                           | 0xFF |
| Photo_Part11/20 | 0x80 | 0xB0 | 0x0B | 0x71 | 0x02 | 0x00                                           | 0xFF |
| Photo_Part12/20 | 0x80 | 0xB0 | 0x0C | 0x70 | 0x02 | 0x00                                           | 0xFF |
| Photo_Part13/20 | 0x80 | 0xB0 | 0x0D | 0x6F | 0x02 | 0x00                                           | 0xFF |
| Photo_Part14/20 | 0x80 | 0xB0 | 0x0E | 0x6E | 0x02 | 0x00                                           | 0xFF |
| Photo_Part15/20 | 0x80 | 0xB0 | 0x0F | 0x6D | 0x02 | 0x00                                           | 0xFF |
| Photo_Part16/20 | 0x80 | 0xB0 | 0x10 | 0x6C | 0x02 | 0x00                                           | 0xFF |
| Photo_Part17/20 | 0x80 | 0xB0 | 0x11 | 0x6B | 0x02 | 0x00                                           | 0xFF |
| Photo_Part18/20 | 0x80 | 0xB0 | 0x12 | 0x6A | 0x02 | 0x00                                           | 0xFF |
| Photo_Part19/20 | 0x80 | 0xB0 | 0x13 | 0x69 | 0x02 | 0x00                                           | 0xFF |
| Photo_Part20/20 | 0x80 | 0xB0 | 0x14 | 0x68 | 0x02 | 0x00                                           | 0xFF |

# Credits

- [ThaiNationalIDCard](https://github.com/chakphanu/ThaiNationalIDCard)
- [ThaiNationalIDCard.NET](