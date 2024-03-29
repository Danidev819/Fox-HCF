package me.danidev.core.utils.staff;

import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;

public enum StaffPriority {

    OWNER("OWNER", 0, 6),
    HEADADMIN("HEADADMIN", 1, 5),
    STAFFMANAGER("STAFFMANAGER", 2, 4),
    ADMIN("ADMIN", 3, 3),
    MODERATOR("MODERATOR", 4, 2),
    TRIAL("TRIAL", 5, 1),
    NONE("NONE", 6, 0);

    private static final ImmutableMap<Integer, StaffPriority> BY_ID;

    static {
        final ImmutableMap.Builder<Integer, StaffPriority> builder = new ImmutableMap.Builder<>();
        StaffPriority[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final StaffPriority staffPriority = values[i];
            builder.put(staffPriority.priorityLevel, staffPriority);
        }
        BY_ID = builder.build();
    }

    private final int priorityLevel;

    StaffPriority(final String s, final int n, final int priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public static StaffPriority of(final int level) {
        return StaffPriority.BY_ID.get(level);
    }

    public static StaffPriority of(final Player player) {
        StaffPriority[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final StaffPriority staffPriority = values[i];
            if (player.hasPermission("staffpriority." + staffPriority.priorityLevel)) {
                return staffPriority;
            }
        }
        return StaffPriority.NONE;
    }

    public int getPriorityLevel() {
        return this.priorityLevel;
    }

    public boolean isMoreThan(final StaffPriority other) {
        return this.priorityLevel > other.priorityLevel;
    }
}
