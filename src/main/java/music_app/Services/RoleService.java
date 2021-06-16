package music_app.Services;

import music_app.Model.Role;
import music_app.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("RoleService")
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Transactional
    public List<Role> rolefindAll() {
        return roleRepository.findAll();
    }


}
