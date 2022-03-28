package me.danidev.core.managers.user;

import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class ConsoleUser extends ServerParticipator implements ConfigurationSerializable
{
    public static final UUID CONSOLE_UUID;
    String name;
    
    static {
        CONSOLE_UUID = UUID.fromString("29f26148-4d55-4b4b-8e07-900fda686a67");
    }
    
    public ConsoleUser() {
        super(ConsoleUser.CONSOLE_UUID);
        this.name = "CONSOLE";
    }
    
    public ConsoleUser(final Map<String, Object> map) {
        super(map);
        this.name = "CONSOLE";
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
