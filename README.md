# Reference Code   
해당 코드는 아래 링크를 기반에서 수정 되었습니다.     
https://github.com/bauerjj/Android-Simple-Bluetooth-Example    

# 안드로이드 Code    
아래 링크위치에 Code 코드 있음   
https://github.com/minwoominwoominwoo7/bluetoothSerial/tree/master/Android/Android-Simple-Bluetooth-Example-master   
빌드된 APK 는 아래 위치에 있음   
https://github.com/minwoominwoominwoo7/bluetoothSerial/tree/master/Android/Android-Simple-Bluetooth-Example-master/app/app-debug.apk   

# 아두이노 code 
1. ESP32 Feathe( BT 내장 ) board 기반으로 동작 확인    
https://github.com/minwoominwoominwoo7/bluetoothSerial/tree/master/Arduino/ESP32_feather/SerialToSerialBT   
2. Arduino Yun + HC06 기반으로 빌드까지만 확인   
https://github.com/minwoominwoominwoo7/bluetoothSerial/tree/master/Arduino/YUN_HC_06/SerialToSerialBT_HC06   

# 사용법    
1. 아두이노 실행   
2. 안드로이드 폰에서 아두이노 BT 디바이스를 찾아 페어링 시켜 둠.   
3. 안드로이드 앱 실행   
4. 안드로이드 앱에서 SHOW PAIRED DEVICES 버튼 눌러 블루투스 장치 찾아 연결   
5. GO Next State 버튼을 눌러 순차적으로 메세지를 보냄.   
   폰 화면상 TX는 아두이노로 보내는  데이타이고 RX 는 아두이노로 부터 받는 데이타임.   
   아두이노상 시리얼 포트 Log를 보면 받은 Data 가  로그로 표시됨.  

# 프로토콜 추가된 사항    
파워포인트를 기반으로 하고 아래 부분을 추가함.     
1. 전체 길이를 255 byte로 고정하고 아래처럼 고정 위치에 데이타 할당    
Type 0~0 Byte 할당   
Value 1 ~ 200 Byte 할당   
 - 1 ~ 50 Byte 가게 이름 할당  ( Type 31일 경우 A5A5로 할당 )   
 - 51 ~ 100 Byte 가게 전화번호 할당    
 - 101 ~150 Byte 메세지 할당  
 - 151 ~200 Byte 가격 할당  
PhoneNum 201 ~ 250 Byte 할당  
Fcs 251~254 Byte 할당  
2. Value에서 문장의 끝나는 부분을 인지하기 위하여 . 마침표 사용.    
  즉 보낼 내용이 없을 경우더라도 해당 영역에 . 를 넣어 주어야함.   
  ex) char * shopPriceString= ".";  


