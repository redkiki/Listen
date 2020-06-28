package com.example.listen;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CET6 extends AppCompatActivity
{
    private List<String> data=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_layout);
        initlisten();
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(CET6.this, android.R.layout.simple_list_item_1, data);
        final ListView first_listview=(ListView) findViewById(R.id.first_listview);
        first_listview.setAdapter(adapter);
        first_listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String choose=data.get(position);

                    Intent intent=new Intent(CET6.this,play.class);
                    intent.putExtra("dizhi",choose);
                    startActivity(intent);

            }
        });
    }

    private void initlisten()
    {
        data.add("CET6.2017.06.1");
        data.add("CET6.2017.06.2");
        data.add("CET6.2017.06.3");
        data.add("CET6.2017.12.1");
        data.add("CET6.2017.12.2");
        data.add("CET6.2017.12.3");
        data.add("CET6.2018.06.1");
        data.add("CET6.2018.06.2");
        data.add("CET6.2018.06.3");
        data.add("CET6.2018.12.1");
        data.add("CET6.2018.12.2");
        data.add("CET6.2018.12.3");
        data.add("CET6.2019.06.1");
        data.add("CET6.2019.06.2");
        data.add("CET6.2019.06.3");
    }
}
