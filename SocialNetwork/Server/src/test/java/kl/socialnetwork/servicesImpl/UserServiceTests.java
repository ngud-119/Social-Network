package kl.socialnetwork.servicesImpl;

import kl.socialnetwork.domain.entities.User;
import kl.socialnetwork.domain.entities.UserRole;
import kl.socialnetwork.domain.modles.serviceModels.UserServiceModel;
import kl.socialnetwork.domain.modles.viewModels.user.UserCreateViewModel;
import kl.socialnetwork.repositories.PostRepository;
import kl.socialnetwork.repositories.RoleRepository;
import kl.socialnetwork.repositories.UserRepository;
import kl.socialnetwork.services.UserService;
import kl.socialnetwork.testUtils.RolesUtils;
import kl.socialnetwork.testUtils.UsersUtils;
import kl.socialnetwork.utils.responseHandler.exceptions.CustomException;
import kl.socialnetwork.validations.serviceValidation.services.PostValidationService;
import kl.socialnetwork.validations.serviceValidation.services.UserValidationService;
import org.h2.engine.Role;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static kl.socialnetwork.utils.constants.ResponseMessageConstants.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository mockUserRepository;

    @MockBean
    private RoleRepository mockRoleRepository;

    @MockBean
    private UserValidationService mockUserValidationService;

    private List<User> userList;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUpTest() {
        userList = new ArrayList<>();
        when(mockUserRepository.findAll())
                .thenReturn(userList);
    }

    @Test
    public void getAllUsers_whenUsersHasRootOrAdminRole2Users_2Users() throws Exception {
        // Arrange
        List<User> users = UsersUtils.getUsers(2);
        UserRole adminRole = RolesUtils.createAdminRole();

        users.get(0).getAuthorities().add(adminRole);
        users.get(1).getAuthorities().add(adminRole);

        userList.addAll(users);

        when(mockUserRepository.findById(anyString()))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        // Act
        List<UserServiceModel> allUsers = userService.getAllUsers("1");

        // Assert
        User expected = userList.get(0);
        UserServiceModel actual = allUsers.get(0);

        assertEquals(2, allUsers.size());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertTrue(actual.getAuthorities().contains(adminRole));

        verify(mockUserRepository).findAll();
        verify(mockUserRepository, times(1)).findAll();
    }

    @Test()
    public void getAllUsers_whenUserHasUserRole_throwCustomException() throws Exception {
        // Arrange
        List<User> users = UsersUtils.getUsers(2);
        UserRole adminRole = RolesUtils.createUserRole();

        users.get(0).getAuthorities().add(adminRole);
        users.get(1).getAuthorities().add(adminRole);

        userList.addAll(users);

        when(mockUserRepository.findById(anyString()))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        thrown.expect(CustomException.class);
        thrown.expectMessage(UNAUTHORIZED_SERVER_ERROR_MESSAGE);

        // Act
        userService.getAllUsers("1");
    }

    @Test()
    public void getAllUsers_whenUserIsNotValid_throwException() throws Exception {
        // Arrange
        List<User> users = UsersUtils.getUsers(2);
        UserRole adminRole = RolesUtils.createUserRole();

        users.get(0).getAuthorities().add(adminRole);
        users.get(1).getAuthorities().add(adminRole);

        userList.addAll(users);

        when(mockUserRepository.findById(anyString()))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(false);

        thrown.expect(Exception.class);
        thrown.expectMessage(SERVER_ERROR_MESSAGE);

        // Act
        userService.getAllUsers("1");
    }

    @Test
    public void createUser_whenUserServiceModelIsValidAndThisIsFirstUserInDatabase_createUser() throws Exception {
        // Arrange
        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, null).get(0);

        UserRole rootRole = RolesUtils.createRootRole();

        User user = UsersUtils.createUser();

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(true);

        when(mockRoleRepository.findByAuthority("ROOT")).thenReturn(rootRole);

        when(mockUserRepository.findAll()).thenReturn(new ArrayList<>());

        when(mockUserRepository.saveAndFlush(any())).thenReturn(user);

        // Act
        userService.createUser(userServiceModel);

        // Assert
        verify(mockUserRepository).saveAndFlush(any());
        verify(mockUserRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void createUser_whenUserServiceModelIsValidThisIsNotFirstUserInDatabase_createUser() throws Exception {
        // Arrange
        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, null).get(0);

        UserRole userRole = RolesUtils.createUserRole();

        User user = UsersUtils.createUser();

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(true);

        when(mockRoleRepository.findByAuthority("USER")).thenReturn(userRole);

        when(mockUserRepository.findAll()).thenReturn(List.of(user));

        when(mockUserRepository.saveAndFlush(any())).thenReturn(user);

        // Act
        userService.createUser(userServiceModel);

        // Assert
        verify(mockUserRepository).saveAndFlush(any());
        verify(mockUserRepository, times(1)).saveAndFlush(any());
    }

    @Test
    public void createUser_whenUserServiceModelIsNotValid_throwException() throws Exception {
        // Arrange
        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, null).get(0);

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(false);

        thrown.expect(Exception.class);
        thrown.expectMessage(SERVER_ERROR_MESSAGE);

        // Act
        userService.createUser(userServiceModel);
    }

    @Test
    public void createUser_whenSavedUserInDatabaseIsNull_throwException() throws Exception {
        // Arrange
        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, null).get(0);

        UserRole rootRole = RolesUtils.createRootRole();
        UserRole userRole = RolesUtils.createUserRole();

        User user = UsersUtils.createUser();

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(true);

        when(mockRoleRepository.findByAuthority("ROOT")).thenReturn(rootRole);
        when(mockRoleRepository.findByAuthority("USER")).thenReturn(userRole);

        when(mockUserRepository.findAll()).thenReturn(new ArrayList<>());

        when(mockUserRepository.saveAndFlush(any())).thenReturn(null);

        thrown.expect(Exception.class);
        thrown.expectMessage(SERVER_ERROR_MESSAGE);

        // Act
        userService.createUser(userServiceModel);
    }

    @Test
    public void updateUser_whenUserServiceModelAndLoggedInUserIdAreValid_updateUser() throws Exception {
        // Arrange
        UserRole rootRole = RolesUtils.createRootRole();
        UserRole userRole = RolesUtils.createUserRole();

        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, rootRole).get(0);

        List<User> users = UsersUtils.getUsers(2);

        users.get(0).getAuthorities().add(rootRole);
        users.get(1).getAuthorities().add(userRole);

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(true);

        when(mockUserRepository.findById("1"))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserRepository.findById("2"))
                .thenReturn(java.util.Optional.of(users.get(1)));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        // Act
        userService.updateUser(userServiceModel, "1");

        // Assert
        verify(mockUserRepository).save(any());
        verify(mockUserRepository, times(1)).save(any());
    }

    @Test
    public void updateUser_whenAllParametersAreValidAndLoggedInUserIsUserToEditAndHasRoleUser_updateUser() throws Exception {
        // Arrange
        UserRole userRole = RolesUtils.createUserRole();

        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, userRole).get(0);

        List<User> users = UsersUtils.getUsers(2);

        users.get(0).getAuthorities().add(userRole);

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(true);

        when(mockUserRepository.findById("1"))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        // Act
        userService.updateUser(userServiceModel, "1");

        // Assert
        verify(mockUserRepository).save(any());
        verify(mockUserRepository, times(1)).save(any());
    }

    @Test
    public void updateUser_whenAllParametersAreValidAndLoggedInUserIsUserToEditAndHasRoleAdmin_updateUser() throws Exception {
        // Arrange
        UserRole adminRole = RolesUtils.createAdminRole();

        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, adminRole).get(0);

        List<User> users = UsersUtils.getUsers(2);

        users.get(0).getAuthorities().add(adminRole);

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(true);

        when(mockUserRepository.findById("1"))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        // Act
        userService.updateUser(userServiceModel, "1");

        // Assert
        verify(mockUserRepository).save(any());
        verify(mockUserRepository, times(1)).save(any());
    }

    @Test
    public void updateUser_whenAllParametersAreValidAndLoggedInUserIsUserToEditAndHasRoleRoot_updateUser() throws Exception {
        // Arrange
        UserRole adminRole = RolesUtils.createAdminRole();

        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, adminRole).get(0);

        List<User> users = UsersUtils.getUsers(2);

        users.get(0).getAuthorities().add(adminRole);

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(true);

        when(mockUserRepository.findById("1"))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        // Act
        userService.updateUser(userServiceModel, "1");

        // Assert
        verify(mockUserRepository).save(any());
        verify(mockUserRepository, times(1)).save(any());
    }

    @Test
    public void updateUser_whenUserServiceModelIsNotValid_throwException() throws Exception {
        // Arrange
        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, null).get(0);

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(false);

        thrown.expect(Exception.class);
        thrown.expectMessage(SERVER_ERROR_MESSAGE);

        // Act
        userService.updateUser(userServiceModel, "1");
    }

    @Test
    public void updateUser_whenLoggedInUserIdIsNotValid_throwException()throws Exception {
        // Arrange
        UserRole rootRole = RolesUtils.createRootRole();
        UserRole userRole = RolesUtils.createUserRole();

        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, rootRole).get(0);

        List<User> users = UsersUtils.getUsers(2);

        users.get(0).getAuthorities().add(rootRole);
        users.get(1).getAuthorities().add(userRole);

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(true);

        when(mockUserRepository.findById("1"))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserRepository.findById("2"))
                .thenReturn(java.util.Optional.of(users.get(1)));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(false);

        thrown.expect(Exception.class);
        thrown.expectMessage(SERVER_ERROR_MESSAGE);

        // Act
        userService.updateUser(userServiceModel, "5");

        // Assert
        verify(mockUserRepository).save(any());
        verify(mockUserRepository, times(1)).save(any());
    }

    @Test
    public void updateUser_whenLoggedInUserHasRootRole_updateUser()throws Exception {
        // Arrange
        UserRole rootRole = RolesUtils.createRootRole();
        UserRole userRole = RolesUtils.createUserRole();

        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, userRole).get(0);

        List<User> users = UsersUtils.getUsers(2);

        users.get(0).getAuthorities().add(userRole);
        users.get(1).getAuthorities().add(rootRole);

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(true);

        when(mockUserRepository.findById("1"))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserRepository.findById("2"))
                .thenReturn(java.util.Optional.of(users.get(1)));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        // Act
        userService.updateUser(userServiceModel, "2");

        // Assert
        verify(mockUserRepository).save(any());
        verify(mockUserRepository, times(1)).save(any());
    }

    @Test
    public void updateUser_whenLoggedInUserHasAdminRole_updateUser()throws Exception {
        // Arrange
        UserRole userRole = RolesUtils.createUserRole();
        UserRole adminRole = RolesUtils.createAdminRole();

        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, userRole).get(0);

        List<User> users = UsersUtils.getUsers(2);

        users.get(0).getAuthorities().add(userRole);
        users.get(1).getAuthorities().add(adminRole);

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(true);

        when(mockUserRepository.findById("1"))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserRepository.findById("2"))
                .thenReturn(java.util.Optional.of(users.get(1)));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        // Act
        userService.updateUser(userServiceModel, "2");

        // Assert
        verify(mockUserRepository).save(any());
        verify(mockUserRepository, times(1)).save(any());
    }

    @Test
    public void updateUser_whenLoggedInUserHasUserRole_throwException()throws Exception {
        // Arrange
        UserRole userRole = RolesUtils.createUserRole();

        UserServiceModel userServiceModel = UsersUtils.getUserServiceModels(1, userRole).get(0);

        List<User> users = UsersUtils.getUsers(2);

        users.get(0).getAuthorities().add(userRole);
        users.get(1).getAuthorities().add(userRole);

        when(mockUserValidationService.isValid(any(UserServiceModel.class)))
                .thenReturn(true);

        when(mockUserRepository.findById("1"))
                .thenReturn(java.util.Optional.of(users.get(0)));

        when(mockUserRepository.findById("2"))
                .thenReturn(java.util.Optional.of(users.get(1)));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        thrown.expect(CustomException.class);
        thrown.expectMessage(UNAUTHORIZED_SERVER_ERROR_MESSAGE);

        // Act
        userService.updateUser(userServiceModel, "2");

        // Assert
        verify(mockUserRepository).save(any());
        verify(mockUserRepository, times(1)).save(any());
    }

    @Test
    public void getById_whenUserIdIsValid_returnUser() throws Exception {
        // Arrange
        User user = UsersUtils.createUser();

        when(mockUserRepository.findById(anyString()))
                .thenReturn(java.util.Optional.of(user));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        // Act
        userService.getById("1");

        // Assert
        verify(mockUserRepository).findById(any());
        verify(mockUserRepository, times(1)).findById(any());
    }

    @Test
    public void getById_whenUserIdIsNotValid_throwException() throws Exception {
        // Arrange
        when(mockUserRepository.findById(anyString()))
                .thenReturn(null);

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(false);

        thrown.expect(Exception.class);

        // Act
        userService.getById(anyString());
    }

    @Test
    public void editById_whenUserIdIsValid_returnUser() throws Exception {
        // Arrange
        User user = UsersUtils.createUser();

        when(mockUserRepository.findById(anyString()))
                .thenReturn(java.util.Optional.of(user));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        // Act
        userService.editById("1");

        // Assert
        verify(mockUserRepository).findById(any());
        verify(mockUserRepository, times(1)).findById(any());
    }

    @Test
    public void editById_whenUserIdIsNotValid_throwException() throws Exception {
        // Arrange
        when(mockUserRepository.findById(anyString()))
                .thenReturn(null);

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(false);

        thrown.expect(Exception.class);

        // Act
        userService.editById(anyString());
    }

    @Test
    public void getByEmailValidation_whenEmailIsValid_returnUser() throws Exception {
        // Arrange
        User user = UsersUtils.createUser();

        when(mockUserRepository.findByEmail(anyString()))
                .thenReturn(user);

        // Act
        User actualUser = userService.getByEmailValidation("e-mail");

        // Assert
        assertNotNull(actualUser);

        verify(mockUserRepository).findByEmail(any());
        verify(mockUserRepository, times(1)).findByEmail(any());
    }

    @Test
    public void getByEmailValidation_whenEmailIsNotValid_returnNull() throws Exception {
        // Arrange
        when(mockUserRepository.findByEmail(anyString()))
                .thenReturn(null);

        // Act
        User user = userService.getByEmailValidation(anyString());

        // Assert
        assertNull(user);

        verify(mockUserRepository).findByEmail(any());
        verify(mockUserRepository, times(1)).findByEmail(anyString());
    }

    @Test
    public void getByUsernameValidation_whenUsernameIsValid_returnUser() throws Exception {
        // Arrange
        User user = UsersUtils.createUser();

        when(mockUserRepository.findByUsername(anyString()))
                .thenReturn(java.util.Optional.of(user));

        // Act
        User actualUser = userService.getByUsernameValidation("pesho");

        // Assert
        assertNotNull(actualUser);

        verify(mockUserRepository).findByUsername(any());
        verify(mockUserRepository, times(1)).findByUsername(any());
    }

    @Test
    public void getByUsernameValidation_whenUserNameIsNotValid_returnNull() throws Exception {
        // Act
        User user = userService.getByUsernameValidation("invalid_username");

        // Assert
        assertNull(user);

        verify(mockUserRepository).findByUsername(any());
        verify(mockUserRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void deleteUserById_whenUserIdIsValid_deleteUser() throws Exception {
        // Arrange
        User user = UsersUtils.createUser();

        when(mockUserRepository.findById(anyString()))
                .thenReturn(java.util.Optional.of(user));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(true);

        // Act
        userService.deleteUserById("userId");

        // Assert
        verify(mockUserRepository).deleteById(anyString());
        verify(mockUserRepository, times(1)).deleteById(anyString());
    }

    @Test
    public void deleteUserById_whenUserIdIsNotValid_throwException() throws Exception {
        // Arrange
        User user = UsersUtils.createUser();

        when(mockUserRepository.findById(anyString()))
                .thenReturn(java.util.Optional.of(user));

        when(mockUserValidationService.isValid(any(User.class)))
                .thenReturn(false);

        thrown.expect(Exception.class);

        // Act
        userService.deleteUserById("invalid_id");
    }
}
