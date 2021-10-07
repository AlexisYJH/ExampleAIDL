// IOnNewBookArrivedListener.aidl
package com.example.aidl;

import com.example.aidl.Book;
interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book book);
}