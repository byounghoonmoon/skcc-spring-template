package skcc.arch.biz.role.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Role {
    private final Long id;
    private final String roleId;
    private final String name;
    private final LocalDateTime createdDate;
    private final LocalDateTime lastModifiedDate;

    // 생성규칙은 굳이 만들지 않음 필요하면 프로젝트 특성에 따라 ID 규칙 채번 등을 넣으면 됨
    // 해당 샘플 프로젝트는 ROLE_ID 및 ROLE_NAME 은 필수값으로 가정하였음

    public static Role update(Role param, Role dbData) {

        return Role.builder()
                .id(param.getId())
                .roleId(param.getRoleId() != null ? param.getRoleId() : dbData.getRoleId())
                .name(param.getName() != null ? param.getName() : dbData.getName())
                .build();
    }
}
