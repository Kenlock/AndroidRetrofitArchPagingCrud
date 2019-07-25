package com.melardev.android.crud.todos.show;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.melardev.android.crud.R;
import com.melardev.android.crud.datasource.common.entities.Todo;
import com.melardev.android.crud.datasource.common.repositories.BaseRepository;
import com.melardev.android.crud.datasource.remote.TodoApiDataSource;
import com.melardev.android.crud.datasource.remote.dtos.responses.SuccessResponse;
import com.melardev.android.crud.todos.base.BaseActivity;
import com.melardev.android.crud.todos.write.TodoCreateEditActivity;


public class TodoDetailsActivity extends BaseActivity {

    private long todoId;

    private TextView txtDetailsId;
    private TextView txtDetailsTitle;
    private TextView txtDetailsDescription;

    private CheckBox checkboxCompleted;

    private TextView txtDetailsCreatedAt;
    private TextView txtDetailsUpdatedAt;


    private Button btnDetailsEditTodo;
    private Button btnDetailsDeleteTodo;
    private Button btnDetailsGoHome;
    private TodoApiDataSource apiSource;


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_details);

        txtDetailsId = findViewById(R.id.txtDetailsId);
        txtDetailsTitle = findViewById(R.id.txtDetailsTitle);
        txtDetailsDescription = findViewById(R.id.txtDetailsDescription);
        checkboxCompleted = findViewById(R.id.checkboxCompleted);
        txtDetailsCreatedAt = findViewById(R.id.txtDetailsCreatedAt);
        txtDetailsUpdatedAt = findViewById(R.id.txtDetailsUpdatedAt);
        btnDetailsEditTodo = findViewById(R.id.btnDetailsEditTodo);
        btnDetailsDeleteTodo = findViewById(R.id.btnDetailsDeleteTodo);
        btnDetailsGoHome = findViewById(R.id.btnDetailsGoHome);

        Intent intent = getIntent();

        todoId = intent.getLongExtra("TODO_ID", -1);


        apiSource = TodoApiDataSource.getInstance();

        apiSource.getById(todoId, new BaseRepository.ResultListener<Todo>() {
            @Override
            public void onSuccess(Todo todo) {
                txtDetailsId.setText(String.valueOf(todo.getId()));
                txtDetailsTitle.setText(todo.getTitle());
                txtDetailsDescription.setText(todo.getDescription());
                checkboxCompleted.setChecked(todo.isCompleted());
                txtDetailsCreatedAt.setText(todo.getCreatedAt());
                txtDetailsUpdatedAt.setText(todo.getUpdatedAt());
            }

            @Override
            public void onError(String[] messages) {
                Toast.makeText(TodoDetailsActivity.this, TextUtils.join(",", messages), Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    public void onButtonClicked(View view) {
        Intent intent = new Intent();
        if (btnDetailsEditTodo == view) {
            intent.setComponent(new ComponentName(this, TodoCreateEditActivity.class));
            intent.putExtra("TODO_ID", todoId);
            startActivity(intent);
        } else if (btnDetailsDeleteTodo == view) {
            delete();
        } else if (btnDetailsGoHome == view) {
            finish();
        }
    }

    private void delete() {

        @SuppressLint("CheckResult") AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("Are you sure You want to delete this todo?")
                .setPositiveButton("Yes",
                        (dialogInterface, id) -> {
                            apiSource.deleteById(todoId, new BaseRepository.SuccessListener() {
                                @Override
                                public void onSuccess(SuccessResponse response) {
                                    if (response != null && response.isSuccess()) {
                                        Toast.makeText(TodoDetailsActivity.this, "Todo Deleted successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(TodoDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(String[] messages) {

                                }
                            });

                        })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();

    }

}

