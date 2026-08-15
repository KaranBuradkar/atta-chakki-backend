package com.attachakki.components.permissions;

import com.attachakki.entity.type.Module;
import com.attachakki.entity.type.PermissionLevel;
import jakarta.validation.constraints.NotNull;

public class StaffPermissionRequestDto {

    @NotNull(message = "module required")
    private Module module;
    @NotNull(message = "permission level required")
    private PermissionLevel level;

    public StaffPermissionRequestDto() {}

    public StaffPermissionRequestDto(
            Module module,
            PermissionLevel level
    ) {
        this.module = module;
        this.level = level;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public PermissionLevel getLevel() {
        return level;
    }

    public void setLevel(PermissionLevel level) {
        this.level = level;
    }
}
