package com.storeapp.group.dto;

/**
 * DTO per lo stato di uscita dal gruppo
 */
public class LeaveGroupStatusDto {
    public boolean canLeave;
    public String reason;
    public boolean willDeleteGroup;
    public boolean isOnlyMember;
    public boolean isLastAdmin;
    public long memberCount;
    public long adminCount;

    public LeaveGroupStatusDto() {
    }

    public LeaveGroupStatusDto(boolean canLeave, String reason, boolean willDeleteGroup, 
                               boolean isOnlyMember, boolean isLastAdmin, 
                               long memberCount, long adminCount) {
        this.canLeave = canLeave;
        this.reason = reason;
        this.willDeleteGroup = willDeleteGroup;
        this.isOnlyMember = isOnlyMember;
        this.isLastAdmin = isLastAdmin;
        this.memberCount = memberCount;
        this.adminCount = adminCount;
    }

    public static LeaveGroupStatusDto canLeave() {
        return new LeaveGroupStatusDto(true, null, false, false, false, 0, 0);
    }

    public static LeaveGroupStatusDto willDeleteGroup(long memberCount) {
        return new LeaveGroupStatusDto(true, 
            "Essendo l'unico membro, uscendo verrà cancellato il gruppo", 
            true, true, false, memberCount, 0);
    }

    public static LeaveGroupStatusDto lastAdmin(long memberCount, long adminCount) {
        return new LeaveGroupStatusDto(false, 
            "Non puoi abbandonare il gruppo, prima devi selezionare un membro come amministratore", 
            false, false, true, memberCount, adminCount);
    }
}
