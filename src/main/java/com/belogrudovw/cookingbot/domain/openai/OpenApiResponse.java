package com.belogrudovw.cookingbot.domain.openai;

import java.util.List;

public record OpenApiResponse(List<Choice> choices) {
}