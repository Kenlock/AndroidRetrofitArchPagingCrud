package com.melardev.android.crud.todos.write;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.melardev.android.crud.R;
import com.melardev.android.crud.datasource.common.entities.Todo;
import com.melardev.android.crud.datasource.common.repositories.BaseRepository;
import com.melardev.android.crud.datasource.remote.TodoApiDataSource;
import com.melardev.android.crud.datasource.remote.dtos.responses.SuccessResponse;
import com.melardev.android.crud.todos.base.BaseActivity;

public class TodoCreateEditActivity extends BaseActivity {

    private boolean editMode;
    private long todoId;

    private Button btnSaveTodo;

    private TextView txtId;
    private EditText eTxtTitle;
    private EditText eTxtDescription;

    private TodoApiDataSource apiSource;

    private CheckBox eCheckboxCompleted;
    private Todo todo;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_create_edit);


        txtId = findViewById(R.id.txtId);
        eTxtTitle = findViewById(R.id.eTxtTitle);
        eTxtDescription = findViewById(R.id.eTxtDescription);
        btnSaveTodo = findViewById(R.id.btnSaveTodo);
        eCheckboxCompleted = findViewById(R.id.eCheckboxCompleted);

        Intent intent = getIntent();
        if (intent != null) {
            this.todoId = intent.getLongExtra("TODO_ID", -1);
            this.editMode = todoId != -1;
        }

        this.apiSource = TodoApiDataSource.getInstance();

        if (editMode) {
            btnSaveTodo.setText("Save Changes");
            apiSource.getById(todoId, new BaseRepository.ResultListener<Todo>() {
                @Override
                public void onSuccess(Todo todo) {
                    TodoCreateEditActivity.this.todo = todo;
                    eTxtTitle.setText(todo.getTitle());
                    eTxtDescription.setText(todo.getDescription());
                    txtId.setText(String.valueOf(todo.getId()));
                    eCheckboxCompleted.setChecked(todo.isCompleted());
                }

                @Override
                public void onError(String[] messages) {
                    Toast.makeText(TodoCreateEditActivity.this, TextUtils.join(",", messages), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        } else {
            btnSaveTodo.setText("Create");
        }

    }

    @SuppressLint("CheckResult")
    public void saveTodo(View view) {

        String title = eTxtTitle.getText().toString();
        String description = eTxtDescription.getText().toString();
        boolean completed = eCheckboxCompleted.isChecked();

        if (editMode) {
            todo.setTitle(title);
            todo.setDescription(description);
            todo.setCompleted(completed);
            apiSource.update(todo, new BaseRepository.ResultListener<Todo>() {
                @Override
                public void onSuccess(Todo todo) {
                    Toast
                            .makeText(TodoCreateEditActivity.this, "Todo Updated!", Toast.LENGTH_LONG)
                            .show();
                    finish();
                }

                @Override
                public void onError(String[] messages) {
                    Toast.makeText(TodoCreateEditActivity.this, TextUtils.join(",", messages), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            Todo todo = new Todo();
            todo.setTitle(title);
            todo.setDescription(description);
            todo.setCompleted(completed);

            apiSource.create(todo, new BaseRepository.ResultListener<Todo>() {
                @Override
                public void onSuccess(Todo todo) {
                    Toast
                            .makeText(TodoCreateEditActivity.this, "Todo Created!", Toast.LENGTH_LONG)
                            .show();

                    getIntent().putExtra("TODO", todo);
                    finish();
                }

                @Override
                public void onError(String[] messages) {
                    Toast.makeText(TodoCreateEditActivity.this, TextUtils.join(",", messages), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

    }

    @SuppressLint("CheckResult")
    public void deleteTodo(View view) {

        if (editMode) {
            this.apiSource.deleteById(todoId, new BaseRepository.SuccessListener() {
                @Override
                public void onSuccess(SuccessResponse response) {
                    if (response != null && response.isSuccess()) {
                        Toast.makeText(TodoCreateEditActivity.this, "Todo Deleted Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(TodoCreateEditActivity.this, "Error Deleting todos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String[] messages) {
                    Toast.makeText(TodoCreateEditActivity.this, TextUtils.join(",", messages), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

    }
}
