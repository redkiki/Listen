package com.example.listen;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class play extends AppCompatActivity
{
    /**************************翻译API********************************/
    String TAG = "play";
    Button button;//以前用于翻译的按钮  现在撤了
    static String to;//目标译文 可变 zh中文 en英文
    private Thread newThread;
    /**************************翻译API********************************/

    //处理英文
    private String line;
    private String result;

    //添加听不懂布局适配器
    FruitAdapter adapter;

    //听不懂布局
    private RecyclerView recyclerView;

    //当前播放毫秒
    private int curretnPosition;

    //时间数组
    private List<String> mtimeList = new ArrayList<>();

    //英文数组
    private List<String> mwordsList = new ArrayList<>();

    //播放按钮
    private ImageButton play_button;

    //媒体对象
    private MediaPlayer mp;

    //进度条对象
    private SeekBar positionbar;

    //显示总时间
    private TextView overtime;

    //显示当前播放时间
    private TextView nowtime;

    //中文字幕
    private TextView translate_view;

    //字母开关
    private Button zimu;

    //上一句
    private Button last_button;

    //下一句
    private Button next_button;

    //重复此句
    private Button repeat_button;

    //添加听不懂
    private Button cant_button;

    //总时间
    private int totaltime;

    //广播数据
    private String broadcast_data;

    //英文原文
    private TextView yuanwen;


    /********************接收网络状态的广播*****************************/
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private List<cailiao> fruitList = new ArrayList<cailiao>();
    //网络变化接收者
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            //判断是否是联网状态
            if (networkInfo != null && networkInfo.isAvailable())
            {}
            else {
                //没有联网，将提醒用户联网。并退出程序
                new AlertDialog.Builder(play.this).setTitle("信息提示")
                        .setMessage("您的网络未连接，请连接网络使用本程序！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //退出程序
                                finish();
                            }
                        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //退出程序
                        finish();
                    }
                }).show();
            }
        }
    }
    /********************接收网络状态的广播*****************************/


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_layout);

        /**********************注册接收网络状态变化的广播**************************/
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
        /**********************注册接收网络状态变化的广播**************************/

        overtime = (TextView) findViewById(R.id.overtime_text);
        yuanwen = (TextView) findViewById(R.id.yuanwen);
        nowtime = (TextView) findViewById(R.id.nowtime_text);
        positionbar = (SeekBar) findViewById(R.id.positionBar);
        play_button = (ImageButton) findViewById(R.id.play_button);
        zimu = (Button) findViewById(R.id.zimu);
        last_button = (Button) findViewById(R.id.last);
        next_button = (Button) findViewById(R.id.next);
        repeat_button = (Button) findViewById(R.id.repeat);
        cant_button = (Button) findViewById(R.id.cant);
        translate_view = (TextView) findViewById(R.id.translate_textview);
        recyclerView=(RecyclerView) findViewById(R.id.recyclerView);
        yuanwen.addTextChangedListener(new MyTextWatcher());

        //设置布局管理器，一行一个item
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new FruitAdapter(fruitList);
        recyclerView.setAdapter(adapter);

        //再次进行用户的网络状态判断，联网才执行，否则提示用户联网并退出
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable())
        {
            Mythread mythread = new Mythread();
            //获取听力
            new Thread(mythread).start();
            Intent intent = getIntent();
            //获取播放地址
            broadcast_data = intent.getStringExtra("dizhi");
            //音频播放，Uri解析的是音频存放的地址，该音频存放在我们的服务器里
            mp = MediaPlayer.create(this, Uri.parse("http://存放音频的服务器ip或域名/CET/" + broadcast_data + ".mp3\n"));
            //循环播放
            mp.setLooping(true);
            //从头开始播放
            mp.seekTo(0);
            //总时间
            totaltime = mp.getDuration();
            //将毫秒化为分钟与秒
            String S_overtime = createTimeLabel(totaltime);
            //总时间
            overtime.setText(S_overtime);
            //进度条
            positionbar = (SeekBar) findViewById(R.id.positionBar);
            //进度条最大值为音频总时间
            positionbar.setMax(totaltime);

            //进度条监听
            positionbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        //监听器，当进度条拖动，音频播放位置改变
                        mp.seekTo(progress);
                        positionbar.setProgress(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            //Thread(更新 进度条和时间）
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //只要音频不为空永远获取音频当前播放时间
                    while (mp != null) {
                        try {
                            Message msg = new Message();
                            msg.what = mp.getCurrentPosition();
                            handler.sendMessage(msg);
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } else {
            new AlertDialog.Builder(play.this).setTitle("信息提示")
                    .setMessage("您的网络未连接，请连接网络，该程序只支持在线使用！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //退出程序
                            finish();
                        }
                    }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //退出程序
                    finish();
                }
            }).show();
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //从线程获取毫秒
            curretnPosition = msg.what;
            //进度条更新
            positionbar.setProgress(curretnPosition);
            //更新时间，将毫秒化为分与秒
            String S_nowtime = createTimeLabel(curretnPosition);
            nowtime.setText(S_nowtime);
            if (mwordsList!=null&&mwordsList.size()>0) {
                //英文根据毫秒取
                yuanwen.setText(mwordsList.get(findShowLine(curretnPosition)));
            }
        }
    };


    //二分法查找当前时间应该显示的行数（最后一个 <= time 的行数）
    private int findShowLine(long time) {
        int left = 0;
        int right = mtimeList.size();
        long middleTime=0;
        while (left <= right)
        {
            int middle = (left + right) / 2;
            //防止为空
            if (mtimeList!=null&&mtimeList.size()>0)
            {
                middleTime = timeHandler(mtimeList.get(middle));}
            if(true) {
                if (time < middleTime) {
                    right = middle - 1;
                } else {
                    if (middle + 1 >= mtimeList.size() || time < timeHandler(mtimeList.get(middle + 1))) {
                        return middle;
                    }
                    left = middle + 1;
                }
            }
        }
        return 0;
    }

    //获取听力
    class Mythread implements Runnable
    {
        @Override
        public void run() {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                String lrcUrl = "http://存放音频的服务器ip或域名/CET/" + broadcast_data + ".txt\n";
                URL url = new URL(lrcUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                //对输入流进行读取
                InputStream input = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input));
                while ((line = reader.readLine()) != null) {
                    //只要有"["就读
                    if (line.indexOf("[") >= 0)
                    {
                        //取时间
                        result = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
                        //取听力材料
                        line = line.substring(line.indexOf("]") + 1);
                        //时间集合
                        mtimeList.add(result);
                        //英文材料集合
                        mwordsList.add(line);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //保证关闭
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }
    }

    //时间标签化毫秒
    private int timeHandler(String string) {
        string = string.replace(".", ":");
        String timeData[] = string.split(":");
        // 分离出分、秒并转换为整型
        int minute = Integer.parseInt(timeData[0]);
        int second = Integer.parseInt(timeData[1]);
        int millisecond = Integer.parseInt(timeData[2]);
        int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
        return currentTime;
    }

    //毫秒化时间标签
    public String createTimeLabel(int time) {
        String timelabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;
        timelabel = min + ":";
        if (sec < 10) timelabel += "0";
        timelabel += sec;
        return timelabel;
    }

    //用于播放的方法
    public void play_button(View view) {
        if (!mp.isPlaying()) {
            //stopping
            mp.start();
            play_button.setBackgroundResource(R.drawable.stop2);
        } else {
            //playing
            mp.pause();
            play_button.setBackgroundResource(R.drawable.play2);
        }
    }

    //将隐藏的textview显示出来的方法
    public void zimu_button(View view) {
        if (yuanwen.isShown()) {
            yuanwen.setVisibility(View.GONE);
        } else {
            yuanwen.setVisibility(View.VISIBLE);
        }
    }

    public void fanyi_button(View view) {
        if (translate_view.isShown()) {
            translate_view.setVisibility(View.GONE);
        } else {
            translate_view.setVisibility(View.VISIBLE);
        }
    }

    //上一句
    public void last_button(View view) {

        /***********************************************
         * 媒体对象与进度条都是用毫秒
         * 根据当前毫秒与二分法——>找到当前播放的行数，当前行数对应英文材料数组游标，而英文材料与时间数组游标在下载时又一一对应——>游标减一则为上一个时间元素——>转化为我们要的毫秒
         ***********************************************/
        try {
            mp.seekTo(timeHandler(mtimeList.get(findShowLine(curretnPosition) - 1)));
            positionbar.setProgress(timeHandler(mtimeList.get(findShowLine(curretnPosition) - 1)));
        } catch (Exception e) {
            Toast.makeText(play.this, "没有上一句了", Toast.LENGTH_SHORT).show();
            mp.start();
        }
    }

    //下一句
    public void next_button(View view) {
        try {
            mp.seekTo(timeHandler(mtimeList.get(findShowLine(curretnPosition) + 1)));
            positionbar.setProgress(timeHandler(mtimeList.get(findShowLine(curretnPosition) + 1)));
        } catch (Exception e) {
            Toast.makeText(play.this, "最后一句了", Toast.LENGTH_SHORT).show();
            mp.start();
        }
    }

    //重播此句
    public void repeat_button(View view) {

        mp.seekTo(timeHandler(mtimeList.get(findShowLine(curretnPosition))));
        positionbar.setProgress(timeHandler(mtimeList.get(findShowLine(curretnPosition))));
    }

    //翻译好的中文显示，更新操作在主线程完成
    private void showon_ui(final String translate_txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //在这里进行UI操作，将结果显示到界面上
                translate_view.setText(translate_txt);
            }

        });
    }
    //recyclerView窗口更新
    private void shuaxin()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                //主线程刷新recycleview
                recyclerView.setAdapter(adapter);
            }
        });
    }

    /**********************************************************************
     * 听力原文监听类
     *其中多处涉及相关参数  详情进官网查询https://api.fanyi.baidu.com/doc/21
     **********************************************************************/
    class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String word = yuanwen.getText().toString();
                    //源语种 en 英语 zh 中文
                    String from = "auto";
                    //成立则说明没有汉字，否则含有汉字。
                    if (word.length() == word.getBytes().length) {
                        //没有汉字 英译中
                        to = "zh";
                    } else {
                        //含有汉字 中译英
                        to = "en";
                    }
                    //appid  申请API的id
                    String appid = "*******";
                    //随机数 这里范围是[0,100]整数 无强制要求
                    String salt = (int) (Math.random() * 100 + 1) + "";
                    //密钥
                    String key = "********";
                    // string1 = appid+q+salt+密钥
                    String string1 = appid + word + salt + key;
                    // 签名 = string1的MD5加密 32位字母小写
                    String sign = MD5Utils.getMD5Code(string1);
                    Retrofit retrofitBaidu = new Retrofit.Builder()
                            .baseUrl("https://fanyi-api.baidu.com/api/trans/vip/")
                            // 设置数据解析器
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    BaiduTranslateService baiduTranslateService = retrofitBaidu.create(BaiduTranslateService.class);
                    Call<RespondBean> call = baiduTranslateService.translate(word, from, to, appid, salt, sign);
                    call.enqueue(new Callback<RespondBean>() {
                        @Override
                        public void onResponse(Call<RespondBean> call, Response<RespondBean> response) {
                            RespondBean respondBean = new RespondBean();
                            //返回的JSON字符串对应的对象
                            respondBean = response.body();
                            //防止空指针
                            List<RespondBean.TransResultBean> list = new ArrayList<>();
                            list = respondBean.getTrans_result();
                            //防止取空
                            if (null != list && list.size() != 0) {
                                //获取翻译的字符串String
                                String result = list.get(0).getDst();
                                showon_ui(result);
                            }
                        }

                        @Override
                        public void onFailure(Call<RespondBean> call, Throwable t) {

                        }
                    });
                }
            }).start();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
    //添加文本到recycleview
    public void cant_button(View view)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //将当前对话添加到recycleview,并将其时间标签也添加，用来跳转播放
                cailiao cl=new cailiao(R.id.cant_cailiao,mwordsList.get(findShowLine(curretnPosition)) + "[" + mtimeList.get(findShowLine(curretnPosition)) + "]");
                fruitList.add(cl);
                shuaxin();
            }
        }).start();
    }


    /*********************************************************************************************
     *为RecyclerView准备一个适配器，新建 FruitAdapter类，让这个适配器继承自RecyclerView.Adapter，
     * 并将泛型指定为FruitAdapter.ViewHolder。其中，ViewHolder是我们在FruitAdapter中定义的一个内部类
     *********************************************************************************************/
    class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder>
    {

        private List<cailiao> mFruitList;

        class ViewHolder extends RecyclerView.ViewHolder
        {
            View fruitView;
            TextView fruitImage;
            TextView fruitName;

            public ViewHolder(View view)
            {
                super(view);
                fruitView = view;
                fruitImage = (TextView) view.findViewById(R.id.cant_Image);
                fruitName = (TextView) view.findViewById(R.id.cant_cailiao);
            }
        }

        public FruitAdapter(List<cailiao> fruitList)
        {
            mFruitList = fruitList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.translate_layout, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            holder.fruitImage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = holder.getAdapterPosition();
                    mFruitList.remove(position);
                    shuaxin();
                }
            });

            holder.fruitName.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int position = holder.getAdapterPosition();
                    cailiao fruit = mFruitList.get(position);
                    String choose = fruit.getCant_cailiao();
                    //分离时间标签，转换毫秒，跳转
                    choose = choose.substring(choose.indexOf("[") + 1, choose.indexOf("]"));
                    int cant_currrenposition = timeHandler(choose);
                    mp.seekTo(cant_currrenposition);
                    positionbar.setProgress(cant_currrenposition);
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(FruitAdapter.ViewHolder holder, int position)
        {
            cailiao fruit = mFruitList.get(position);
            holder.fruitImage=(TextView)findViewById(fruit.getCant_id());
            holder.fruitName.setText(fruit.getCant_cailiao());
        }

        @Override
        public int getItemCount()
        {
            return mFruitList.size();
        }
    }

    //删除RecyclerView中自己已经明白的句子
    public void delete(View view)
    {
        fruitList.clear();
        shuaxin();
    }





    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mp.stop();
        unregisterReceiver(networkChangeReceiver);
        finish();
    }

}


































