#!/usr/bin/env python
import alsaaudio
import os
import rospy
from std_msgs.msg import String
from rosserial_arduino.msg import Adc

def callback(data):
    rospy.loginfo("recieve data %d", data.adc0)
    volume = data.adc0
    #command = 'osascript -e "Set volume ' + volume + ""
    #os.system(command)    
    mixer = alsaaudio.Mixer()
    mixer.setvolume(volume)
    
def listener():

    # In ROS, nodes are uniquely named. If two nodes with the same
    # name are launched, the previous one is kicked off. The
    # anonymous=True flag means that rospy will choose a unique
    # name for our 'listener' node so that multiple listeners can
    # run simultaneously.
    rospy.init_node('listener', anonymous=True)

    rospy.Subscriber("adc", Adc, callback)
    rospy.loginfo("adc")

    # spin() simply keeps python from exiting until this node is stopped
    rospy.spin()

if __name__ == '__main__':
    listener()
