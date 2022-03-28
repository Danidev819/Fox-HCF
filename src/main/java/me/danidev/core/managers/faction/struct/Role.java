package me.danidev.core.managers.faction.struct;

public enum Role {

    LEADER("LEADER", 0, "Leader", "***"), 
    COLEADER("COLEADER", 1, "Co-Leader", "**"), 
    CAPTAIN("CAPTAIN", 2, "Captain", "*"), 
    MEMBER("MEMBER", 3, "Member", "");
    
    private final String name;
    private final String astrix;

    Role(String s, final int n, final String name, final String astrix) {
        this.name = name;
        this.astrix = astrix;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getAstrix() {
        return this.astrix;
    }
}
