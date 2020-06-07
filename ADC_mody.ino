/* 
 * rosserial ADC Example
 * 
 * This is a poor man's Oscilloscope.  It does not have the sampling 
 * rate or accuracy of a commerical scope, but it is great to get
 * an analog value into ROS in a pinch.
 */

#if (ARDUINO >= 100)
 #include <Arduino.h>
#else
 #include <WProgram.h>
#endif
#include <ros.h>
#include <rosserial_arduino/Adc.h>

ros::NodeHandle nh;

rosserial_arduino::Adc adc_msg;
ros::Publisher p("adc", &adc_msg);

void setup()
{ 
  pinMode(A0, INPUT);
  nh.initNode();

  nh.advertise(p);
}

int potVal;
int vol;

void loop()
{
  

  potVal = analogRead(A0);
  vol = map(potVal, 0, 1023,0,100);
  adc_msg.adc0 = vol ;
  //Serial.println(vol);
    
  p.publish(&adc_msg);

  nh.spinOnce();
}
