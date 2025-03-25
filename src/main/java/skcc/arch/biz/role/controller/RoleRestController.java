package skcc.arch.biz.role.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import skcc.arch.app.dto.ApiResponse;
import skcc.arch.app.dto.PageInfo;
import skcc.arch.biz.role.controller.port.RoleServicePort;
import skcc.arch.biz.role.controller.request.RoleCreateRequest;
import skcc.arch.biz.role.domain.Role;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role")
public class RoleRestController {

    private final RoleServicePort roleServicePort;

    @PostMapping
    public ApiResponse<Role> createRole(@RequestBody @Valid RoleCreateRequest role) {
        return ApiResponse.ok(roleServicePort.save(role.toModel()));

    }

    @GetMapping("/{roleId}")
    public ApiResponse<Role> getRole(@PathVariable Long roleId) {
        return ApiResponse.ok(roleServicePort.findById(roleId));
    }

    @GetMapping
    public ApiResponse<List<Role>> getRoleListByCondition(Pageable pageable, Role role) {

        Page<Role> result = roleServicePort.findByCondition(pageable, role);
        List<Role> content = result.getContent();
        return ApiResponse.ok(content, PageInfo.fromPage(result));
    }

    @PatchMapping
    public ApiResponse<Role> updateRole(@RequestBody Role role) {
        return ApiResponse.ok(roleServicePort.update(role));
    }

}
