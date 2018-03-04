package at.rseiler.homeauto.common.milight;

import com.google.common.base.Preconditions;
import lombok.Data;

@Data
public class MiLightCommand {
    private final String command;
    private final Integer group;
    private final Integer value;

    public int getArgumentCount() {
        return (group != null ? 1 : 0) + (value != null ? 1 : 0);
    }

    public static MiLightCommand fromString(String command) {
        Preconditions.checkNotNull(command, "command must not be null");
        String[] args = command.split(" ");

        Integer group = args.length > 1 ? Integer.valueOf(args[1]) : null;
        Integer value = args.length > 2 ?Integer.valueOf(args[2]) : null;
        return new MiLightCommand(args[0], group, value);
    }
}
