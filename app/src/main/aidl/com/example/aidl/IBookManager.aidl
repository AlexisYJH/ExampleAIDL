// IBookManager.aidl
//�ڶ���AIDL�ļ�������
package com.example.aidl;

//��������Ҫʹ�õķ�Ĭ��֧���������͵İ�
import com.example.aidl.Book;
interface IBookManager {
    //���еķ���ֵǰ������Ҫ���κζ�����������ʲô��������
    List<Book> getBookList();
    //����ʱ����Java���������Լ�String��CharSequence֮������Ͷ���Ҫ��ǰ����϶���tag�������ʲô�������
    void addBook(in Book book);
}