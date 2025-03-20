package skcc.arch.biz.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import skcc.arch.app.exception.CustomException;
import skcc.arch.app.exception.ErrorCode;
import skcc.arch.biz.common.constants.CacheGroup;
import skcc.arch.biz.common.service.MyCacheService;
import skcc.arch.biz.menu.controller.port.MenuServicePort;
import skcc.arch.biz.menu.domain.Menu;
import skcc.arch.biz.menu.service.port.MenuRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuService implements MenuServicePort {

    private final MenuRepositoryPort menuRepositoryPort;
    private final MyCacheService myCacheService;

    @Override
    @Transactional
    public Menu save(Menu param) {

        // 순번이 없을 경우 마지막 순번 채번하여 세팅
        int lastMenuOrder = param.getMenuOrder() < 1 ? menuRepositoryPort.getLastMenuOrder(param.getParentId()) : param.getMenuOrder();
        Menu target = Menu.createMenu(param, lastMenuOrder);

        // 저장
        Menu saved = menuRepositoryPort.save(target);

        // 형제 순서 업데이트
        menuRepositoryPort.updateSiblingsMenuOrder(param.getParentId(), saved.getId(), param.getMenuOrder());

        return saved;
    }

    @Override
    public Page<Menu> findByCondition(Pageable pageable, Menu menu) {
        return menuRepositoryPort.findByCondition(pageable, menu);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Menu findByMenuId(Long menuId) {
        return menuRepositoryPort.findById(menuId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));
    }

    @Override
    @Transactional
    public Menu update(Menu param) {

        Menu dbData = menuRepositoryPort.findById(param.getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_ELEMENT));

        // 업데이트 요청 값이 없을 경우 기존 그대로 유지
        Menu target = Menu.updateMenu(param, dbData);

        // 순서를 입력받았을때
        if(param.getMenuOrder() > 0) {
            // 형제 순서 업데이트
            menuRepositoryPort.updateSiblingsMenuOrder(target.getParentId(), param.getId(), target.getMenuOrder());
        }

        // 실제 대상 업데이트
        return menuRepositoryPort.update(target);
    }

    @Override
    public List<Menu> getRootMenuList() {
        Map<Long, Menu> cacheMenu = (Map<Long, Menu>) myCacheService.get(CacheGroup.MENU, "ROOT", Map.class);
        return new ArrayList<>(cacheMenu.values());
    }
}
