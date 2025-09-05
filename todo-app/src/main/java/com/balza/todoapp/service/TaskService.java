package com.balza.todoapp.service;

import com.balza.todoapp.dto.CreateTaskRequestDto;
import com.balza.todoapp.dto.TaskResponseDto;
import com.balza.todoapp.dto.UpdateTaskRequestDto;
import com.balza.todoapp.exception.TaskNotFoundException;
import com.balza.todoapp.model.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    /**
     * Создает новую задачу на основе предоставленных данных.
     *
     * @param requestDto DTO с данными для создания задачи.
     * @return DTO созданной задачи.
     */
    TaskResponseDto createTask(CreateTaskRequestDto requestDto);

    /**
     * Обновляет существующую задачу.
     *
     * @param requestDto DTO с ID и обновленными данными задачи.
     * @return DTO обновленной задачи.
     * @throws TaskNotFoundException если задача с указанным ID не найдена.
     */
    TaskResponseDto updateTask(UpdateTaskRequestDto requestDto);

    /**
     * Обновляет статус существующей задачи.
     *
     * @param id     ID задачи для обновления.
     * @param status Новый статус для задачи.
     * @return DTO обновленной задачи.
     * @throws TaskNotFoundException если задача с указанным ID не найдена.
     */
    TaskResponseDto updateTaskStatus(Long id, Status status);

    /**
     * Находит задачу по ее уникальному идентификатору.
     *
     * @param id ID искомой задачи.
     * @return DTO найденной задачи.
     * @throws TaskNotFoundException если задача с указанным ID не найдена.
     */
    TaskResponseDto getById(Long id);

    /**
     * Удаляет задачу по ее уникальному идентификатору.
     * Операция идемпотентна: не выбрасывает ошибку, если задача уже удалена.
     *
     * @param id ID задачи для удаления.
     */
    void deleteById(Long id);

    /**
     * Возвращает страницу задач с возможностью фильтрации по статусу и сортировки.
     *
     * @param status   Статус для фильтрации (может быть null).
     * @param pageable Объект с параметрами пагинации и сортировки.
     * @return Страница (Page) с DTO задач.
     */
    Page<TaskResponseDto> getTasks(Status status, Pageable pageable);
}
