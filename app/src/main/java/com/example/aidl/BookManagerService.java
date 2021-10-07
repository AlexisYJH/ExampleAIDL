package com.example.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookManagerService extends Service {
    private static final String TAG = "BMS";
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListeners = new RemoteCallbackList<IOnNewBookArrivedListener>();

    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            onNewBookArrived(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.register(listener);
            Log.d(TAG, "registerListener: " + listener);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.unregister(listener);
            Log.d(TAG, "unregisterListener: " + listener);
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Log.d(TAG, "onTransact");
            //check permission 2
            /*if (checkPermissionDenied()) {
                return false;
            }*/
            return super.onTransact(code, data, reply, flags);
        }
    };

    private void onNewBookArrived(Book book) {
        Log.d(TAG, Thread.currentThread().getName() + " onNewBookArrived: " + book);
        mBookList.add(book);
        final int count = mListeners.beginBroadcast();
        for (int i = 0; i < count; i++) {
            IOnNewBookArrivedListener listener = mListeners.getBroadcastItem(i);
            if (listener != null) {
                try {
                    listener.onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mListeners.finishBroadcast();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "IOS"));
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //check permission 1(Best)
        Log.d(TAG, "onBind");
        return checkPermissionDenied() ? null : mBinder;
    }

    private boolean checkPermissionDenied() {
        int check = checkCallingOrSelfPermission("com.example.aidl.permission.ACCESS_BOOK_SERVICE");
        Log.d(TAG, "checkPermission: " + check);
        return check == PackageManager.PERMISSION_DENIED;
    }
}
