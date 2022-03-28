package me.danidev.core.managers.suggestions;

import me.danidev.core.Main;
import me.danidev.core.utils.file.FileConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class SuggestionManager {

    private final List<Suggestion> suggestions = Lists.newArrayList();
    private final Set<UUID> makingSuggestion = Sets.newHashSet();

    private final FileConfig suggestionsConfig = Main.get().getSuggestionsConfig();

    public void load() {
        this.suggestions.clear();

        Set<String> section = suggestionsConfig.getConfiguration().getConfigurationSection("SUGGESTIONS").getKeys(false);

        section.forEach(id -> {
            String author = suggestionsConfig.getString("SUGGESTIONS." + id + ".AUTHOR");
            String suggestion = suggestionsConfig.getString("SUGGESTIONS." + id + ".SUGGESTION");

            this.suggestions.add(new Suggestion(id, author, suggestion));
        });
    }

    public void save() {
        this.suggestions.forEach(suggestion -> {
            suggestionsConfig.getConfiguration().set("SUGGESTIONS." + suggestion.getID() + ".AUTHOR", suggestion.getAuthor());
            suggestionsConfig.getConfiguration().set("SUGGESTIONS." + suggestion.getID() + ".SUGGESTION", suggestion.getSuggestion());
        });
        suggestionsConfig.save();
    }
}
