package by.bsuir.controller.rest;

import by.bsuir.controller.exception.NoSuchEntityException;
import by.bsuir.controller.exception.PresentEntityException;
import by.bsuir.controller.exception.UnauthorizedException;
import by.bsuir.domain.ESystemRoles;
import by.bsuir.domain.Room;
import by.bsuir.domain.viewhelper.View;
import by.bsuir.repository.RepositoryUtils;
import by.bsuir.repository.springdata.IRoomDataRepository;
import by.bsuir.security.utils.PrincipalUtil;
import by.bsuir.util.MyMessages;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/room")
@RequiredArgsConstructor
public class RoomRestController {

    private final IRoomDataRepository roomRepository;

    private final RepositoryUtils repositoryUtils;

    private final PrincipalUtil principalUtil;

    @GetMapping
    @ApiOperation(value = "Find all rooms")
    @JsonView(View.PublicView.class)
    public ResponseEntity<List<Room>> findAll() {
        return ResponseEntity.ok(roomRepository.findAll());
    }

    @GetMapping("/search/{id}")
    @ApiOperation(value = "Find room by id")
    @JsonView(View.PublicView.class)
    public ResponseEntity<Room> findById(@PathVariable Integer id) {
        Room foundRoom = repositoryUtils.findRoomById(roomRepository, id);
            return ResponseEntity.ok(foundRoom);
    }

    @ApiOperation(value = "Create Room. Role_Admin only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header"),
            @ApiImplicitParam(name = "Room_number",
                    value = "New room number", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(
            HttpServletRequest request,
            @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (isAdmin) {
            String newRoomNumber = request.getHeader("Room_number");

            Optional<Room> searchRoomResult = roomRepository.findByRoomNumberIgnoreCase
                    (newRoomNumber);
            if (searchRoomResult.isPresent()) {
                throw new PresentEntityException("The entity " +
                        searchRoomResult.get().getClass().getSimpleName() +
                        " \"" + searchRoomResult.get().getRoomNumber() +
                        "\" " + " is all ready created");
            }
            Room newRoom = new Room();
            newRoom.setRoomNumber(newRoomNumber);
            return ResponseEntity.ok(roomRepository.save(newRoom));
        } else {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
    }

    @ApiOperation(value = "Update room by ID. Role_Admin only")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-Auth-Token",
                    value = "token", required = true,
                    dataType = "string",
                    paramType = "header"),
            @ApiImplicitParam(name = "Room_number",
                    value = "New room number", required = true,
                    dataType = "string",
                    paramType = "header")
    })
    @PostMapping("/update/{id}")
    public ResponseEntity<Room> updateRoom(
            HttpServletRequest request,
            @PathVariable Integer id,
            @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (isAdmin) {
            Optional<Room> foundRoom = roomRepository.findById(id);

            if (foundRoom.isEmpty()) {
                throw new NoSuchEntityException("No such room with id:" + id);
            }
            Room updatedRoom = foundRoom.get();
            updatedRoom.setRoomNumber(request.getHeader("Room_number"));

            return ResponseEntity.ok(roomRepository.save(updatedRoom));
        } else {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
    }

    @ApiOperation(value = "Hard delete room by Id")
    @DeleteMapping("/delete_hard/{id}")
    @ApiImplicitParam(name = "X-Auth-Token",
            value = "token", required = true,
            dataType = "string",
            paramType = "header")
    public void deleteUserHard(@PathVariable Integer id,
                               @ApiIgnore Principal principal) {

        boolean isAdmin = principalUtil.getAuthorities(principal)
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals(ESystemRoles.ROLE_ADMIN.toString()));

        if (!isAdmin) {
            throw new UnauthorizedException(MyMessages.BAD_PERMISSIONS);
        }
        roomRepository.deleteById(id);
    }
}
