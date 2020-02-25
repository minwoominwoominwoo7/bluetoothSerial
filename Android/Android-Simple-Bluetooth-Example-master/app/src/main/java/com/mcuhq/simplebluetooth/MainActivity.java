package com.mcuhq.simplebluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // GUI Components
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private TextView mSendBuffer;
    private Button mNextBtn;
    private Button mInitBtn;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private CheckBox mLED1;

    private final String TAG = MainActivity.class.getSimpleName();
    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private int data_size = 255;

    byte[] rType = new byte[1];
    byte[] rValue = new byte[200];
    byte[] rPhoneNum = new byte[50];
    byte[] rFcs= new byte[4];
    boolean start = true ;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothStatus = (TextView)findViewById(R.id.bluetoothStatus);
        mReadBuffer = (TextView) findViewById(R.id.readBuffer);
        mSendBuffer = (TextView) findViewById(R.id.sendBuffer);
        mNextBtn = (Button)findViewById(R.id.next);
        mInitBtn = (Button)findViewById(R.id.init);
        mScanBtn = (Button)findViewById(R.id.scan);
        mOffBtn = (Button)findViewById(R.id.off);
        mDiscoverBtn = (Button)findViewById(R.id.discover);
        mListPairedDevicesBtn = (Button)findViewById(R.id.PairedBtn);
        mLED1 = (CheckBox)findViewById(R.id.checkboxLED1);

        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);
        start = true;

        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){

                if(msg.what == MESSAGE_READ){

                    byte[] rbytes = new byte[data_size];
                    System.arraycopy((byte[]) msg.obj,0, rbytes,0, rbytes.length);
                    //mReadBuffer.setText(byteArrayToHex(rbytes));
                    //Log.d("test","read rbytes" + byteArrayToHex(rbytes));

                    System.arraycopy(rbytes,0, rType,0, rType.length);
                    System.arraycopy(rbytes,rType.length, rValue,0, rValue.length);
                    System.arraycopy(rbytes,rType.length + rValue.length, rPhoneNum,0, rPhoneNum.length);
                    System.arraycopy(rbytes,rType.length + rValue.length + rPhoneNum.length, rFcs,0, rFcs.length);

                    //Log.d("test", "rType " + byteArrayToHex(rbytes)) ;

                    byte[] shopNameByte =  new byte[48] ;
                    System.arraycopy(rValue,2, shopNameByte,0, shopNameByte.length);
                    String shopName = new String(shopNameByte);
                    int idx = shopName.indexOf(".");
                    shopName= shopName.substring(0, idx);
                    Log.d("test", "shopName " + shopName) ;

                    byte[]   shopNumByte  =  new byte[48] ;
                    System.arraycopy(rValue,50+2, shopNumByte,0, shopNumByte.length);
                    String shopNum = new String(shopNumByte);
                    idx = shopNum.indexOf(".");
                    shopNum= shopNum.substring(0, idx);
                    Log.d("test", "shopNum " + shopNum) ;

                    byte[]   shopMessageByte  =  new byte[48];
                    System.arraycopy(rValue,100+2, shopMessageByte,0, shopMessageByte.length);
                    String shopMessage = new String(shopMessageByte);
                    idx = shopMessage.indexOf(".");
                    shopMessage= shopMessage.substring(0, idx);
                    Log.d("test", "shopMessage " + shopMessage) ;

                    byte[]  shopPriceByte  =  new byte[48] ;
                    System.arraycopy(rValue,150+2, shopPriceByte,0, shopPriceByte.length);
                    String shopPrice = new String(shopPriceByte);
                    idx = shopPrice.indexOf(".");
                    shopPrice= shopPrice.substring(0, idx);
                    Log.d("test", "shopPrice  " + shopPrice)  ;

                    String phoneNum = new String(rPhoneNum);
                    idx = phoneNum.indexOf(".");
                    phoneNum= phoneNum.substring(0, idx);
                    Log.d("test", "rPhoneNum " + phoneNum ) ;
                    Log.d("test", "rFcs " + byteArrayToHex(rFcs)) ;

                    mReadBuffer.setText("Type  " + byteArrayToHex(rType) + "\n" +
                            "shopName  " + shopName + "\n" +
                            "shopNum  " + shopNum + "\n" +
                            "shopMessage  " + shopMessage + "\n" +
                            "shopPrice  " + shopPrice + "\n" +
                            "phoneNum  " + phoneNum + "\n" +
                            "rFcs  " + byteArrayToHex(rFcs) + "\n" );

                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }

        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {

            mNextBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(mConnectedThread != null) //First check to make sure thread created
                    {
                        //mConnectedThread.write("1");

                        byte[] tType = new byte[1];
                        byte[] tValue = new byte[200];
                        byte[] tPhoneNumByte = new byte[50];
                        byte[] tFcs = new byte[4];
                        byte[] tBytes = new byte[255]; //255

                        if ( start ){
                            start = false ;

                            ///type setting
                            tType[0] = 0x31;

                            ///Value Setting
                            byte[]tempValue= {(byte) 0xA5, (byte)0xA5};
                            System.arraycopy(tempValue,0, tValue,0, tempValue.length);

                            ///phoneNum setting
                            String tPhoneNum = "383231303131313133333333." ;
                            try {
                                tPhoneNumByte = tPhoneNum.getBytes("utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            ///fcs setting
                            byte[]tempFcs= {0x39,0x39,0x39,0x39};
                            System.arraycopy(tempFcs,0, tFcs,0, tempFcs.length);

                            System.arraycopy(tType,0, tBytes,0, tType.length);
                            System.arraycopy(tValue,0, tBytes, 1, tValue.length);
                            System.arraycopy(tPhoneNumByte,0,tBytes, 1+200, tPhoneNumByte.length);
                            System.arraycopy(tFcs,0, tBytes,1+200+50, tFcs.length);
                            mConnectedThread.write(tBytes);

                            //mSendBuffer.setText(byteArrayToHex(tBytes));
                            mSendBuffer.setText("Type  " + byteArrayToHex(tType) + "\n" +
                                    //"shopName  " + shopName + "\n" +
                                    //"shopNum  " + shopNum + "\n" +
                                    //"shopMessage  " + shopMessage + "\n" +
                                    //"shopPrice  " + shopPrice + "\n" +
                                    "phoneNum  " + tPhoneNum + "\n" +
                                    "Fcs  " + byteArrayToHex(tFcs) + "\n"
                                    );

                        }else if ( rType[0] == 0x32 ){

                            ///type setting
                            tType[0] = 0x33;
                            ///Value Setting
                            byte[]tempShopNameHeader= {(byte) 0x0A, (byte)0x01};
                            String tempShopName = "행복한 오늘.";
                            byte[] tempShopNameContext = new byte[48] ;
                            try {
                                tempShopNameContext = tempShopName.getBytes("utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            byte[]tempShopNumHeader= {(byte) 0x0A, (byte)0x02};
                            String tempShopNum = "0289338902.";
                            byte[] tempShopNumContext = new byte[48] ;
                            try {
                                tempShopNumContext = tempShopNum.getBytes("utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            byte[]tempShopMessageHeader= {(byte) 0x0A, (byte)0x03};
                            String tempShopMessage = "오늘도 즐거운 하루되세요.";
                            byte[] tempShopMessageContext = new byte[48] ;
                            try {
                                tempShopMessageContext = tempShopMessage.getBytes("utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } ;

                            System.arraycopy(tempShopNameHeader,0, tValue,0, tempShopNameHeader.length);
                            System.arraycopy(tempShopNameContext,0, tValue,2, tempShopNameContext.length);
                            System.arraycopy(tempShopNumHeader,0, tValue,2+48, tempShopNumHeader.length);
                            System.arraycopy(tempShopNumContext,0, tValue,2+48+2, tempShopNumContext.length);
                            System.arraycopy(tempShopMessageHeader,0, tValue,2+48+2+48, tempShopMessageHeader.length);
                            System.arraycopy(tempShopMessageContext,0, tValue,2+48+2+48+2, tempShopMessageContext.length);

                            ///phoneNum setting
                            String tPhoneNum = "383231303131313133333333." ;
                            try {
                                tPhoneNumByte = tPhoneNum.getBytes("utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            ///fcs setting
                            byte[]tempFcs= {0x39,0x39,0x39,0x39};
                            System.arraycopy(tempFcs,0, tFcs,0, tempFcs.length);


                            System.arraycopy(tType,0, tBytes,0, tType.length);
                            System.arraycopy(tValue,0, tBytes, 1, tValue.length);
                            System.arraycopy(tPhoneNumByte,0,tBytes, 1+200, tPhoneNumByte.length);
                            System.arraycopy(tFcs,0, tBytes,1+200+50, tFcs.length);
                            mConnectedThread.write(tBytes);

                            //mSendBuffer.setText(byteArrayToHex(tBytes));
                            mSendBuffer.setText("Type  " + byteArrayToHex(tType) + "\n" +
                                            "shopName  " + tempShopName + "\n" +
                                            "shopNum  " + tempShopNum + "\n" +
                                            "shopMessage  " + tempShopMessage + "\n" +
                                            //"shopPrice  " + shopPrice + "\n" +
                                            "phoneNum  " + tPhoneNum + "\n" +
                                            "Fcs  " + byteArrayToHex(tempFcs) + "\n"
                            );

                        }else if ( rType[0] == 0x34 ){

                            ///type setting
                            tType[0] = 0x35;

                            ///Value Setting
                            byte[]tempShopPriceHeader= {(byte) 0x0A, (byte)0x04};
                            String tempShopPrice = "17000.";
                            byte[] tempShopPriceContext = new byte[48] ;
                            try {
                                tempShopPriceContext = tempShopPrice.getBytes("utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } ;

                            System.arraycopy(tempShopPriceHeader,0, tValue,2+48+2+48+2+48, tempShopPriceHeader.length);
                            System.arraycopy(tempShopPriceContext,0, tValue,2+48+2+48+2+48+2, tempShopPriceContext.length);

                            ///phoneNum setting
                            String tPhoneNum = "383231303131313133333333." ;
                            try {
                                tPhoneNumByte = tPhoneNum.getBytes("utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            ///fcs setting
                            byte[]tempFcs= {0x39,0x39,0x39,0x39};
                            System.arraycopy(tempFcs,0, tFcs,0, tempFcs.length);

                            System.arraycopy(tType,0, tBytes,0, tType.length);
                            System.arraycopy(tValue,0, tBytes, 1, tValue.length);
                            System.arraycopy(tPhoneNumByte,0,tBytes, 1+200, tPhoneNumByte.length);
                            System.arraycopy(tFcs,0, tBytes,1+200+50, tFcs.length);
                            mConnectedThread.write(tBytes);

                            //mSendBuffer.setText(byteArrayToHex(tBytes));
                            mSendBuffer.setText("Type  " + byteArrayToHex(tType) + "\n" +
                                    //"shopName  " + tempShopName + "\n" +
                                    //"shopNum  " + tempShopNum + "\n" +
                                    //"shopMessage  " + tempShopMessage + "\n" +
                                    "shopPrice  " + tempShopPrice + "\n" +
                                    "phoneNum  " + tPhoneNum + "\n" +
                                    "Fcs  " + byteArrayToHex(tempFcs) + "\n"
                            );

                            //start = true ;
                        }

                    }
                }
            });

            mInitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    start = true ;
                    mSendBuffer.setText("");
                    mReadBuffer.setText("");
                }
            });

            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(v);
                }
            });
        }
    }

    private String byteArrayToHex(byte[] buffer) {
        StringBuilder sb = new StringBuilder();
        for(int i =0 ; i < buffer.length ; i++ )
            sb.append(String.format("%02x ", buffer[i]));
        return sb.toString();
    }

    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Enabled");
            }
            else
                mBluetoothStatus.setText("Disabled");
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Bluetooth disabled");
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    private void discover(View view){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(View view){
        mBTArrayAdapter.clear();
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        public void write(byte[] bytes) {
            //byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

    }
}
