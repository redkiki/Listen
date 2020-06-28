package com.example.listen;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder>
{
    private Context mContext;

    private List<Book> mBookList;

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        ImageView bookImage;
        TextView bookName;

        public ViewHolder(View view)
        {
            super(view);
            cardView = (CardView) view;
            bookImage = (ImageView) view.findViewById(R.id.book_image);
            bookName = (TextView) view.findViewById(R.id.book_name);
        }
    }
    public BookAdapter(List<Book> bookList)
    {
        mBookList = bookList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (mContext == null)
        {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.book_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                Book book = mBookList.get(position);
                String txt=book.getName();
                switch (txt)
               {
                   case "CET4":
                   {
                       Intent intent = new Intent(mContext, CET4.class);
                       mContext.startActivity(intent);//相等进入添加界面
                       break;
                   }
                   case "CET6":
                   {
                       Intent intent = new Intent(mContext, CET6.class);
                       mContext.startActivity(intent);//相等进入添加界面
                       break;
                   }
                   default:
                   {
                       Toast.makeText(mContext, "尽请期待", Toast.LENGTH_SHORT).show();
                       break;
                   }

               }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        Book book = mBookList.get(position);
        holder.bookName.setText(book.getName());
        Glide.with(mContext).load(book.getId()).into(holder.bookImage);

    }

    @Override
    public int getItemCount()
    {
        return mBookList.size();
    }

}
