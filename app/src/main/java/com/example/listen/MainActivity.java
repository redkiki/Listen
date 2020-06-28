package com.example.listen;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
{


    private List<Book> bookList=new ArrayList<>();
    private BookAdapter adapter;
    private RecyclerView recyclerView;
    private CardView cardView;
    /********************网络状态广播的定义*****************************/
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    class  NetworkChangeReceiver extends BroadcastReceiver {//网络变化接收者
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(MainActivity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null && networkInfo.isAvailable())//联网了
                {}
            else {
                //没有联网，将提醒用户联网。并退出程序
                new AlertDialog.Builder(MainActivity.this).setTitle("信息提示")
                        .setMessage("您的网络未连接，请连接网络，该程序只支持在线使用！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();//退出程序
                            }
                        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();//退出程序
                    }
                }).show();
            }
        }
    }
    /**********************************************************/



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /***********负责网络网络状态广播接收者的注册*****************/
        //注册广播接收者，监测网络变化
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver , intentFilter);

        /**********************************************************/
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        initlisten();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);//三列书
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BookAdapter(bookList);
        recyclerView.setAdapter(adapter);

    }


    private void initlisten()
    {
        Book cet4=new Book(R.drawable.cet4,"CET4");
        Book cet6=new Book(R.drawable.cet6,"CET6");
        Book add=new Book(R.drawable.add,"添加听力");
        bookList.add(cet4);
        bookList.add(cet6);
        bookList.add(add);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

}
