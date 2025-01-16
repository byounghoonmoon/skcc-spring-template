package skcc.arch.mock;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import skcc.arch.user.domain.User;
import skcc.arch.user.service.port.UserRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FakeUserRepository implements UserRepository {

    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<User> data = Collections.synchronizedList(new ArrayList<>());
    
    @Override
    public Optional<User> findByEmail(String email) {
        return data.stream().filter(item -> Objects.equals(item.getEmail(), email)).findAny();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId() == 0) {
            User newUser = User.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .status(user.getStatus())
                    .build();
            data.add(newUser);
            return newUser;
        } else {
            data.removeIf(item -> Objects.equals(item.getId(), user.getId()));
            data.add(user);
            return user;
        }
    }

    @Override
    public List<User> findAllUsers() {
        return data.stream().toList();
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), data.size());
        List<User> subList = data.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(subList, pageable, data.size());
    }
}
