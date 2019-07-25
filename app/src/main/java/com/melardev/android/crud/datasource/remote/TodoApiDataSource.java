package com.melardev.android.crud.datasource.remote;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.google.gson.Gson;
import com.melardev.android.crud.datasource.common.entities.Todo;
import com.melardev.android.crud.datasource.common.repositories.TodoRepository;
import com.melardev.android.crud.datasource.remote.api.TodoApi;
import com.melardev.android.crud.datasource.remote.dtos.responses.ErrorDataResponse;
import com.melardev.android.crud.datasource.remote.dtos.responses.SuccessResponse;
import com.melardev.android.crud.datasource.remote.dtos.responses.TodoPagedResponse;
import com.melardev.android.crud.utils.NetUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoApiDataSource extends PageKeyedDataSource<Integer, Todo> implements TodoRepository {

    private static TodoApiDataSource instance;
    private static final Object lock = new Object();
    private final TodoApi todoApi;
    private final Gson gson;

    public TodoApiDataSource(TodoApi todoApi) {
        this.todoApi = todoApi;
        this.gson = new Gson();
    }

    public static TodoApiDataSource getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new TodoApiDataSource(NetUtils.getTodoApi());
                }
            }
        }

        return instance;
    }


    @SuppressLint("CheckResult")
    @Override
    public void loadInitial(@NonNull final LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Todo> callback) {
        Call<TodoPagedResponse> fetchTodosRequest = this.todoApi.fetchTodos(1, params.requestedLoadSize);
        fetchTodosRequest.enqueue(new Callback<TodoPagedResponse>() {
            @Override
            public void onResponse(Call<TodoPagedResponse> call, Response<TodoPagedResponse> response) {
                TodoPagedResponse body = response.body();
                callback.onResult(body.getTodos(),
                        body.getPageMeta().isHasPrevPage() ? body.getPageMeta().getPrevPageNumber() : null,
                        body.getPageMeta().isHasNextPage() ? body.getPageMeta().getNextPageNumber() : null);
            }

            @Override
            public void onFailure(Call<TodoPagedResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Todo> callback) {
        System.out.println("loadBefore");
        // For Api apps this is not needed, because the data does not change
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Todo> callback) {
        Call<TodoPagedResponse> fetchRequest = this.todoApi.fetchTodos(params.key, params.requestedLoadSize);
        fetchRequest.enqueue(new Callback<TodoPagedResponse>() {
            @Override
            public void onResponse(Call<TodoPagedResponse> call, Response<TodoPagedResponse> response) {
                TodoPagedResponse body = response.body();
                callback.onResult(body.getTodos(),
                        body.getPageMeta().isHasNextPage() ? body.getPageMeta().getNextPageNumber() : null);
            }

            @Override
            public void onFailure(Call<TodoPagedResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void getById(Long todoId, ResultListener<Todo> listener) {
        Call<Todo> call = this.todoApi.fetchTodo(todoId);
        call.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                if (response.body() != null && response.code() == 200) {
                    listener.onSuccess(response.body());
                } else {
                    try {
                        ErrorDataResponse errors = gson.fromJson(response.errorBody().string(), ErrorDataResponse.class);
                        listener.onError(errors.getFullMessages());
                    } catch (IOException e) {
                        listener.onError(new String[]{""});
                    }
                }
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                listener.onError(new String[]{t.getMessage()});
            }
        });
    }

    @Override
    public void create(Todo todo, ResultListener<Todo> listener) {
        Call<Todo> request = todoApi.createTodo(todo);
        request.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {

            }
        });
    }

    @Override
    public void update(Todo entity, ResultListener<Todo> listener) {
        Call<Todo> req = todoApi.update(entity.getId(), entity);
        req.enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {

            }
        });
    }

    @Override
    public void deleteById(Long todoId, SuccessListener listener) {
        Call<SuccessResponse> req = todoApi.deleteTodo(todoId);
        req.enqueue(new Callback<SuccessResponse>() {
            @Override
            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<SuccessResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void delete(Todo entity, SuccessListener listener) {
        deleteById(entity.getId(), listener);
    }

    @Override
    public void getAll(int page, int pageSize, ResultPageListener listener) {
        Call<TodoPagedResponse> call = this.todoApi.fetchTodos();
        call.enqueue(new Callback<TodoPagedResponse>() {
            @Override
            public void onResponse(Call<TodoPagedResponse> call, Response<TodoPagedResponse> response) {
                if (response.body() != null && response.code() == 200) {
                    listener.onSuccess(response.body());
                } else {
                    try {
                        ErrorDataResponse errors = gson.fromJson(response.errorBody().string(), ErrorDataResponse.class);
                        listener.onError(errors.getFullMessages());
                    } catch (IOException e) {
                        listener.onError(new String[]{""});
                    }

                }

            }

            @Override
            public void onFailure(Call<TodoPagedResponse> call, Throwable t) {
                call.cancel();
                listener.onError(new String[]{t.getMessage()});
            }
        });
    }
}
