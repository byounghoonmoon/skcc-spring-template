package skcc.arch.biz.role.infrastructure;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import skcc.arch.biz.menu.domain.Menu;
import skcc.arch.biz.menu.infrastructure.jpa.MenuConditionBuilder;
import skcc.arch.biz.menu.infrastructure.jpa.MenuEntity;
import skcc.arch.biz.role.domain.Role;
import skcc.arch.biz.role.infrastructure.jpa.RoleCondition;
import skcc.arch.biz.role.infrastructure.jpa.RoleConditionBuilder;
import skcc.arch.biz.role.infrastructure.jpa.RoleEntity;
import skcc.arch.biz.role.infrastructure.jpa.RoleRepositoryJpa;
import skcc.arch.biz.role.service.port.RoleRepositoryPort;

import java.util.List;
import java.util.Optional;

import static skcc.arch.biz.menu.infrastructure.jpa.QMenuEntity.menuEntity;
import static skcc.arch.biz.role.infrastructure.jpa.QRoleEntity.roleEntity;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryJpaCustomImpl implements RoleRepositoryPort {

    private final RoleRepositoryJpa repository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Role save(Role role) {
        return repository.save(RoleEntity.from(role)).toModel();
    }

    @Override
    public Optional<Role> findById(Long id) {
        if (id == null) return Optional.empty();
        Optional<RoleEntity> roleEntity = repository.findById(id);
        return roleEntity.map(RoleEntity::toModel);
    }

    @Override
    public Page<Role> findByCondition(Pageable pageable, Role search) {
        BooleanBuilder booleanBuilder = RoleConditionBuilder.roleCondition(RoleCondition.from(search));

        List<Role> content = queryFactory.selectFrom(roleEntity)
                .where(booleanBuilder)
                .orderBy(roleEntity.roleId.asc(), roleEntity.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(RoleEntity::toModel)
                .toList();

        Long totalCount = queryFactory.select(roleEntity.count())
                .from(roleEntity)
                .where(booleanBuilder)
                .fetchOne();
        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Role update(Role Role) {
        return null;
    }
}
