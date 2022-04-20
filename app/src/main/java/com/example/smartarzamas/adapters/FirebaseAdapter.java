package com.example.smartarzamas.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartarzamas.firebaseobjects.User;

import java.util.ArrayList;

public abstract class FirebaseAdapter<T, VH extends FirebaseAdapter.FirebaseHolder> extends RecyclerView.Adapter<VH> {

    protected boolean isAdmin; // список для администратора или пользователи
    protected Context context; // контекст активности/фрагмента
    protected User user; // пользователь
    protected ArrayList<T> items = new ArrayList<T>(); // список элементов
    protected OnStateClickListener<T> onItemClickListener; // слушатель нажатий на элемент списка

    // констуктор
    public FirebaseAdapter(Context context, User user, boolean isAdmin, ArrayList<T> items, OnStateClickListener<T> onItemClickListener) {
        this.context = context;
        this.user = user;
        this.isAdmin = isAdmin;
        this.items = items;
        this.onItemClickListener = onItemClickListener;
    }

    // создание холдера
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateHolder(parent, viewType);
    }

    // обновление холдера
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(position);
        if (isAdmin)
            holder.bindAdmin(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(items.get(holder.getAdapterPosition()));
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onItemClickListener.onLongClick(items.get(holder.getAdapterPosition()));
                return false;
            }
        });
    }

    // получение размера списка
    @Override
    public int getItemCount() {
        return items.size();
    }

    // создание холдера
    protected abstract VH onCreateHolder(@NonNull ViewGroup parent, int viewType);
    // получение элемента по позиции
    protected final T getItem(int position){
        return items.get(position);
    }

    // абстрактный холдер
    public abstract static class FirebaseHolder<T> extends RecyclerView.ViewHolder {

        protected T item; // элемент

        // конструктор
        public FirebaseHolder(@NonNull View itemView) {
            super(itemView);
        }

        // метод обновления холдера
        public abstract void bind(int position);
        // метод обновления холдера если пользователь - администратор
        public abstract void bindAdmin(int position);
    }

    // слушатель нажатий
    public interface OnStateClickListener<T> {
        void onClick(T item);
        void onLongClick(T item);
    }
}
