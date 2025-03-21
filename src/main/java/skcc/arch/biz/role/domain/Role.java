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
}
