package com.belogrudovw.cookingbot.service;

import com.belogrudovw.cookingbot.domain.Chat;

public interface CookingScheduleService {
    void scheduleNexStep(Chat chat);

    void cancelSchedule(Chat chat);
}