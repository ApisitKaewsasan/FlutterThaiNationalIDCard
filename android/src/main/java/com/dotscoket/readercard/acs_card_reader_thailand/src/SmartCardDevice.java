package com.dotscoket.readercard.acs_card_reader_thailand.src;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.util.Log;

import com.acs.smartcard.Reader;
import com.dotscoket.readercard.acs_card_reader_thailand.AcsCardReaderThailandPlugin;
import com.dotscoket.readercard.acs_card_reader_thailand.constant.ApduCommand;
import com.dotscoket.readercard.acs_card_reader_thailand.interfaces.SmartCardDeviceEvent;
import com.dotscoket.readercard.acs_card_reader_thailand.model.PersonalInformation;
import com.dotscoket.readercard.acs_card_reader_thailand.model.TaskEvent;
import com.dotscoket.readercard.acs_card_reader_thailand.model.TransmitParams;
import com.dotscoket.readercard.acs_card_reader_thailand.model.TransmitProgress;
import com.google.android.gms.common.util.Base64Utils;
import com.google.common.primitives.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SmartCardDevice {
    private static final String ACTION_USB_PERMISSION = "ninkoman.smartcardreader.USB_PERMISSION";
    private static final String TAG = "SmartCardDevice";
    private static final String[] powerActionStrings = {"Power Down", "Cold Reset", "Warm Reset"};


    private static final int SLOTNUM = 0;

    private UsbManager mManager;
    private Reader mReader;
    private Context context;
    private SmartCardDeviceEvent eventCallback = null;
    private PendingIntent mPermissionIntent;
    ByteArrayOutputStream photho_byte = new ByteArrayOutputStream();
    PersonalInformation personalInformation = new PersonalInformation();

    public SmartCardDevice(Context context, UsbManager manager, SmartCardDeviceEvent eventCallback) {

        this.context = context;
        this.mManager = manager;
        this.eventCallback = eventCallback;

        mReader = new Reader(mManager);

        mReader.setOnStateChangeListener(new Reader.OnStateChangeListener() {

            @Override
            public void onStateChange(int slotNum, int prevState, int currState) {

                if (prevState < Reader.CARD_UNKNOWN
                        || prevState > Reader.CARD_SPECIFIC) {
                    prevState = Reader.CARD_UNKNOWN;
                }

                if (currState < Reader.CARD_UNKNOWN
                        || currState > Reader.CARD_SPECIFIC) {
                    currState = Reader.CARD_UNKNOWN;
                }

            }
        });

        // Register receiver for USB permission
        mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(
                ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
      //  filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        context.registerReceiver(mReceiver, filter);
    }


    public void start() {
        // For each device
  personalInformation.Status = true;
        // show the list of available terminals

        if (mManager.getDeviceList().size() > 0) {
            for (UsbDevice device : mManager.getDeviceList().values()) {

                Log.d(TAG, device.getDeviceName());
                // If device name is found
                mManager.requestPermission(device, mPermissionIntent);
                break;
            }
        } else {
            personalInformation.Status = false;
            personalInformation.Message = "not found device";
            eventCallback.OnSuceess(personalInformation);
        }


    }


    public static SmartCardDevice getSmartCardDevice(Context context, SmartCardDeviceEvent eventCallback) {

        UsbManager manager;
        HashMap<String, UsbDevice> deviceList;
        UsbDevice device = null;
        SmartCardDevice cardDevice = null;


        manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (manager == null) {
            Log.w(TAG, "USB manager not found");
            return null;
        }

        cardDevice = new SmartCardDevice(context, manager, eventCallback);

        cardDevice.start();

        return cardDevice;

    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {

                synchronized (this) {

                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {

                        if (device != null) {
                            Log.d(TAG, "Opening reader:");
                           // SmartCardDevice.this.eventCallback.OnReady(SmartCardDevice.this);

                            new OpenTask().execute(device);

                            // show the list of available terminals

                        }

                    } else {

                        Log.d(TAG, "Permission denied for device");
                        eventCallback.OnErrory("Permission denied for device");

                    }
                }

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                synchronized (this) {

                    Log.d(TAG, "Closing reader...");
                   // eventCallback.OnErrory("USB device is detached");
                    new CloseTask().execute();
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.d(TAG, "ACTION_USB_DEVICE_ATTACHED");

            }
        }
    };


    private class OpenTask extends AsyncTask<UsbDevice, Void, Exception> {

        @Override
        protected Exception doInBackground(UsbDevice... params) {

            Exception result = null;

            try {

                mReader.open(params[0]);
//                EditText xx =  findViewById(R.id.main_edit_text_command);
//                xx.setText("0x00\n0xA4\n0X04\n0x00\n0x08\n0xA0\n0X00\n0x00\n0x00\n0x54\n0x48\n0x00\n0x01");
//


            } catch (Exception e) {

                result = e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Exception result) {

            if (result != null) {



                eventCallback.OnErrory(result.toString());
                  if(personalInformation.Status){
                    personalInformation.Status = false;
            personalInformation.Message = "OpenTask error Device";
            eventCallback.OnSuceess(personalInformation);
               Log.d(TAG, "OpenTask error : " + result.toString());
               }


            } else {

                Log.d(TAG, mReader.getReaderName());

                int numSlots = mReader.getNumSlots();
                Log.d(TAG, "Number of slots: " + numSlots);

                for (int i = 0; i < numSlots; i++) {
                    Log.d(TAG, "Number of slots: " + i);
                }

                PowerParams params = new PowerParams();
                params.slotNum = SLOTNUM; //0
                params.action = Reader.CARD_WARM_RESET;

                // Perform power action
                Log.d(TAG, "Slot " + numSlots + ": "
                        + powerActionStrings[params.action] + "...");
                new PowerTask().execute(params);


                // Add slot items
                //  mSlotAdapter.clear();
//                for (int i = 0; i < numSlots; i++) {
//                    mSlotAdapter.add(Integer.toString(i));
//                }

            }
        }
    }

    private class CloseTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            mReader.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


        }

    }


    private class PowerParams {

        public int slotNum;
        public int action;
    }

    private class PowerResult {

        public byte[] atr;
        public Exception e;
    }

    private class PowerTask extends AsyncTask<PowerParams, Void, PowerResult> {

        @Override
        protected PowerResult doInBackground(PowerParams... params) {

            PowerResult result = new PowerResult();

            try {

                result.atr = mReader.power(params[0].slotNum, params[0].action);

            } catch (Exception e) {

                result.e = e;

            }

            return result;
        }

        @Override
        protected void onPostExecute(PowerResult result) {

            if (result.e != null) {
               // eventCallback.OnErrory(result.e.toString());
               if(personalInformation.Status){
                    personalInformation.Status = false;
            personalInformation.Message = "The card is not firmly inserted";
            eventCallback.OnSuceess(personalInformation);
              Log.d(TAG, "PowerTask : " + result.e.toString());
               }



            } else {

                // Show ATR
                if (result.atr != null) {

                    //  atr = result.atr;
                    logBuffer("ATR:", result.atr, result.atr.length);
                    // logBuffer(result.atr, result.atr.length);


                    // Set Parameters
                    SetProtocolParams params = new SetProtocolParams();
                    params.slotNum = SLOTNUM;
                    params.preferredProtocols = Reader.PROTOCOL_T0;

                    // Set protocol
                    Log.d(TAG, "Slot " + SLOTNUM + ":  Setting protocol to T0 ");
                    new SetProtocolTask().execute(params);

                } else {

                    Log.d(TAG, "ATR: None");
                }
            }
        }
    }

    private class SetProtocolResult {

        public int activeProtocol;
        public Exception e;
    }

    private class SetProtocolParams {

        public int slotNum;
        public int preferredProtocols;
    }


    private class SetProtocolTask extends
            AsyncTask<SetProtocolParams, Void, SetProtocolResult> {

        @Override
        protected SetProtocolResult doInBackground(SetProtocolParams... params) {

            SetProtocolResult result = new SetProtocolResult();

            try {

                result.activeProtocol = mReader.setProtocol(params[0].slotNum,
                        params[0].preferredProtocols);

            } catch (Exception e) {

                result.e = e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(SetProtocolResult result) {

            if (result.e != null) {

                Log.d(TAG, result.e.toString());

            } else {

                String activeProtocolString = "Active Protocol: ";

                switch (result.activeProtocol) {

                    case Reader.PROTOCOL_T0:
                        activeProtocolString += "T=0";
                        break;

                    case Reader.PROTOCOL_T1:
                        activeProtocolString += "T=1";
                        break;

                    default:
                        activeProtocolString += "Unknown";
                        break;
                }
                // Show active protocol
                Log.d(TAG, activeProtocolString);


                //  new TransmitTask1().execute(params);

                GetSelect();
            }
        }
    }

    void GetSelect() {
        // Transmit APDU Select
        Log.d(TAG, "Slot " + SLOTNUM + ": Transmitting APDU...");

        new TransmitTask(mReader, new TaskEvent() {

            @Override
            public void onSuccess(TransmitProgress response) {
                logBuffer("Command:", response.command, response.commandLength);


                logBuffer("Applet:", response.response,response.responseLength-2);
                GetData();
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, ApduCommand.Select, null));
    }

    byte[] convertRequest(byte[] command) {
        byte[] request;
        if (mReader.getAtr(SLOTNUM)[0] == 0x3B && mReader.getAtr(SLOTNUM)[1] == 0x67) {
            request = new byte[]{0x00, (byte) 0xc0, 0x00, 0x01, command[command.length - 1]};
        } else {
            request = new byte[]{0x00, (byte) 0xc0, 0x00, 0x00, command[command.length - 1]};
        }

        return request;
    }


    void GetData() {


        GetFullName();
        GetCID();
        GetBirthDate();
        GetGender();
        GetAddress();
        GetCardIssuer();
        GetIssueDate();
        GetExpireDate();


        Photo_Part1();
        Photo_Part2();
        Photo_Part3();
        Photo_Part4();
        Photo_Part5();
        Photo_Part6();
        Photo_Part7();
        Photo_Part8();
        Photo_Part9();
        Photo_Part10();
        Photo_Part11();
        Photo_Part12();
        Photo_Part13();
        Photo_Part14();
        Photo_Part15();
        Photo_Part16();
        Photo_Part17();
        Photo_Part18();
        Photo_Part19();
        Photo_Part20();


    }

    void GetFullName() {

        // Transmit APDU FullnameTH
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {
                    // Log.d(TAG, String.valueOf(new String(Arrays.copyOfRange(response1, 0, 13), "TIS620")));
                    Log.d(TAG, String.valueOf(new String(response.response, "TIS620").substring(0, response.responseLength - 2).trim()));
                    personalInformation.NameTH = new String(response.response, "TIS620").substring(0, response.responseLength - 2).replace("#"," ").trim();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.THFullName), ApduCommand.THFullName));


        // Transmit APDU FullnameEN
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {
                    // Log.d(TAG, String.valueOf(new String(Arrays.copyOfRange(response1, 0, 13), "TIS620")));\
                    personalInformation.NameEN = new String(response.response, "TIS620").substring(0, response.responseLength - 2).replace("#"," ").trim();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.ENFullname), ApduCommand.ENFullname));

    }

    void GetCID() {
        // Transmit APDU CID
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    // Log.d(TAG, String.valueOf(new String(Arrays.copyOfRange(response1, 0, 13), "TIS620")));
                    Log.d(TAG, String.valueOf(new String(response.response, "TIS620").substring(0, response.responseLength - 2).trim()));

                    personalInformation.PersonalID = new String(response.response, "TIS620").substring(0, response.responseLength - 2).trim();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.CID), ApduCommand.CID));


    }

    void GetBirthDate() {
        // Transmit APDU Datebirth
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {
                    // Log.d(TAG, String.valueOf(new String(Arrays.copyOfRange(response1, 0, 13), "TIS620")));
                    Log.d(TAG, String.valueOf(new String(response.response, "TIS620").substring(0, response.responseLength - 2).trim()));
                    personalInformation.BirthDate = new String(response.response, "TIS620").substring(0, response.responseLength - 2).trim();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Datebirth), ApduCommand.Datebirth));


    }

    void GetGender() {
        // Transmit APDU Gender
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {
                    // Log.d(TAG, String.valueOf(new String(Arrays.copyOfRange(response1, 0, 13), "TIS620")));
                    Log.d(TAG, String.valueOf(new String(response.response, "TIS620").substring(0, response.responseLength - 2).trim()));
                    personalInformation.Gender = Integer.parseInt(new String(response.response, "TIS620").substring(0, response.responseLength - 2).trim());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Gender), ApduCommand.Gender));


    }

    void GetAddress() {
        // Transmit APDU Address
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {
                    // Log.d(TAG, String.valueOf(new String(Arrays.copyOfRange(response1, 0, 13), "TIS620")));
                    Log.d(TAG, String.valueOf(new String(response.response, "TIS620").substring(0, response.responseLength - 2).trim()));
                    personalInformation.Address = new String(response.response, "TIS620").substring(0, response.responseLength - 2).replace("####"," ").replace("#"," ").trim();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Address), ApduCommand.Address));
    }


    void GetCardIssuer() {
        // Transmit APDU Address
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {
                    // Log.d(TAG, String.valueOf(new String(Arrays.copyOfRange(response1, 0, 13), "TIS620")));
                    Log.d(TAG, String.valueOf(new String(response.response, "TIS620").substring(0, response.responseLength - 2).trim()));
                    personalInformation.CardIssuer = new String(response.response, "TIS620").substring(0, response.responseLength - 2).replace("####"," ").replace("#"," ").trim();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.CardIssuer), ApduCommand.CardIssuer));
    }


    void GetIssueDate() {
        // Transmit APDU Address
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {
                    // Log.d(TAG, String.valueOf(new String(Arrays.copyOfRange(response1, 0, 13), "TIS620")));
                    Log.d(TAG, String.valueOf(new String(response.response, "TIS620").substring(0, response.responseLength - 2).trim()));
                    personalInformation.IssueDate = new String(response.response, "TIS620").substring(0, response.responseLength - 2).replace("####"," ").replace("#"," ").trim();

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.IssueDate), ApduCommand.IssueDate));
    }

    void GetExpireDate() {
        // Transmit APDU Address
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {
                    // Log.d(TAG, String.valueOf(new String(Arrays.copyOfRange(response1, 0, 13), "TIS620")));
                    Log.d(TAG, String.valueOf(new String(response.response, "TIS620").substring(0, response.responseLength - 2).trim()));
                    personalInformation.ExpireDate = new String(response.response, "TIS620").substring(0, response.responseLength - 2).replace("####"," ").replace("#"," ").trim();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.ExpireDate), ApduCommand.ExpireDate));
    }


    // Transmit APDU Photo_Part1

    void Photo_Part1() {

        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {
                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part1), ApduCommand.Photo_Part1));
    }

    // Transmit APDU Photo_Part2
    void Photo_Part2() {

        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part2), ApduCommand.Photo_Part2));
    }

    // Transmit APDU Photo_Part3
    void Photo_Part3() {

        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part3), ApduCommand.Photo_Part3));
    }

    // Transmit APDU Photo_Part4
    void Photo_Part4() {

        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part4), ApduCommand.Photo_Part4));
    }

    // Transmit APDU Photo_Part5
    void Photo_Part5() {

        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part5), ApduCommand.Photo_Part5));
    }

    // Transmit APDU Photo_Part6
    void Photo_Part6() {

        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part6), ApduCommand.Photo_Part6));
    }

    // Transmit APDU Photo_Part7
    void Photo_Part7() {

        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part7), ApduCommand.Photo_Part7));
    }

    // Transmit APDU Photo_Part8
    void Photo_Part8() {

        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part8), ApduCommand.Photo_Part8));
    }

    // Transmit APDU Photo_Part9
    void Photo_Part9() {

        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part9), ApduCommand.Photo_Part9));
    }

    // Transmit APDU Photo_Part10
    void Photo_Part10() {
        new

                TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part10), ApduCommand.Photo_Part10));
    }

    // Transmit APDU Photo_Part11
    void Photo_Part11() {
        new

                TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part11), ApduCommand.Photo_Part11));
    }

    // Transmit APDU Photo_Part12
    void Photo_Part12() {
        new

                TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part12), ApduCommand.Photo_Part12));
    }

    // Transmit APDU Photo_Part13
    void Photo_Part13() {
        new

                TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part13), ApduCommand.Photo_Part13));
    }

    // Transmit APDU Photo_Part14
    void Photo_Part14() {
        new

                TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part14), ApduCommand.Photo_Part14));
    }

    // Transmit APDU Photo_Part15
    void Photo_Part15() {
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part15), ApduCommand.Photo_Part15));
    }

    // Transmit APDU Photo_Part16
    void Photo_Part16() {
        new

                TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part16), ApduCommand.Photo_Part16));
    }

    // Transmit APDU Photo_Part17
    void Photo_Part17() {
        new

                TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part17), ApduCommand.Photo_Part17));
    }

    // Transmit APDU Photo_Part18
    void Photo_Part18() {
        new

                TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).

                execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part18), ApduCommand.Photo_Part18));
    }

    // Transmit APDU Photo_Part19
    void Photo_Part19() {
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {

                    
                    photho_byte.write(response.response, 0,response.responseLength-2);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part19), ApduCommand.Photo_Part19));
    }

    // Transmit APDU Photo_Part20
    void Photo_Part20() {
        new TransmitTask(mReader, new TaskEvent() {
            @Override
            public void onSuccess(TransmitProgress response) {
                try {
                    photho_byte.write(response.response, 0,response.responseLength-2);

                    String encodedText = Base64Utils.encode(photho_byte.toByteArray());
                    personalInformation.PictureSubFix = encodedText.replaceAll("\\s+","");
                    Log.d(TAG, encodedText+" "+encodedText.length());
                    eventCallback.OnSuceess(personalInformation);
                    mReader.close();
                    // Photo_Part21();;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {
                eventCallback.OnErrory(e.getMessage());
            }
        }).execute(new TransmitParams(SLOTNUM, -1, convertRequest(ApduCommand.Photo_Part20), ApduCommand.Photo_Part20));
    }


    private void logBuffer(String tag, byte[] buffer, int bufferLength) {

        String bufferString = "";

        for (int i = 0; i < bufferLength; i++) {

            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }

            if (i % 16 == 0) {

                if (bufferString != "") {

                    Log.d(TAG, bufferString);
                    bufferString = "";
                }
            }

            bufferString += hexChar.toUpperCase() + " ";
        }

        if (bufferString != "") {
            Log.d(TAG, tag + " " + bufferString);
        }
    }


    private String toHexString(int i) {

        String hexString = Integer.toHexString(i);
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }

        return hexString.toUpperCase();
    }

    private String toHexString(byte[] buffer) {

        String bufferString = "";

        for (int i = 0; i < buffer.length; i++) {

            String hexChar = Integer.toHexString(buffer[i] & 0xFF);
            if (hexChar.length() == 1) {
                hexChar = "0" + hexChar;
            }

            bufferString += hexChar.toUpperCase() + " ";
        }

        return bufferString;
    }


    public void stop() {
        mReader.close();
    }

}


