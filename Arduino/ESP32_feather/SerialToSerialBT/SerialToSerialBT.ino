//This example code is in the Public Domain (or CC0 licensed, at your option.)
//By Evandro Copercini - 2018
//
//This example creates a bridge between Serial and Classical Bluetooth (SPP)
//and also demonstrate that SerialBT have the same functionalities of a normal Serial

#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif
#define data_size 255 // type 1byte, value 50 byte , Phone Num 20 byte, Fcs 4 byt 

BluetoothSerial SerialBT;

byte rData[data_size] ;
byte rType[1] ;
byte rValue[200] ;
byte rPhoneNum[50] ;
byte rFcs[4] ;                         
int curStatus = 0; 

byte tData[data_size] ;
byte tType[1] ;
byte tValue[200] ;
byte tPhoneNum[50] ;
byte tFcs[4] ; 

void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32test"); //Bluetooth device name
  Serial.println("The device started, now you can pair it with bluetooth!");
}

void loop() {

  if (SerialBT.available() >= data_size) // this will only be true if there are at least 9 bytes to read
  {
    for (int i=0; i <data_size ; i++)
    {
      rData[i] = SerialBT.read();     
      //Serial.print(rData[i],HEX);
      //Serial.print(",");                      
    }
    //Serial.println("");

    Serial.println("Recieve Message");

    //read type 
    char hexCar[2];    
    memcpy(rType, rData, sizeof(rType));
    sprintf(hexCar, "Type 0x%02X", rType[0]);
    Serial.println(hexCar);

    //read value
    memcpy(rValue , rData + sizeof(rType), sizeof(rValue));
    
    String rShopName = String((char *)rValue+2);
    rShopName = rShopName.substring(0, rShopName.indexOf(".")); 
    Serial.print("ShopName ");
    Serial.println(rShopName);

    String rShopNum = String((char *)rValue+52);
    rShopNum = rShopNum.substring(0, rShopNum.indexOf(".")); 
    Serial.print("ShopNum ");
    Serial.println(rShopNum);

    String rShopMessage = String((char *)rValue+102);
    rShopMessage = rShopMessage.substring(0, rShopMessage.indexOf(".")); 
    Serial.print("ShopMessage ");
    Serial.println(rShopMessage);

    String rShopPrice = String((char *)rValue+152);
    rShopPrice = rShopPrice.substring(0, rShopPrice.indexOf(".")); 
    Serial.print("ShopPrice ");
    Serial.println(rShopPrice);

    //read phone number
    memcpy(rPhoneNum, rData + 1 + 200, 50);
    String rPhoneNumString = String((char *)rPhoneNum);
    rPhoneNumString = rPhoneNumString.substring(0, rPhoneNumString.indexOf("."));
    Serial.print("Phone Number "); 
    Serial.println(rPhoneNumString);    
  }  

  if(rType[0] == 0x31){

    ///type ////////
    byte tempType[] = {0x32};
    memcpy(tData, tempType, sizeof(tempType));

    ///value ////////
    byte shopNameHeader[] = {0x0A,0x01} ;
    char * shopNameString = "행복한 가계."; 
    byte shopNumHeader[] = {0x0A,0x02} ; 
    char * shopNumString = "0289338902."; 
    byte shopMessageHeader[] = {0x0A,0x03} ; 
    char * shopMessageString = "오늘도 즐거움 하루 되세요."; 
    byte shopPriceHeader[]= {0x0A,0x04};  
    char * shopPriceString= ".";  
    
    memcpy(tData+1, shopNameHeader, 2); 
    memcpy(tData+1+2, (byte *)shopNameString, 48); 
    memcpy(tData+1+2+48, shopNumHeader, 2); 
    memcpy(tData+1+2+48+2, (byte * )shopNumString, 48); 
    memcpy(tData+1+2+48+2+48, shopMessageHeader, 2); 
    memcpy(tData+1+2+48+2+48+2, shopMessageString, 48); 
    memcpy(tData+1+2+48+2+48+2+48, shopPriceHeader, 2);
    memcpy(tData+1+2+48+2+48+2+48+2, (byte *)shopPriceString, 48);

    ///phone Num ////////
    char * tempPhoneNum = "38323130313131313333333788.";    
    memcpy(tData+1+200, (byte *)tempPhoneNum, 50);
    
    ///fcs ////////
    byte tempFcs[]= {0x39,0x39,0x39,0x39} ;    
    memcpy(tData+1+200+50, tempFcs, sizeof(tempFcs));
    SerialBT.write(tData, sizeof(tData));
    
  }else if(rType[0] == 0x33){

    ///type ////////
    byte tempType[] = {0x34};
    memcpy(tData, tempType, sizeof(tempType));

    ///value ////////
    byte shopNameHeader[] = {0x0A,0x01} ;
    char * shopNameString = "행복한 오늘."; 
    byte shopNumHeader[] = {0x0A,0x02} ; 
    char * shopNumString = "0289338902."; 
    byte shopMessageHeader[] = {0x0A,0x03} ; 
    char * shopMessageString = "오늘도 즐거운 하루 되세요."; 
    byte shopPriceHeader[]= {0x0A,0x04};  
    char * shopPriceString= ".";  
    
    memcpy(tData+1, shopNameHeader, 2); 
    memcpy(tData+1+2, (byte *)shopNameString, 48); 
    memcpy(tData+1+2+48, shopNumHeader, 2); 
    memcpy(tData+1+2+48+2, (byte * )shopNumString, 48); 
    memcpy(tData+1+2+48+2+48, shopMessageHeader, 2); 
    memcpy(tData+1+2+48+2+48+2, shopMessageString, 48); 
    memcpy(tData+1+2+48+2+48+2+48, shopPriceHeader, 2);
    memcpy(tData+1+2+48+2+48+2+48+2, (byte *)shopPriceString, 48);

    ///phone Num ////////
    char * tempPhoneNum = "38323130313131313333333788.";    
    memcpy(tData+1+200, (byte *)tempPhoneNum, 50);
    
    ///fcs ////////
    byte tempFcs[]= {0x39,0x39,0x39,0x39} ;    
    memcpy(tData+1+200+50, tempFcs, sizeof(tempFcs));
    SerialBT.write(tData, sizeof(tData));
  }else if(rType[0] == 0x35){

    ///type ////////
    byte tempType[] = {0x36};
    memcpy(tData, tempType, sizeof(tempType));

    ///value ////////
    byte shopNameHeader[] = {0x0A,0x01} ;
    char * shopNameString = "."; 
    byte shopNumHeader[] = {0x0A,0x02} ; 
    char * shopNumString = "."; 
    byte shopMessageHeader[] = {0x0A,0x03} ; 
    char * shopMessageString = "."; 
    byte shopPriceHeader[]= {0x0A,0x04};  
    char * shopPriceString= "17000.";  
    
    memcpy(tData+1, shopNameHeader, 2); 
    memcpy(tData+1+2, (byte *)shopNameString, 48); 
    memcpy(tData+1+2+48, shopNumHeader, 2); 
    memcpy(tData+1+2+48+2, (byte * )shopNumString, 48); 
    memcpy(tData+1+2+48+2+48, shopMessageHeader, 2); 
    memcpy(tData+1+2+48+2+48+2, shopMessageString, 48);
    memcpy(tData+1+2+48+2+48+2+48, shopPriceHeader, 2);
    memcpy(tData+1+2+48+2+48+2+48+2, (byte *)shopPriceString, 48);

    ///phone Num ////////
    char * tempPhoneNum = "38323130313131313333333788.";    
    memcpy(tData+1+200, (byte *)tempPhoneNum, 50);
    
    ///fcs ////////
    byte tempFcs[]= {0x39,0x39,0x39,0x39} ;    
    memcpy(tData+1+200+50, tempFcs, sizeof(tempFcs));
    SerialBT.write(tData, sizeof(tData));
  }
  delay(500);
}
