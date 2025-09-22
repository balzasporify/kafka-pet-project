package com.balza.todoapp.util;

public interface RestApiUrls {
    String TASK_API_BASE_PATH = "/api/v1";
    String TASKS_BASE_PATH = "/tasks";
    String GET_TASK_PATH = TASKS_BASE_PATH;
    String GET_TASK_BY_ID_PATH = GET_TASK_PATH + "/{id}";
    String POST_TASK_PATH = TASKS_BASE_PATH;
    String DELETE_TASK_PATH = TASKS_BASE_PATH + "/{id}";
    String PUT_TASK_PATH = TASKS_BASE_PATH;
    String PATCH_TASK_PATH = TASKS_BASE_PATH + "/{id}/status";
}
