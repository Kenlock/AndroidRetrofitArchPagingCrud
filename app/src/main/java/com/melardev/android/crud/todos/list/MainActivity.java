package com.melardev.android.crud.todos.list;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.melardev.android.crud.R;
import com.melardev.android.crud.datasource.common.entities.Todo;
import com.melardev.android.crud.datasource.remote.TodoApiDataSource;
import com.melardev.android.crud.todos.base.BaseActivity;
import com.melardev.android.crud.todos.show.TodoDetailsActivity;
import com.melardev.android.crud.todos.write.TodoCreateEditActivity;
import com.melardev.android.crud.utils.NetUtils;

public class MainActivity extends BaseActivity
        implements TodoListAdapter.TodoRowEventListener {

    private RecyclerView rvTodos;

    private TodoListAdapter todoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvTodos = findViewById(R.id.rvTodos);
        rvTodos.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        setupUi();
    }

    private void setupUi() {

        todoListAdapter = new TodoListAdapter(MainActivity.this, new TodoDiffUtil());
        rvTodos.setAdapter(todoListAdapter);

        TodoApiDataSource todoApiDataSource = TodoApiDataSource.getInstance();
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(5)
                .setInitialLoadSizeHint(5)
                .setEnablePlaceholders(false)
                .build();

        MainThreadExecutor executor = new MainThreadExecutor();
        PagedList<Todo> list =
                new PagedList.Builder<>(todoApiDataSource, config) // Can pass `pageSize` directly instead of `config`
                        // Do fetch operations on the main thread. We'll instead be using Retrofit's
                        // built-in enqueue() method for background api calls.
                        .setFetchExecutor(executor)
                        // Send updates on the main thread
                        .setNotifyExecutor(executor)
                        .build();

        todoListAdapter.submitList(list);
    }

    public void createTodo(View view) {
        Intent intent = new Intent(this, TodoCreateEditActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClicked(Todo todo) {
        Intent intent = new Intent(this, TodoDetailsActivity.class);
        intent.putExtra("TODO_ID", todo.getId());
        startActivity(intent);
    }

}
