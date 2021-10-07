package com.example.aidl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "BMSActivity";
    private static final int MSG_BOOK_ARRIVED = 1;
    private IBookManager mBookManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG_BOOK_ARRIVED:
                    Log.d(TAG, Thread.currentThread().getName() + " handleMessage receive new book: " + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) {
            Log.d(TAG, Thread.currentThread().getName() + " onNewBookArrived: " + book);
            mHandler.obtainMessage(MSG_BOOK_ARRIVED, book).sendToTarget();
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: " + service);
            mBookManager = IBookManager.Stub.asInterface(service);
            try {
                mBookManager.registerListener(mOnNewBookArrivedListener);
                Log.d(TAG, "registerListener: " + mOnNewBookArrivedListener);
                List<Book> list = mBookManager.getBookList();
                Log.d(TAG, "getBookList: " + list.toString());
                mBookManager.addBook(new Book(3, "Python"));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: " + name);
            mBookManager = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (mBookManager != null && mBookManager.asBinder().isBinderAlive()) {
            try {
                mBookManager.unregisterListener(mOnNewBookArrivedListener);
                Log.d(TAG, "unregisterListener: " + mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}