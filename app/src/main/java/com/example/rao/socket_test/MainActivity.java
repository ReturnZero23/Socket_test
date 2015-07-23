package com.example.rao.socket_test;

import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
//import java.util.logging.Handler;
import android.os.Handler;


public class MainActivity extends ActionBarActivity {
    Socket socket =null;
    String buffer = "";
    TextView txt1;
    Button send;
    EditText ed1;
    String geted1;
    public Handler myHandler = new Handler() {
        public void handleMessage(Message msg){
            if(msg.what == 0x11){
                Bundle bundle = msg.getData();
                txt1.append("server:"+bundle.getString("msg")+"\n");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt1 = (TextView) findViewById(R.id.textview);
        send = (Button)findViewById(R.id.btn_send);
        ed1 = (EditText)findViewById(R.id.send_str);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geted1 = ed1.getText().toString();
                txt1.append("client:"+geted1+"\n");
                new MyThread(geted1).start();
            }
        });
    }
    class MyThread extends Thread {

        public String txt1;

        public MyThread(String str) {
            txt1 = str;
        }

        @Override
        public void run() {
            //������Ϣ
            Message msg = new Message();
            msg.what = 0x11;
            Bundle bundle = new Bundle();
            bundle.clear();
            try {
                //���ӷ����� ���������ӳ�ʱΪ5��
                socket = new Socket();
                try {
                    socket.connect(new InetSocketAddress("192.168.0.10",9000));
                    //socket.connect(new InetSocketAddress("192.168.0.10",9000), 5000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //��ȡ���������
                OutputStream ou = socket.getOutputStream();
                BufferedReader bff = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                //��ȡ������������Ϣ
                String line = null;
                buffer="";
                while ((line = bff.readLine()) != null) {
                    buffer = line + buffer;
                }

                //�������������Ϣ
                ou.write("android �ͻ���".getBytes("gbk"));
                ou.flush();
                bundle.putString("msg", buffer.toString());
                msg.setData(bundle);
                //������Ϣ �޸�UI�߳��е����
                myHandler.sendMessage(msg);
                //�رո������������
                bff.close();
                ou.close();
                socket.close();
            } catch (SocketTimeoutException aa) {
                //bundle 进行设置
                bundle.putString("msg", "���������ʧ�ܣ����������Ƿ��");
                msg.setData(bundle);
                //������Ϣ �޸�UI�߳��е����
                myHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
