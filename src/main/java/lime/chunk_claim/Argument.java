package lime.chunk_claim;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.ISuggestionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Argument implements ArgumentType<String> {

    public static Argument argument() {
        return new Argument();
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        return reader.readString().toLowerCase();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        List<String> commands = new ArrayList<>();

        for(COMMANDS command : COMMANDS.values()) {
            commands.add(command.toString().toLowerCase());
        }

        return ISuggestionProvider.suggest(commands, builder);
    }

    public enum COMMANDS {
        INFO, LIST, CLAIM, UNCLAIM, ADD_MEMBER, REMOVE_MEMBER, EVICT, SUDO_UNCLAIM;
    }
}
