package com.example.demo.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class MeetingTypeRequest {
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 64, message = "Название не может превышать 64 символа")
    private String title;

    @Size(max = 256, message = "Описание не может превышать 256 символов")
    private String description;

    @NotNull(message = "Длительность встречи не может быть пустой")
    private Integer duration;

    @NotNull(message = "Максимальное количество дней для записи должно быть указано")
    private Integer maxDaysInAdvance;

    private List<Long> locationIds;

    private List<@Valid MeetingTypeTimeRangeRequest> timeRanges;
}
