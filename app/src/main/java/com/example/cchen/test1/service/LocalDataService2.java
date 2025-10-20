package com.example.cchen.test1.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.example.cchen.test1.inter.DataUi;
import com.example.cchen.test1.util.BitUtil;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import android_serialport_api.SerialPort;

/**
 * Created by cchen on 2016/6/18.
 */
public class LocalDataService2 extends Service {
    private static final String TAG = LocalDataService2.class.getSimpleName() + " cchen";
    public static boolean continueRead = false;
    private static final int GOOD_SIZE = 26;
    private static final int HEAD_0 = 0xAA;
    private static final int HEAD_1 = 0x55;
    private static final int HEAD_2 = 0xFB;
    ArrayList<DataUi> uis = new ArrayList<DataUi>();

    public void addUi(DataUi ui) {
        uis.add(ui);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind ...");
        initLocalPort();
        return new MsgBinder();
    }

    private void initLocalPort() {
        try {
            //SerialPort serialPort = new SerialPort(new File("/dev/ttyAMA2/"), 9600, 0);
            SerialPort serialPort = new SerialPort(new File("/dev/ttyS3/"), 9600, 0);
            new DataThread(serialPort).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         *
         * @return
         */
        public LocalDataService2 getService() {
            return LocalDataService2.this;
        }

    }

    private class DataThread extends Thread {
        private final SerialPort mPort;

        public DataThread(SerialPort serialPort) {
            mPort = serialPort;
            continueRead = true;
        }

        @Override
        public void run() {
            super.run();

            // 定义一个包的最大长度  
            int maxLength = 2048;
            byte[] buffer = new byte[maxLength];
            // 每次收到实际长度  
            int available = 0;
            // 当前已经收到包的总长度  
            int currentLength = 0;
            // 协议头长度4个字节（开始符1，开始符2，长度，标志字FB）  
            int headerLength = 4;

            if (mPort == null) return;
            InputStream mInputStream = mPort.getInputStream();
            while (!isInterrupted()) {
                try {
                    available = mInputStream.available();
                    if (available > 0) {
                        // 防止超出数组最大长度导致溢出  
                        if (available > maxLength - currentLength) {
                            available = maxLength - currentLength;
                        }
                        mInputStream.read(buffer, currentLength, available);
                        currentLength += available;

                        byte[] raw = new byte[currentLength];
                        System.arraycopy(buffer, 0, raw, 0, currentLength);
                        if (uis != null) {
                            for (DataUi ui : uis) {
                                if (ui != null)
                                    ui.onNewData(raw);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                int cursor = 0;

                // 如果当前收到包大于头的长度，则解析当前包  
                while (currentLength >= headerLength) {

                    // 证明当前的cursor并没有指向头
                    if (buffer[cursor] != -86) {
                        currentLength = currentLength - 1;
                        ++cursor;
                        continue;
                    } else if (buffer[cursor + 1] != 85) {
                        currentLength = currentLength - 1;
                        ++cursor;
                        continue;
                    } else if (buffer[cursor + 3] != -5) {
                        currentLength = currentLength - 1;
                        ++cursor;
                        continue;

                    }

                    //此处cursor已经指向了头，然后看看内容长度是多少
                    int contentLength = buffer[cursor + 2];

                    // 如果内容包的长度大于最大内容长度或者小于等于0，则说明这个包有问题，丢弃  
                    if (contentLength <= 0 || contentLength > maxLength - 7) {
                        currentLength = 0;
                        break;
                    }


                    // 如果当前获取到长度小于整个包的长度，则跳出循环等待继续接收数据  
                    int factPackLen = contentLength + 7;
                    if (currentLength < factPackLen) {
                        break;
                    }

                    // 一个完整数据帧即产生，然后解析它  
                    onDataReceived(buffer, cursor, factPackLen);

                    //将当前的cursor向后移动一个完整数据帧长度，同时将收到的数据包长度减少完整数据帧长度
                    currentLength -= factPackLen;
                    cursor += factPackLen;
                }

                // 残留字节移到缓冲区首  
                if (currentLength > 0 && cursor > 0) {
                    System.arraycopy(buffer, cursor, buffer, 0, currentLength);
                }
            }
        }
    }

    protected void onDataReceived(final byte[] buffer, final int index, final int packlen) {
        System.out.println("收到信息");
        byte[] pakage = new byte[packlen];
        System.arraycopy(buffer, index, pakage, 0, packlen);
        //此时pakage就是一帧完整的数据包，该包以0xAA 0x55 0x 0xFB开头，最终以0xAA 0x55结束
        //在此处编写解析与刷新界面函数吧
        ArrayList<Byte> objects = new ArrayList<>();
        for (byte tempB : pakage) {
            objects.add(tempB);
        }
        Log.d(TAG, "onDataReceived good  " + Arrays.toString(objects.toArray()));
        if (uis != null) {
            for (DataUi ui : uis) {
                if (ui != null)
                    ui.onFrameGet(objects);
            }
        }
    }


    private byte[] deleteExtraZero(byte[] bytes) {
        int length = bytes.length;
        int last55Index = 0;
        int lastAAIndex = 0;
        for (int k = length - 1; k > 0; k--) {
            byte aByte = bytes[k];
            int intFromByte = BitUtil.getIntFromByte(aByte);

            if (intFromByte == HEAD_1) last55Index = k;
            else if (intFromByte == HEAD_0) {
                lastAAIndex = k;
                break;
            }
        }
        Log.d(TAG, lastAAIndex + " deleteExtraZero " + last55Index);
        if (last55Index != 0 && lastAAIndex != 0 && lastAAIndex == (last55Index - 1)) {
            byte[] newData = new byte[last55Index + 1];
            System.arraycopy(bytes, 0, newData, 0, last55Index + 1);
//            for (int i = 0; i < last55Index; i++) {
//                newData[i] = bytes[i];
//            }
            return newData;
        }
        return bytes;
    }


    ArrayList<Byte> dataList = new ArrayList<Byte>();


    private void newJudge(byte b) {
        dataList.add(b);
        int size = dataList.size();

        if (size < GOOD_SIZE) return;

        int firstAaIndex = -1;
        int Len = -1;
        for (int k = 0; k < size - 3; k++) {
            Byte tempByte1 = dataList.get(k);
            Byte tempByte2 = dataList.get(k + 1);
            Byte tempByte3 = dataList.get(k + 2);
            Byte tempByte4 = dataList.get(k + 3);
            if ((BitUtil.getIntFromByte(tempByte1) == HEAD_0) &&
                    (BitUtil.getIntFromByte(tempByte2) == HEAD_1) &&
                    (BitUtil.getIntFromByte(tempByte4) == HEAD_2)) {
                //
                firstAaIndex = k;
                Len = tempByte3;
                break;
            }
        }

        if (firstAaIndex != -1 && Len != -1) {
            for (int k = 0; k < firstAaIndex; k++) {
                dataList.remove(k);
            }

            ArrayList<Byte> newList = new ArrayList<>();
            for (int k = 0; k < Len + 8; k++) {
                newList.add(dataList.get(k));
            }
            if (uis != null) {
                for (DataUi ui : uis) {
                    if (ui != null)
                        ui.onFrameGet(newList);
                }
            }
            for (int k = 0; k < Len + 8; k++) {
                dataList.remove(k);
            }
        }


    }

    private void judge(byte byteI) {
        dataList.add(byteI);
        int size = dataList.size();
        if (size < GOOD_SIZE) {
//            Log.d(TAG, size + " less: " + Arrays.toString(dataList.toArray()));
            return;
        }
        Log.d(TAG, size + " " + byteI + " !!!!!!!!!!!!!!!!!!!!: " + Arrays.toString(dataList.toArray()));
        Byte b0 = dataList.get(0);
        Byte b1 = dataList.get(1);
        Byte b2 = dataList.get(2);
        Byte b3 = dataList.get(3);
        int i0 = BitUtil.getIntFromByte(b0);
        int i1 = BitUtil.getIntFromByte(b1);
        int i3 = BitUtil.getIntFromByte(b3);
        String b0s = Integer.toHexString(b0);
        String b1s = Integer.toHexString(b1);
        String b3s = Integer.toHexString(b3);
        Log.d(TAG, b0s + " " + b1s + " " + b3s);
        Log.d(TAG, i0 + " " + i1 + " " + i3);
        Log.d(TAG, HEAD_0 + " " + HEAD_1 + " " + HEAD_2);
        if (i0 == HEAD_0 && i1 == HEAD_1) {
            if (i3 == HEAD_2) {
                ArrayList<Byte> goodFrame = new ArrayList<>();
                //Todo head
                for (int z = 0; z < GOOD_SIZE; z++) {
                    Log.d(TAG, z + " good:  " + dataList.get(z) + "  " + BitUtil.getIntFromByte(dataList.get(z)));
                    goodFrame.add(dataList.get(z));
                    dataList.remove(z);
                }

                if (uis != null) {
                    for (DataUi ui : uis) {
                        if (ui != null)
                            ui.onFrameGet(goodFrame);
                    }
                }
            } else {
                //Todo tail
                dataList.remove(0);
                dataList.remove(1);
            }
        } else {
            int firstAaIndex = -1;
            for (int k = 2; k < size; k++) {
                Byte aByte = dataList.get(k);
                if (BitUtil.getIntFromByte(aByte) == HEAD_0) {
                    firstAaIndex = k;
                    break;
                }
            }
            if (firstAaIndex == -1) {
                dataList.clear();
                return;
            }

            ArrayList<Byte> newList = new ArrayList<>();
            for (int j = firstAaIndex; j < size; j++) {
                newList.add(dataList.get(j));
            }
            dataList = newList;
            Log.d(TAG, firstAaIndex + "newList " + Arrays.toString(dataList.toArray()));
        }
        Log.d(TAG, size + " judged dataList: " + Arrays.toString(dataList.toArray()));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        continueRead = false;
        uis.clear();
    }
}
