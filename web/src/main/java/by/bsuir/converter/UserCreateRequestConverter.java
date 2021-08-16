package by.bsuir.converter;

import by.bsuir.controller.request.UserCreateRequest;
import by.bsuir.domain.Credential;
import by.bsuir.domain.User;
import by.bsuir.repository.springdata.IDepartmentDataRepository;
import by.bsuir.repository.springdata.IRateDataRepository;
import by.bsuir.repository.springdata.IRoomDataRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.Date;

@Component
public class UserCreateRequestConverter extends EntityConverter<UserCreateRequest, User> {

    private final IRoomDataRepository roomRepository;

    private final IDepartmentDataRepository departmentRepository;

    private final IRateDataRepository rateRepository;

    private final PasswordEncoder passwordEncoder;

    public UserCreateRequestConverter(IRoomDataRepository roomRepository, IDepartmentDataRepository departmentRepository, IRateDataRepository rateRepository, PasswordEncoder passwordEncoder) {
        this.roomRepository = roomRepository;
        this.departmentRepository = departmentRepository;
        this.rateRepository = rateRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User convert(UserCreateRequest request) {

        User user = new User();
        user.setCreated(new Timestamp(new Date().getTime()));
        user.setRoom(roomRepository.findById(
                request.getRoomId()).orElseThrow(EntityNotFoundException::new));
        user.setDepartment(departmentRepository.findById(
                request.getDepartmentId()).orElseThrow(EntityNotFoundException::new));
        user.setRate(rateRepository.findById(
                request.getRoomId()).orElseThrow(EntityNotFoundException::new));

        Credential credential = new Credential();
        credential.setPassword(passwordEncoder.encode(request.getPassword()));

        /* Role foundRole = roleRepository.findById(createRequest.getRoleId()).get();
        Set<Role> roles = new HashSet<>();
        roles.add(foundRole);*/

        return doConvert(request, user);
    }
}
