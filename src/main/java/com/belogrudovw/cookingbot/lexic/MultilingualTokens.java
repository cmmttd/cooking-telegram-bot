package com.belogrudovw.cookingbot.lexic;

import com.belogrudovw.cookingbot.domain.displayable.Languages;

import java.util.EnumMap;

import lombok.Getter;

@Getter
public enum MultilingualTokens implements StringToken {
    // TODO: 16/01/2024 Consider more readable way to setup
    CHOOSE_LANG_TOKEN("Choose language", "Выберите язык", "Sprache wählen", "选择语言", "Виберіть мову",
            "Choisissez la langue"),
    CHOOSE_MEASUREMENTS_TOKEN("Measurements unit format", "Выберите систему единиц измерений", "Maßeinheiten auswählen", "选择测量单位",
            "Виберіть одиниці вимірів", "Sélectionnez les unités de mesure"),
    CHOOSE_LIGHTNESS_TOKEN("Lightness of the desired dish (calories)", "Выберите относительную тяжесть (каллораж)",
            "Wählen Sie den Schweregrad des gewünschten Gerichts", "选择所需菜肴的严重程度", "Виберіть тягар бажаної страви",
            "Sélectionnez la sévérité du plat souhaité"),
    CHOOSE_DIFFICULTIES_TOKEN("How long do you plan to cook?", "Как много времени у вас есть на готовку?",
            "Wie viel Zeit haben Sie zum Kochen?", "你有多少时间做饭？", "Як багато часу у вас є на приготування?",
            "De combien de temps disposez-vous pour cuisiner?"),
    HOW_TO_COOK_TOKEN("Pick the option", "Как будем готовить?", "Wählen Sie die Option", "选择选项", "Виберіть варіант",
            "Choisissez l'option"),
    CONGRATULATIONS_TOKEN("Congratulations! Nice to cook with you!\nOne more time?",
            "Позвольте поздравить, Вы завершили приготовление! Ещё раз? :)", "Glückwunsch! Schön, mit dir zu kochen!\nEin Mal noch?",
            "恭喜！ 很高兴和你一起做饭！\n再一次？", "Щиро вітаю! Приємно готувати з вами!\nЩе раз?",
            "Toutes nos félicitations! Ravi de cuisiner avec vous !\nUne fois de plus?"),
    AWAIT_TOKEN("Beautiful wait animation is on its way...%nPlease wait until generation completes: %s, %s, %s, %s",
            "Скоро тут появится красивая анимация ожидания...%nПодождите, пока завершится генерация: %s, %s, %s, %s",
            "Eine wunderschöne Warteanimation ist unterwegs...%nWarten Sie, bis die Generierung abgeschlossen ist: %s, %s, %s, %s",
            "美丽的等待动画即将开始...%n等待生成完成：%s, %s, %s, %s",
            "Чудова анімація очікування вже в дорозі...%nЗачекайте, доки завершиться генерація: %s, %s, %s, %s",
            "Une belle animation d'attente est en route...%nAttendez la fin de la génération: %s, %s, %s, %s"),
    EXPECT_CUSTOM_QUERY_TOKEN("Enter the desired dish/cuisine, or list the ingredients you have, or just write your mood...\n"
            + "_Max 200 symbols, so the shorter the better :)_",
            "Введите желаемое блюдо/страну происхождения блюда, или перечислите ингредиенты, которые у вас есть, ну или просто " +
                    "напишите своё настроение...\n_Макс. 200 символов, чем короче, тем лучше :)_",
            "Geben Sie das gewünschte Gericht ein, listen Sie die Zutaten auf, die Sie haben, oder schreiben Sie einfach Ihre " +
                    "Stimmung ...\n_Maximal 200 Symbole, also je kürzer, desto besser :)_",
            "输入想要的菜肴，或者列出您拥有的食材，或者只是写下您的心情...\n_最多 200 个符号，因此越短越好:)_",
            "Введіть бажану страву, або перерахуйте інгредієнти, які у вас є, або просто напишіть свій настрій...\n"
                    + "_Максимум 200 символів, тож чим коротше, тим краще :)_",
            "Saisissez le plat souhaité, ou listez les ingrédients dont vous disposez, ou écrivez simplement votre humeur...\n"
                    + "_Max 200 symboles, donc plus c'est court, mieux c'est :)_"),
    PICK_HISTORY_TOKEN("Pick from the history", "Выберите из истории", "Wählen Sie aus der Geschichte", "从历史中挑选",
            "Виберіть з історії", "Choisissez dans l'histoire"),
    METRIC_BUTTON_TOKEN("Metric (kg, gram, liter, etc)", "Метрическая (кг, грамм, литр и т.д.)",
            "Metrisch (kg, Gramm, Liter usw.)", "公制（千克、克、升等）", "Метрична (кг, грам, літр тощо)",
            "Métrique (kg, gramme, litre, etc.)"),
    IMPERIAL_BUTTON_TOKEN("Imperial (inch, feet, gallon, etc)", "Имперская (дюймы, футы, галлоны и т.д.)",
            "Imperial (Zoll, Fuß, Gallone usw.)", "英制（英寸、英尺、加仑等）", "Британська (дюйми, фути, галони тощо)",
            "Impérial (pouces, pieds, gallons, etc.)"),
    LIGHT_BUTTON_TOKEN("Light", "Легкое", "Leichtes", "清淡菜", "Легка", "Léger"),
    MODERATE_BUTTON_TOKEN("Moderate", "Среднее", "Mittel", "中", "Cередній", "Moyen"),
    HEAVY_BUTTON_TOKEN("Heavy", "Тяжелое", "Schweres", "重盘", "Тяжка", "Lourd"),
    ANY_BUTTON_TOKEN("Any", "Любое", "Beliebig", "任何", "Будь-яке", "N'importe lequel"),
    MINUTES_TOKEN("min", "мин", "Minuten", "分钟", "хвилин", "min"),
    REQUEST_CUSTOM_TOKEN("Personal recipe", "Персональный рецепт", "Persönliches Rezept",
            "个人食谱", "Персональний рецепт", "Recette personnelle"),
    REQUEST_RANDOM_TOKEN("Random recipe", "Случайный рецепт", "Zufälliges Rezept",
            "随机配方", "Випадковий рецепт", "Recette aléatoire"),
    REQUEST_HISTORY_TOKEN("History", "История", "Rezeptgeschichte", "食谱历史", "Історія", "Histoire"),
    REQUEST_RESET_PREFERENCES_TOKEN("Reset preferences", "Сбросить настройки", "Einstellungen zurücksetzen", "重置偏好设置",
            "Скинути налаштування", "Réinitialiser les options"),
    ADDITIONAL_PARAMETERS_TOKEN(", you also asked for: ", ", а ещё вы попросили: ", ", Sie haben auch nach Folgendem gefragt: ",
            "，您还要求： ", ", ви також просили: ", ", vous avez également demandé: "),
    SHOW_CALORIC_TOKEN("Caloric intake", "Сколько калорий", "Kalorienaufnahme", "热量摄入", "Калорійність",
            "Apport calorique"),
    SHOW_IMAGE_TOKEN("Generate image", "Сгенерировать картинку", "Bild erzeugen", "生成图片", "Згенерувати картинку",
            "Générer une image"),
    CONTACT_SUPPORT_TOKEN("Something went wrong, please contact the developers from the bot description",
            "Что-то пошло не так, пожалуйста обратитесь к разработчикам из описания бота",
            "Es ist ein Fehler aufgetreten. Bitte wenden Sie sich über die Bot-Beschreibung an die Entwickler",
            "出现问题，请通过机器人描述联系开发人员", "Щось пішло не так, зверніться до розробників з опису бота",
            "Quelque chose s'est mal passé, veuillez contacter les développeurs à partir de la description du bot"),
    ;

    private final EnumMap<Languages, String> tokens;

    MultilingualTokens(String en, String ru, String de, String ch, String ua, String fr) {
        EnumMap<Languages, String> map = new EnumMap<>(Languages.class);
        map.put(Languages.EN, en);
        map.put(Languages.RU, ru);
        map.put(Languages.DE, de);
        map.put(Languages.CH, ch);
        map.put(Languages.UA, ua);
        map.put(Languages.FR, fr);
        this.tokens = map;
    }

    @Override
    public String in(Languages lang) {
        return tokens.getOrDefault(lang, "Not implemented");
    }
}