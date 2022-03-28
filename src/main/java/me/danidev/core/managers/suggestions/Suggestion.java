package me.danidev.core.managers.suggestions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Suggestion {

    private String ID;
    private String author;
    private String suggestion;
}
