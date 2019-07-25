package com.melardev.android.crud.datasource.common.repositories;


import com.melardev.android.crud.datasource.common.entities.Todo;
import com.melardev.android.crud.datasource.common.models.DataSourceOperation;
import com.melardev.android.crud.datasource.remote.dtos.responses.TodoPagedResponse;

import java.util.List;

import io.reactivex.Observable;

public interface TodoRepository extends BaseRepository<Long, Todo> {

    interface ResultPageListener extends BaseListener {
        void onSuccess(TodoPagedResponse todo);
    }

    void getAll(int page, int pageSize, ResultPageListener listener);

}
